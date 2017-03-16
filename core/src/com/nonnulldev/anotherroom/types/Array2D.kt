package com.nonnulldev.anotherroom.types

import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.data.DungeonTile

fun array2dOfDungeonTiles(sizeOuter: Int, sizeInner: Int): Array<Array<DungeonTile>>
        = Array(sizeOuter) { Array(sizeInner){ DungeonTile() } }

fun <T> loopDungeon(dungeon: Dungeon, body: (x: Int, y: Int) -> T) {
    for (x in 0..dungeon.grid.lastIndex) {
        for (y in 0..dungeon.grid[x].lastIndex) {
            body(x, y)
        }
    }
}





