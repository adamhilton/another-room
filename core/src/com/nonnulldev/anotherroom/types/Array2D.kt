package com.nonnulldev.anotherroom.types

import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.data.DungeonTile
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.system.passive.generation.RegionConnectorSystem

fun array2dOfDungeonTiles(sizeOuter: Int, sizeInner: Int): Array<Array<DungeonTile>>
        = Array(sizeOuter) { Array(sizeInner){ DungeonTile() } }

fun array2dOfRegionTiles(sizeOuter: Int, sizeInner: Int): Array<Array<RegionConnectorSystem.RegionTile>>
        = Array(sizeOuter) { Array(sizeInner){ RegionConnectorSystem.RegionTile(0, DungeonTileTypes.Earth) } }


fun <T> Array<Array<DungeonTile>>.loop(body: (x: Int, y: Int) -> T) {
    for (x in 0..this.lastIndex) {
        for (y in 0..this.lastIndex) {
            body(x, y)
        }
    }
}





