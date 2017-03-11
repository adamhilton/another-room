package com.nonnulldev.anotherroom.enum

import java.util.*

enum class DIRECTION{
    NORTH,
    SOUTH,
    EAST,
    WEST;

    companion object {
        private val random = Random()

        fun random(): DIRECTION {
            val directions: Array<DIRECTION> = DIRECTION.values()
            return directions[random.nextInt(directions.size)]
        }
    }
}