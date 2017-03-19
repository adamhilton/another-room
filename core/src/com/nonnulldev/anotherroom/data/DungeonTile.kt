package com.nonnulldev.anotherroom.data

import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.enum.Orientation

data class DungeonTile(var regionId: Int = 0,
                       var type: DungeonTileTypes = DungeonTileTypes.Earth,
                       var orientation: Orientation = Orientation.HORIZONTAL)
