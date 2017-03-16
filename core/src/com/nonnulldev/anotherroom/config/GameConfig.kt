package com.nonnulldev.anotherroom.config

class GameConfig private constructor(){

    companion object {
        val WIDTH = 800 // pixels
        val HEIGHT = 480 // pixels

        val WORLD_WIDTH = 27f // world units
        val WORLD_HEIGHT = 27f // world units

        val WORLD_CENTER_X = WORLD_WIDTH / 2f // world units
        val WORLD_CENTER_Y = WORLD_HEIGHT / 2f // world units

        val ROOM_CREATION_ATTEMPTS = 1000
        val REGION_MERGING_ATTEMPTS = 1000

        val SMALL_ROOM_DIMENSION = 3f
        val MEDIUM_ROOM_DIMENSION = 5f
        val LARGE_ROOM_DIMENSION = 7f

        val WALL_SIZE = 1f
        val PATH_SIZE = 1f

        val ROOM_TO_ROOM_BUFFER = WALL_SIZE + 2f
        val ROOM_TO_EDGE_OF_MAP_BUFFER = (WALL_SIZE ) + PATH_SIZE

        val DOOR_SIZE = 1f
        val DOOR_HALF_SIZE = DOOR_SIZE / 2f
    }
}
