package com.nonnulldev.anotherroom.system.passive.generation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.Coordinates
import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.data.DungeonTile
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.extension.isWithinWorldBounds
import com.nonnulldev.anotherroom.types.loopDungeon
import java.util.*

class RegionConnectorSystem(private val dungeon: Dungeon, private val listener: Listener) : EntitySystem() {

    private val log = Logger(RegionConnectorSystem::class.simpleName, Logger.DEBUG)

    private var mergedRegions = HashMap<Int, Int>()

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        var attemptsToMergeRegions = GameConfig.REGION_MERGING_ATTEMPTS
        while(!allRegionsAreMerged() && attemptsToMergeRegions > 0) {
            log.debug("Merging region")
            loopDungeon(dungeon, { x, y ->
                val tile = dungeon.grid[x][y]
                if (tile.type == DungeonTileTypes.Earth && Coordinates(x, y).isWithinWorldBounds()) {
                    // TODO: connectors are placed too predictably. Find a better way to place connectors on all sides of room
                    placeConnector(x, y)
                }
            })
            attemptsToMergeRegions--
        }

        if (!allRegionsAreMerged()) {
            listener.regionConnectorSystemFailed()
        }
    }

    private fun allRegionsAreMerged(): Boolean {
        dungeon.regions.forEach {
            if (!mergedRegions.containsValue(it) && !mergedRegions.containsKey(it)) {
                return false
            }
        }
        return true
    }

    private fun placeConnector(x: Int, y: Int) {
        val tileToNorth = dungeon.grid[x][y + 1]
        val tileToSouth = dungeon.grid[x][y - 1]
        val tileToEast = dungeon.grid[x + 1][y]
        val tileToWest = dungeon.grid[x - 1][y]

        if(tileTypesCanHaveConnector(tileToNorth, tileToSouth)) {
            mergedRegions[tileToNorth.regionId] = tileToSouth.regionId
            dungeon.grid[x][y].type = DungeonTileTypes.Door
        } else if (tileTypesCanHaveConnector(tileToEast, tileToWest)) {
            mergedRegions[tileToEast.regionId] = tileToWest.regionId
            dungeon.grid[x][y].type = DungeonTileTypes.Door
        }
    }

    private fun tileTypesCanHaveConnector(firstTile: DungeonTile, secondTile: DungeonTile): Boolean {
        if (firstTile.regionId == secondTile.regionId) {
            return false
        }
        if (regionAlreadyMerged(firstTile.regionId, secondTile.regionId)) {
            return false
        }
        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Room) {
            return true
        }
        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Path) {
            return true
        }
        return false
    }

    private fun regionAlreadyMerged(firstRegionId: Int, secondRegionId: Int): Boolean {
        return (mergedRegions.contains(firstRegionId) || mergedRegions.containsValue(firstRegionId))
                && (mergedRegions.contains(secondRegionId) || mergedRegions.containsValue(secondRegionId))
    }

    interface Listener {
        fun regionConnectorSystemFailed()
    }
}