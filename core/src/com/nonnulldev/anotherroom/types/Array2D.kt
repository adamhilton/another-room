package com.nonnulldev.anotherroom.types

import com.nonnulldev.anotherroom.data.DungeonTile

fun array2dOfDungeonTiles(sizeOuter: Int, sizeInner: Int): Array<Array<DungeonTile>>
        = Array(sizeOuter) { Array(sizeInner){ DungeonTile() } }




