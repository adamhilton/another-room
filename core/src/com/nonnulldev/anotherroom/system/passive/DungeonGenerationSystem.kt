package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.enum.Direction
import com.nonnulldev.anotherroom.enum.DungeonTiles
import com.nonnulldev.anotherroom.types.array2dOfDungeonTiles
import java.util.*

class DungeonGenerationSystem : EntitySystem() {

    private var dungeon = array2dOfDungeonTiles(GameConfig.WORLD_HEIGHT.toInt(), GameConfig.WORLD_WIDTH.toInt())

    private lateinit var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        val centerRoom = centerRoom(GameConfig.SMALL_ROOM_DIMENSION.toInt(), GameConfig.SMALL_ROOM_DIMENSION.toInt())

        val rooms = ArrayList<Room>()

        for (numOfRoomGenerationAttempts in 0..GameConfig.ROOM_CREATION_ATTEMPTS) {
            val randomRoom = createRandomRoom(
                    GameConfig.SMALL_ROOM_DIMENSION.toInt(),
                    GameConfig.SMALL_ROOM_DIMENSION.toInt()
            )
            rooms.add(randomRoom)
        }

        addRoom(centerRoom)
        rooms.forEach {
            if (canBuildRoom(it)) {
                addRoom(it)
            }
        }

        generatePaths(centerRoom.coordinates.x - 2, centerRoom.coordinates.y - 2)

        for (x in 0..dungeon.lastIndex) {
            for (y in 0..dungeon[x].lastIndex) {

                var tile = dungeon[x][y]

                val position = positionComponent(x.toFloat(), y.toFloat())
                val dimension = dimensionComponent(1f, 1f)
                val bounds = boundsComponent(position, dimension)

                if (tile == DungeonTiles.Earth) {
                    bounds.color = Color.CLEAR
                } else if (tile == DungeonTiles.Room) {
                    bounds.color = Color.BLUE
                } else if (tile == DungeonTiles.Path) {
                    bounds.color = Color.GOLD
                }

                val entity = engine.createEntity()
                entity.add(position)
                entity.add(dimension)
                entity.add(bounds)

                engine.addEntity(entity)
            }
        }
    }

    private fun generatePaths(x: Int, y: Int) {
            visitNeighbor( x-1, y, Direction.WEST)
            visitNeighbor( x+1, y, Direction.EAST)
            visitNeighbor( x, y-1, Direction.SOUTH)
            visitNeighbor( x, y+1, Direction.NORTH)
    }

    private fun visitNeighbor (x: Int, y: Int, direction: Direction) {
        if (x < GameConfig.WALL_SIZE || x >= GameConfig.WORLD_WIDTH - GameConfig.WALL_SIZE)
            return

        if (y < GameConfig.WALL_SIZE || y >= GameConfig.WORLD_HEIGHT - GameConfig.WALL_SIZE)
            return

        if (dungeon[x][y] != DungeonTiles.Earth ) {
            return
        }

        if (!enoughSpaceAhead(x, y, direction)) {
            return
        }

        dungeon[x][y] = DungeonTiles.Path

        generatePaths(x, y)
    }

    private fun enoughSpaceAhead(x: Int, y: Int, direction: Direction): Boolean {

        var spacesToCheck = ArrayList<Coordinates>()

        if (direction == Direction.NORTH) {
            spacesToCheck.add(northCoodinates(x, y))
            spacesToCheck.add(northEastCoordinates(x, y))
            spacesToCheck.add(northWestCoodinates(x, y))
            spacesToCheck.add(eastCoodinates(x, y))
            spacesToCheck.add(westCoodinates(x, y))
        } else if (direction == Direction.SOUTH) {
            spacesToCheck.add(southCoodinates(x, y))
            spacesToCheck.add(southEastCoodinates(x, y))
            spacesToCheck.add(southWestCoodinates(x, y))
            spacesToCheck.add(eastCoodinates(x, y))
            spacesToCheck.add(westCoodinates(x, y))
        } else if (direction == Direction.EAST) {
            spacesToCheck.add(eastCoodinates(x, y))
            spacesToCheck.add(southEastCoodinates(x, y))
            spacesToCheck.add(northEastCoordinates(x, y))
            spacesToCheck.add(northCoodinates(x, y))
            spacesToCheck.add(southCoodinates(x, y))
        } else if (direction == Direction.WEST) {
            spacesToCheck.add(westCoodinates(x, y))
            spacesToCheck.add(southWestCoodinates(x, y))
            spacesToCheck.add(northWestCoodinates(x, y))
            spacesToCheck.add(northCoodinates(x, y))
            spacesToCheck.add(southCoodinates(x, y))
        }

        spacesToCheck.forEach {
            if (dungeon[it.x][it.y] != DungeonTiles.Earth) {
                return false
            }
        }

        return true
    }

    fun northCoodinates(x: Int, y: Int): Coordinates {
        return Coordinates(x, y + 1)
    }

    fun northEastCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y + 1)
    }

    fun southCoodinates(x: Int, y: Int): Coordinates {
        return Coordinates(x, y - 1)
    }

    fun southEastCoodinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y - 1)
    }

    fun eastCoodinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y)
    }

    fun northWestCoodinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y + 1)
    }

    fun westCoodinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y)
    }

    fun southWestCoodinates(x: Int, y: Int): Coordinates {
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

    private fun createRandomRoom(roomWidth: Int, roomHeight: Int): Room {
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
        for (x in 0..dungeon.lastIndex) {
            for (y in 0..dungeon[x].lastIndex) {
                body(x, y)
            }
        }
    }

    private fun addRoom(room: Room) {
        for (roomX in 0..room.dimension.width - 1) {
            for (roomY in 0..room.dimension.height - 1)
                dungeon[room.coordinates.x + roomX][room.coordinates.y + roomY] = DungeonTiles.Room
        }
    }

    private fun canBuildRoom(room: Room): Boolean {
        for (roomX in 0..room.dimension.width + 2) {
            for (roomY in 0..room.dimension.height + 2) {
                var tile = dungeon[room.coordinates.x + roomX - 2][room.coordinates.y + roomY - 2]
                if (tile != DungeonTiles.Earth) {
                    return false
                }
            }
        }
        return true
    }

    data class Room(val coordinates: Coordinates, val dimension: Dimension)

    data class Dimension(val width: Int, val height: Int)

    data class Coordinates(val x: Int, val y: Int)
}
