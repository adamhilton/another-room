package com.nonnulldev.anotherroom.dungeon.data

import com.nonnulldev.anotherroom.common.data.Coordinates
import com.nonnulldev.anotherroom.common.data.Dimension

data class Room(val coordinates: Coordinates, val dimension: Dimension) {
    val tiles: ArrayList<Coordinates>
    get() {
        val roomTiles = ArrayList<Coordinates>()
        val coordinates = Coordinates(-1, -1)
        for (x in coordinates.x..(coordinates.x + dimension.width)) {
            for (y in coordinates.y..(coordinates.y + dimension.height)) {
                coordinates.set(x, y)
                roomTiles.add(coordinates)
            }
        }
        return roomTiles
    }
}

