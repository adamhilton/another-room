package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.*
import com.nonnulldev.anotherroom.enum.Direction
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.enum.RoomSize
import com.nonnulldev.anotherroom.types.array2dOfDungeonTiles
import java.util.*
import java.util.ArrayList
import kotlin.collections.HashMap


class DungeonGenerationSystem : EntitySystem() {

    private var dungeon = Dungeon(array2dOfDungeonTiles(GameConfig.WORLD_HEIGHT.toInt(), GameConfig.WORLD_WIDTH.toInt()))

    private lateinit var engine: PooledEngine

    private var regions = ArrayList<Int>()
    private var mergedRegions = HashMap<Int, Int>()

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        init(engine)
    }

    // TODO: uncomment when done using input processing to make this a static system
//    override fun checkProcessing(): Boolean {
//        return false
//    }

    private fun init(engine: PooledEngine) {
//        val centerRoom = centerRoom(GameConfig.SMALL_ROOM_DIMENSION.toInt(), GameConfig.SMALL_ROOM_DIMENSION.toInt())

        val rooms = ArrayList<Room>()

        for (numOfRoomGenerationAttempts in 0..GameConfig.ROOM_CREATION_ATTEMPTS) {
            val randomRoom = createRandomRoom()
            rooms.add(randomRoom)
        }

//        addRoom(centerRoom)
        rooms.forEach {
            if (canBuildRoom(it)) {
                addRoom(it)
            }
        }

        loopDungeon { x, y ->
            val regionId = regions.size + 1
            var tileIsEarth = dungeon.grid[x][y].type == DungeonTileTypes.Earth
            if (tileIsEarth && isWithinBounds(x, y) && spaceInAnyDirectionForPath(x, y)) {
                generatePaths(x, y, regionId)
            }
        }


        // TODO: change to a finite number of tries and then if fails then rerun whole dungeon generation
        val canPlaceConnector = Random()
        while(!allRegionsAreMerged()) {
            loopDungeon { x, y ->
                val tile = dungeon.grid[x][y]
                if (tile.type == DungeonTileTypes.Earth && isWithinBounds(x, y)) {
                    // TODO: connectors are placed too predictably. Find a better way to place connectors on all sides of room
                    if(canPlaceConnector.nextBoolean())
                    placeConnector(x, y)
                }
            }
        }

        for (x in 0..dungeon.grid.lastIndex) {
            for (y in 0..dungeon.grid[x].lastIndex) {

                var tile = dungeon.grid[x][y]

                val position = positionComponent(x.toFloat(), y.toFloat())
                val dimension = dimensionComponent(1f, 1f)
                val bounds = boundsComponent(position, dimension)

                if (tile.type == DungeonTileTypes.Earth) {
                    bounds.color = Color.CLEAR
                } else if (tile.type == DungeonTileTypes.Room) {
                    bounds.color = Color.BLUE
                } else if (tile.type == DungeonTileTypes.Path) {
                    bounds.color = Color.GOLD
                } else if (tile.type == DungeonTileTypes.Door) {
                    bounds.color = Color.GREEN
                }

                val entity = engine.createEntity()
                entity.add(position)
                entity.add(dimension)
                entity.add(bounds)

                engine.addEntity(entity)
            }
        }
    }

    private fun allRegionsAreMerged(): Boolean {
        regions.forEach {
            if (!mergedRegions.containsValue(it) && !mergedRegions.containsKey(it)) {
                return false
            }
        }
        return true
    }

    private fun placeConnector(x: Int, y: Int) {
        val tileToNorth = dungeon.grid[x][y + 1]
        val tileToSouth = dungeon.grid[x][y - 1]
        val tileToEast = dungeon.grid[x + 1][y]
        val tileToWest = dungeon.grid[x - 1][y]

        if(tileTypesCanHaveConnector(tileToNorth, tileToSouth)) {
            mergedRegions[tileToNorth.regionId] = tileToSouth.regionId
            dungeon.grid[x][y].type = DungeonTileTypes.Door
        } else if (tileTypesCanHaveConnector(tileToEast, tileToWest)) {
            mergedRegions[tileToEast.regionId] = tileToWest.regionId
            dungeon.grid[x][y].type = DungeonTileTypes.Door
        }
    }

    private fun tileTypesCanHaveConnector(firstTile: DungeonTile, secondTile: DungeonTile): Boolean {
        if (firstTile.regionId == secondTile.regionId) {
            return false
        }
        if (regionAlreadyMerged(firstTile.regionId, secondTile.regionId)) {
            return false
        }
        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Room) {
            return true
        }
        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Path) {
            return true
        }
        return false
    }

    private fun regionAlreadyMerged(firstRegionId: Int, secondRegionId: Int): Boolean {
        return (mergedRegions.contains(firstRegionId) || mergedRegions.containsValue(firstRegionId))
                && (mergedRegions.contains(secondRegionId) || mergedRegions.containsValue(secondRegionId))
    }

    override fun update(deltaTime: Float) {
        if(Gdx.input.isKeyPressed(Input.Keys.R)) {
            dungeon = Dungeon(array2dOfDungeonTiles(GameConfig.WORLD_HEIGHT.toInt(), GameConfig.WORLD_WIDTH.toInt()))
            init(engine)
        }
    }

    private fun isWithinBounds(x: Int, y: Int): Boolean {
        return (x > 0 && x <= GameConfig.WORLD_WIDTH - 2f) &&
                (y > 0 && y <= GameConfig.WORLD_HEIGHT -2f)
    }

    private fun spaceInAnyDirectionForPath(x: Int, y: Int): Boolean {
        return enoughSpaceAhead(x, y, Direction.NORTH) ||
                enoughSpaceAhead(x, y, Direction.SOUTH) ||
                enoughSpaceAhead(x, y, Direction.EAST) ||
                enoughSpaceAhead(x, y, Direction.WEST)
    }

    private fun generatePaths(x: Int, y: Int, regionId: Int) {
            visitNeighbor( x-1, y, Direction.WEST, regionId)
            visitNeighbor( x+1, y, Direction.EAST, regionId)
            visitNeighbor( x, y-1, Direction.SOUTH, regionId)
            visitNeighbor( x, y+1, Direction.NORTH, regionId)
    }

    private fun visitNeighbor (x: Int, y: Int, direction: Direction, regionId: Int) {
        if (x < GameConfig.WALL_SIZE || x >= GameConfig.WORLD_WIDTH - GameConfig.WALL_SIZE)
            return

        if (y < GameConfig.WALL_SIZE || y >= GameConfig.WORLD_HEIGHT - GameConfig.WALL_SIZE)
            return

        if (dungeon.grid[x][y].type != DungeonTileTypes.Earth ) {
            return
        }

        if (!enoughSpaceAhead(x, y, direction)) {
            return
        }

        var dungeonTile = dungeon.grid[x][y]
        dungeonTile.regionId = regionId
        dungeonTile.type = DungeonTileTypes.Path

        if (!regions.contains(regionId)) {
            regions.add(regionId)
        }

        generatePaths(x, y, regionId)
    }

    private fun enoughSpaceAhead(x: Int, y: Int, direction: Direction): Boolean {

        var spacesToCheck = ArrayList<Coordinates>()

        if (direction == Direction.NORTH) {
            spacesToCheck.add(northCoordinates(x, y))
            spacesToCheck.add(northEastCoordinates(x, y))
            spacesToCheck.add(northWestCoordinates(x, y))
            spacesToCheck.add(eastCoordinates(x, y))
            spacesToCheck.add(westCoordinates(x, y))
        } else if (direction == Direction.SOUTH) {
            spacesToCheck.add(southCoordinates(x, y))
            spacesToCheck.add(southEastCoordinates(x, y))
            spacesToCheck.add(southWestCoordinates(x, y))
            spacesToCheck.add(eastCoordinates(x, y))
            spacesToCheck.add(westCoordinates(x, y))
        } else if (direction == Direction.EAST) {
            spacesToCheck.add(eastCoordinates(x, y))
            spacesToCheck.add(southEastCoordinates(x, y))
            spacesToCheck.add(northEastCoordinates(x, y))
            spacesToCheck.add(northCoordinates(x, y))
            spacesToCheck.add(southCoordinates(x, y))
        } else if (direction == Direction.WEST) {
            spacesToCheck.add(westCoordinates(x, y))
            spacesToCheck.add(southWestCoordinates(x, y))
            spacesToCheck.add(northWestCoordinates(x, y))
            spacesToCheck.add(northCoordinates(x, y))
            spacesToCheck.add(southCoordinates(x, y))
        }

        spacesToCheck.forEach {
            if (dungeon.grid[it.x][it.y].type != DungeonTileTypes.Earth) {
                return false
            }
        }

        return true
    }

    private fun northCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x, y + 1)
    }

    private fun northEastCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y + 1)
    }

    private fun southCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x, y - 1)
    }

    private fun southEastCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y - 1)
    }

    private fun eastCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y)
    }

    private fun northWestCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y + 1)
    }

    private fun westCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y)
    }

    private fun southWestCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y - 1)
    }

    private fun centerRoom(roomWidth: Int, roomHeight: Int): Room {

        val coordinates = Coordinates(
            GameConfig.WORLD_CENTER_X.toInt() - (roomWidth / 2),
            GameConfig.WORLD_CENTER_Y.toInt() - (roomHeight / 2)
        )

        val dimension = Dimension(
            roomWidth,
            roomHeight
        )

        return Room(coordinates, dimension)
    }

    private fun createRandomRoom(): Room {
        var randomDimension = RoomSize.random()
        val roomWidth = randomDimension.dimension.width
        val roomHeight = randomDimension.dimension.height
        val coordinates = randomPosition(roomWidth, roomHeight)
        val dimension = Dimension(roomWidth, roomHeight)
        return Room(coordinates, dimension)
    }

    private fun positionComponent(x: Float, y: Float): PositionComponent {
        val position = engine.createComponent(PositionComponent::class.java)
        position.x = x
        position.y = y
        return position
    }

    private fun dimensionComponent(width: Float, height: Float): DimensionComponent {
        val dimension = engine.createComponent(DimensionComponent::class.java)
        dimension.width = width
        dimension.height = height
        return dimension
    }

    private fun boundsComponent(position: PositionComponent, dimension: DimensionComponent): BoundsComponent {
        val bounds = engine.createComponent(BoundsComponent::class.java)
        bounds.rectangle.setPosition(position.x, position.y)
        bounds.rectangle.setSize(dimension.width, dimension.height)
        return bounds
    }

    fun randomPosition(width: Int, height: Int): Coordinates {

        val random = Random()

        val maxX = GameConfig.WORLD_WIDTH - width - GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER
        val minX = GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER

        var rectangleX = minX+random.nextInt(((maxX-minX)/2).toInt()) *2

        val maxY = GameConfig.WORLD_HEIGHT - height - GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER
        val minY = GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER

        var rectangleY = minY+random.nextInt(((maxY-minY)/2).toInt()) *2

        return Coordinates(rectangleX.toInt(), rectangleY.toInt())
    }

    fun <T> loopDungeon(body: (x: Int, y: Int) -> T) {
        for (x in 0..dungeon.grid.lastIndex) {
            for (y in 0..dungeon.grid[x].lastIndex) {
                body(x, y)
            }
        }
    }

    private fun addRoom(room: Room) {
        val regionId = regions.size + 1
        regions.add(regionId)
        for (roomX in 0..room.dimension.width - 1) {
            for (roomY in 0..room.dimension.height - 1) {
                val dungeonTile = dungeon.grid[room.coordinates.x + roomX][room.coordinates.y + roomY]
                dungeonTile.regionId = regionId
                dungeonTile.type = DungeonTileTypes.Room
            }
        }
    }

    private fun canBuildRoom(room: Room): Boolean {
        for (roomX in 0..room.dimension.width + 2) {
            for (roomY in 0..room.dimension.height + 2) {
                var tile = dungeon.grid[room.coordinates.x + roomX - 2][room.coordinates.y + roomY - 2]
                if (tile.type != DungeonTileTypes.Earth) {
                    return false
                }
            }
        }
        return true
    }
}
