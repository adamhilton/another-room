package com.nonnulldev.anotherroom.data

import com.badlogic.gdx.math.Vector2

class Dungeon(val grid:Array<Array<DungeonTile>>,
              val regions: ArrayList<Int> = ArrayList(),
              val rooms: ArrayList<Room> = ArrayList())

fun Array<Array<DungeonTile>>.get(coordinates: Coordinates): DungeonTile {
    return this[coordinates.x][coordinates.y]
}