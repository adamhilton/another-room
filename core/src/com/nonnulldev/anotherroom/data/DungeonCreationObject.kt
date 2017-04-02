package com.nonnulldev.anotherroom.data

class DungeonCreationObject(val grid:Array<Array<DungeonTile>>,
                            val regions: ArrayList<Int> = ArrayList(),
                            val rooms: ArrayList<Room> = ArrayList())

fun Array<Array<DungeonTile>>.get(coordinates: Coordinates): DungeonTile {
    return this[coordinates.x][coordinates.y]
}