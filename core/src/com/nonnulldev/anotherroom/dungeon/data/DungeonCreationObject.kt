package com.nonnulldev.anotherroom.dungeon.data

import com.nonnulldev.anotherroom.common.data.Coordinates

class DungeonCreationObject(val grid:Array<Array<DungeonTile>>,
                            val regions: ArrayList<Int> = ArrayList(),
                            val rooms: ArrayList<Room> = ArrayList())

fun Array<Array<DungeonTile>>.get(coordinates: Coordinates): DungeonTile {
    return this[coordinates.x][coordinates.y]
}