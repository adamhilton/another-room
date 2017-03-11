package com.nonnulldev.anotherroom.config

class GameConfig private constructor(){

    companion object {
        val WIDTH = 800 // pixels
        val HEIGHT = 480 // pixels

        val WORLD_WIDTH = 33f // world units
        val WORLD_HEIGHT = 33f // world units

        val WORLD_CENTER_X = WORLD_WIDTH / 2f // world units
        val WORLD_CENTER_Y = WORLD_HEIGHT / 2f // world units

        val PLAYER_SIZE = 1f // world units

        val ROOM_CREATION_ATTEMPTS = 1000

        val SMALL_ROOM_DIMENSION = 3f
        val MEDIUM_ROOM_DIMENSION = 5f
        val LARGE_ROOM_DIMENSION = 7f

        val WALL_SIZE = 1f

        val ROOM_BUFFER = WALL_SIZE + 2f

        val DOOR_SIZE = 1f
        val DOOR_HALF_SIZE = DOOR_SIZE / 2f
    }
}
