package com.nonnulldev.anotherroom.dungeon.types

import com.nonnulldev.anotherroom.common.data.Coordinates
import com.nonnulldev.anotherroom.dungeon.data.DungeonTile
import com.nonnulldev.anotherroom.dungeon.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.dungeon.system.creation.RegionConnectorSystem

fun array2dOfDungeonTiles(sizeOuter: Int, sizeInner: Int): Array<Array<DungeonTile>>
        = Array(sizeOuter) { Array(sizeInner){ DungeonTile() } }

fun array2dOfRegionTiles(sizeOuter: Int, sizeInner: Int): Array<Array<RegionConnectorSystem.RegionTile>>
        = Array(sizeOuter) { Array(sizeInner){ RegionConnectorSystem.RegionTile(0, DungeonTileTypes.Earth) } }


fun <T> Array<Array<DungeonTile>>.loop(body: (Coordinates) -> T) {
    for (x in 0..this.lastIndex) {
        for (y in 0..this.lastIndex) {
            body(Coordinates(x, y))
        }
    }
}





