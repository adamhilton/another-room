package com.nonnulldev.anotherroom.data

class Dungeon(val grid:Array<Array<DungeonTile>>, val regions: ArrayList<Int> = ArrayList())

fun Array<Array<DungeonTile>>.get(coordinates: Coordinates): DungeonTile {
    return this[coordinates.x][coordinates.y]
}