package com.nonnulldev.anotherroom.types

import com.nonnulldev.anotherroom.enum.DungeonTiles

fun array2dOfDungeonTiles(sizeOuter: Int, sizeInner: Int): Array<Array<DungeonTiles>>
        = Array(sizeOuter) { Array<DungeonTiles>(sizeInner){ DungeonTiles.Earth } }



