package com.nonnulldev.anotherroom.system.generation.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.MathUtils
import com.nonnulldev.anotherroom.component.RoomComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.Coordinates
import com.nonnulldev.anotherroom.data.Dimension
import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.data.Room
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.enum.RoomSize
import java.util.*

class RoomGenerationSystem(private val dungeon: Dungeon) : EntitySystem() {

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        val rooms = ArrayList<Room>()

        for (numOfRoomGenerationAttempts in 0..GameConfig.ROOM_CREATION_ATTEMPTS) {
            val randomRoom = createRandomRoom()
            rooms.add(randomRoom)
        }

        rooms.forEach {
            if (canBuildRoom(it)) {
                addRoom(it)
            }
        }
    }

    private fun addRoom(room: Room) {
        val regionId = dungeon.regions.size + 1
        dungeon.regions.add(regionId)
        for (roomX in 0..room.dimension.width - 1) {
            for (roomY in 0..room.dimension.height - 1) {
                val dungeonTile = dungeon.grid[room.coordinates.x + roomX][room.coordinates.y + roomY]
                dungeonTile.regionId = regionId
                dungeonTile.type = DungeonTileTypes.Room
            }
        }
        dungeon.rooms.add(room)
        val roomComponent = (engine as PooledEngine).createComponent(RoomComponent::class.java)
        roomComponent.dimension = room.dimension
        roomComponent.centerX = room.coordinates.x + (room.dimension.width / 2f)
        roomComponent.centerY = room.coordinates.y + (room.dimension.height / 2f)
        val entity = (engine as PooledEngine).createEntity()
        entity.add(roomComponent)
        engine.addEntity(entity)
    }

    private fun canBuildRoom(room: Room): Boolean {
        val roomWidthWithWalls = room.dimension.width + (GameConfig.WALL_SIZE.toInt() * 2)
        val roomHeightWithWalls = room.dimension.height + (GameConfig.WALL_SIZE.toInt() * 2)

        for (roomX in 0..roomWidthWithWalls) {
            for (roomY in 0..roomHeightWithWalls) {
                var tile = dungeon.grid[room.coordinates.x + roomX - (GameConfig.WALL_SIZE.toInt() * 2)][room.coordinates.y + roomY - (GameConfig.WALL_SIZE.toInt() * 2)]
                if (tile.type != DungeonTileTypes.Earth) {
                    return false
                }
            }
        }
        return true
    }

    private fun createRandomRoom(): Room {
        var randomDimension = RoomSize.random()
        val roomWidth = randomDimension.dimension.width
        val roomHeight = randomDimension.dimension.height
        val coordinates = randomPosition(roomWidth, roomHeight)
        val dimension = Dimension(roomWidth, roomHeight)
        return Room(coordinates, dimension)
    }

    fun randomPosition(width: Int, height: Int): Coordinates {
        val maxX = GameConfig.WORLD_WIDTH - width - GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER + 2
        val minX = GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER

        var rectangleX = getOddPosition(minX.toInt(), maxX.toInt())

        val maxY = GameConfig.WORLD_HEIGHT - height - GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER + 2
        val minY = GameConfig.ROOM_TO_EDGE_OF_MAP_BUFFER

        var rectangleY = getOddPosition(minY.toInt(), maxY.toInt())

        return Coordinates(rectangleX, rectangleY)
    }

    fun getOddPosition(min: Int, max: Int): Int {
        while(true) {
            val num = MathUtils.random(min, max)
            if (num % 2 != 0) {
                return num
            }
        }
    }
}