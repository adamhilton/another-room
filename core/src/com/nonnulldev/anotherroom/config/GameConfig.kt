package com.nonnulldev.anotherroom.config

class GameConfig private constructor(){

    companion object {
        val WIDTH = 800f // pixels
        val HEIGHT = 480f // pixels

        val WOLRD_WIDTH = 25f // world units
        val WORLD_HEIGHT = 15f // world units

        val WORLD_CENTER_X = WOLRD_WIDTH / 2f // world units
        val WORLD_CENTER_Y = WORLD_HEIGHT / 2f // world units

        val PLAYER_SIZE = 1f // world units
    }
}
