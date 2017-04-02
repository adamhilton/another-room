package com.nonnulldev.anotherroom.dungeon.enum

import com.nonnulldev.anotherroom.common.data.Dimension
import java.util.*


enum class RoomSize(val dimension: Dimension) {
    SMALL(Dimension(3, 3)),
    SMALL_MEDIUM(Dimension(3, 5)),
    SMALL_LARGE(Dimension(3, 7)),
    MEDIUM(Dimension(5, 5)),
    MEDIUM_SMALL(Dimension(5, 3)),
    MEDIUM_LARGE(Dimension(5, 7)),
    LARGE(Dimension(7, 7)),
    HUGE(Dimension(9, 9));

    companion object {
        private val random = Random()

        fun random(): RoomSize {
            val roomSizes: Array<RoomSize> = values()
            return roomSizes[random.nextInt(roomSizes.size)]
        }
    }
}
