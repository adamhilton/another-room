package com.nonnulldev.anotherroom.common.enum

import java.util.*

enum class Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    companion object {
        private val random = Random()

        fun random(): Direction {
            val directions: Array<Direction> = Direction.values()
            return directions[random.nextInt(directions.size)]
        }
    }
}