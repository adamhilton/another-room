package com.nonnulldev.anotherroom.config

class GameConfig private constructor(){

    companion object {
        val WIDTH = 800f // pixels
        val HEIGHT = 480f // pixels

        val WORLD_WIDTH = 37f // world units
        val WORLD_HEIGHT = 37f // world units

        val WORLD_CENTER_X = WORLD_WIDTH / 2f // world units
        val WORLD_CENTER_Y = WORLD_HEIGHT / 2f // world units

        val PLAYER_SIZE = 1f // world units

        val ROOM_TILE_SIZE = 1f
        val MEDIUM_ROOM_DIMENSION = 7f

        val WALL_SIZE = 1f

        val ROOM_BUFFER = WALL_SIZE + 2f
    }
}
