package com.nonnulldev.anotherroom.dungeon.data

import com.nonnulldev.anotherroom.dungeon.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.common.enum.Orientation

data class DungeonTile(var regionId: Int = 0,
                       var type: DungeonTileTypes = DungeonTileTypes.Earth,
                       var orientation: Orientation = Orientation.HORIZONTAL) {

    companion object {
        @JvmStatic
        val INVALID_REGION_ID = 0
    }
}