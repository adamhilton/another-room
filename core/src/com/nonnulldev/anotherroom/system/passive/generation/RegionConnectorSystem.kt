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
import kotlin.collections.ArrayList

class RegionConnectorSystem(private val dungeon: Dungeon, private val listener: Listener) : EntitySystem() {

    private val log = Logger(RegionConnectorSystem::class.simpleName, Logger.DEBUG)

    private var mergedRegions = HashMap<Int, Int>()
    private var placedConnectors = ArrayList<Coordinates>()

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {

        var availableConnectors = ArrayList<Coordinates>()
        placedConnectors = ArrayList<Coordinates>()

        loopDungeon(dungeon, { x, y ->
            if (Coordinates(x, y).isWithinWorldBounds() && connectorIsValid(x, y)) {
                availableConnectors.add(Coordinates(x, y))
            }
        })

        val seed = System.nanoTime()
        Collections.shuffle(availableConnectors, Random(seed))

        var attemptsToMergeRegions = GameConfig.REGION_MERGING_ATTEMPTS
        while(!allRegionsAreMerged() && attemptsToMergeRegions > 0) {
            availableConnectors.forEach {
                val tile = dungeon.grid[it.x][it.y]
                if (tile.type == DungeonTileTypes.Earth && Coordinates(it.x, it.y).isWithinWorldBounds()) {
                    placeConnector(it.x, it.y)
                }
            }
            attemptsToMergeRegions--
        }

        removeAllDisconnectedConnectors()

        if (!allRegionsAreMerged() ) {
            listener.regionConnectorSystemFailed()
        }
}

    private fun removeAllDisconnectedConnectors() {
        placedConnectors.forEach {
            if (!connectorIsValid(it.x, it.y)) {
                dungeon.grid[it.x][it.y].type = DungeonTileTypes.Earth
            }
        }
    }

    private fun anyConnectorsAreDisconnected(placedConnectors: ArrayList<Coordinates>): Boolean {
        placedConnectors.forEach {
            if (!connectorIsValid(it.x, it.y)) {
                return true
            }
        }
        return false
    }

    private fun allRegionsAreMerged(): Boolean {
        dungeon.regions.forEach {
            // TODO: This needs to better check whether all regions are merged or not
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

        if(tilesCanHaveConnector(tileToNorth, tileToSouth)) {
            mergedRegions[tileToNorth.regionId] = tileToSouth.regionId
            dungeon.grid[x][y].type = DungeonTileTypes.Door
            placedConnectors.add(Coordinates(x, y))
        } else if (tilesCanHaveConnector(tileToEast, tileToWest)) {
            mergedRegions[tileToEast.regionId] = tileToWest.regionId
            dungeon.grid[x][y].type = DungeonTileTypes.Door
            placedConnectors.add(Coordinates(x, y))
        }
    }

    private fun connectorIsValid(x: Int, y: Int): Boolean {
        val tileToNorth = dungeon.grid[x][y + 1]
        val tileToSouth = dungeon.grid[x][y - 1]
        val tileToEast = dungeon.grid[x + 1][y]
        val tileToWest = dungeon.grid[x - 1][y]

        return connectorIsConnected(tileToNorth, tileToSouth) || connectorIsConnected(tileToEast, tileToWest)
                || connectorIsConnected(tileToNorth, tileToEast) || connectorIsConnected(tileToNorth, tileToEast)
            || connectorIsConnected(tileToSouth, tileToEast) || connectorIsConnected(tileToSouth, tileToWest)
    }

    private fun tilesCanHaveConnector(firstTile: DungeonTile, secondTile: DungeonTile): Boolean {
        if (firstTile.regionId == secondTile.regionId) {
            return false
        }

        if (regionAlreadyMerged(firstTile.regionId, secondTile.regionId)) {
            return false
        }

        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Room) {
            return true
        }

        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Path ||
                firstTile.type == DungeonTileTypes.Path && secondTile.type == DungeonTileTypes.Room) {
            return true
        }

        return false
    }

    private fun connectorIsConnected(firstTile: DungeonTile, secondTile: DungeonTile): Boolean {
        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Room) {
            return true
        }

        if(firstTile.type == DungeonTileTypes.Room && secondTile.type == DungeonTileTypes.Path ||
                firstTile.type == DungeonTileTypes.Path && secondTile.type == DungeonTileTypes.Room) {
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
