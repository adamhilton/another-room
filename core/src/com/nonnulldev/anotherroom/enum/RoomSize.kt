package com.nonnulldev.anotherroom.enum

import com.nonnulldev.anotherroom.config.GameConfig.Companion.LARGE_ROOM_DIMENSION
import com.nonnulldev.anotherroom.config.GameConfig.Companion.MEDIUM_ROOM_DIMENSION
import com.nonnulldev.anotherroom.config.GameConfig.Companion.SMALL_ROOM_DIMENSION
import com.nonnulldev.anotherroom.data.Dimension
import java.util.*


enum class RoomSize(val dimension: Dimension) {
    SMALL(Dimension(SMALL_ROOM_DIMENSION.toInt(), MEDIUM_ROOM_DIMENSION.toInt())),
    MEDIUM(Dimension(MEDIUM_ROOM_DIMENSION.toInt(), MEDIUM_ROOM_DIMENSION.toInt())),
    LARGE(Dimension(LARGE_ROOM_DIMENSION.toInt(), MEDIUM_ROOM_DIMENSION.toInt()));

    companion object {
        private val random = Random()

        fun random(): RoomSize {
            val roomSizes: Array<RoomSize> = RoomSize.values()
            return roomSizes[random.nextInt(roomSizes.size)]
        }
    }
}
