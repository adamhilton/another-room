package com.nonnulldev.anotherroom.system.passive.generation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.types.loopDungeon

class PathCleanupSystem(private val dungeon: Dungeon) : EntitySystem() {

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        loopDungeon(dungeon, { x, y ->
            val tile = dungeon.grid[x][y]
            if (tile.type == DungeonTileTypes.Path && isDeadEnd(x, y)) {
                removePath(x, y)
            }
        })
    }

    private fun isDeadEnd(x: Int, y: Int): Boolean {
        val tileToNorthIsPath = dungeon.grid[x][y + 1].type == DungeonTileTypes.Path
        val tileToSouthIsPath = dungeon.grid[x][y - 1].type == DungeonTileTypes.Path
        val tileToEastIsPath = dungeon.grid[x + 1][y].type == DungeonTileTypes.Path
        val tileToWestIsPath = dungeon.grid[x - 1][y].type == DungeonTileTypes.Path

        val tileToNorthIsDoor = dungeon.grid[x][y + 1].type == DungeonTileTypes.Door
        val tileToSouthIsDoor = dungeon.grid[x][y - 1].type == DungeonTileTypes.Door
        val tileToEastIsDoor = dungeon.grid[x + 1][y].type == DungeonTileTypes.Door
        val tileToWestIsDoor = dungeon.grid[x - 1][y].type == DungeonTileTypes.Door

        var neighbors = 0
        neighbors += if (tileToNorthIsPath || tileToNorthIsDoor) 1 else 0
        neighbors += if (tileToSouthIsPath || tileToSouthIsDoor) 1 else 0
        neighbors += if (tileToEastIsPath || tileToEastIsDoor) 1 else 0
        neighbors += if (tileToWestIsPath || tileToWestIsDoor) 1 else 0

        return neighbors <= 1
    }

    private fun removePath(x: Int, y: Int) {

        if (x < GameConfig.WALL_SIZE || x >= GameConfig.WORLD_WIDTH - GameConfig.WALL_SIZE)
            return

        if (y < GameConfig.WALL_SIZE || y >= GameConfig.WORLD_HEIGHT - GameConfig.WALL_SIZE)
            return

        if (dungeon.grid[x][y].type != DungeonTileTypes.Path || !isDeadEnd(x, y)) {
            return
        }

        var dungeonTile = dungeon.grid[x][y]
        dungeonTile.regionId = 0
        dungeonTile.type = DungeonTileTypes.Earth

        removePath( x-1, y)
        removePath( x+1, y)
        removePath( x, y-1)
        removePath( x, y+1)
    }
}
