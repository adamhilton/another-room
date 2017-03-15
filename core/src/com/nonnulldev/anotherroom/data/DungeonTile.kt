package com.nonnulldev.anotherroom.data

import com.nonnulldev.anotherroom.enum.DungeonTileTypes

data class DungeonTile(var regionId: Int = 0, var type: DungeonTileTypes = DungeonTileTypes.Earth)
