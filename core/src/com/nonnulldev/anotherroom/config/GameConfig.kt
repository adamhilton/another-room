package com.nonnulldev.anotherroom.config

class GameConfig private constructor(){

    companion object {
        val WIDTH = 800 // pixels
        val HEIGHT = 480 // pixels

        val WORLD_WIDTH = 17f // world units
        val WORLD_HEIGHT = 17f // world units

        val WORLD_CENTER_X = WORLD_WIDTH / 2f // world units
        val WORLD_CENTER_Y = WORLD_HEIGHT / 2f // world units

        val ROOM_CREATION_ATTEMPTS = 1000
        val REGION_MERGING_ATTEMPTS = 1000

        val WALL_SIZE = 1f
        val PATH_SIZE = 1f

        val ROOM_TO_EDGE_OF_MAP_BUFFER = (WALL_SIZE ) + PATH_SIZE

        val DOOR_SIZE = 1f
    }
}
