package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
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

        val centerRoom = centerRoom(GameConfig.SMALL_ROOM_DIMENSION.toInt(), GameConfig.SMALL_ROOM_DIMENSION.toInt())
        rooms.add(centerRoom)

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

    private fun createRandomRoom(): Room {
        var randomDimension = RoomSize.random()
        val roomWidth = randomDimension.dimension.width
        val roomHeight = randomDimension.dimension.height
        val coordinates = randomPosition(roomWidth, roomHeight)
        val dimension = Dimension(roomWidth, roomHeight)
        return Room(coordinates, dimension)
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
}