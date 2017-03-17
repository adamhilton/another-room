package com.nonnulldev.anotherroom.system.passive.generation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.*
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.extension.areWithinWorldBounds
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
            val coordinates = Coordinates(x, y)
            if (coordinates.areWithinWorldBounds() && connectorIsValid(coordinates)) {
                availableConnectors.add(coordinates)
            }
        })

        val seed = System.nanoTime()
        Collections.shuffle(availableConnectors, Random(seed))

        var attemptsToMergeRegions = GameConfig.REGION_MERGING_ATTEMPTS
        while(!allRegionsAreMerged() && attemptsToMergeRegions > 0) {
            availableConnectors.forEach {
                val coordinates = Coordinates(it.x, it.y)
                val tile = dungeon.grid.get(coordinates)
                if (tile.type == DungeonTileTypes.Earth && coordinates.areWithinWorldBounds()) {
                    placeConnector(coordinates)
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
            if (!connectorIsValid(it)) {
                dungeon.grid.get(it).type = DungeonTileTypes.Earth
            }
        }
    }

    private fun anyConnectorsAreDisconnected(placedConnectors: ArrayList<Coordinates>): Boolean {
        placedConnectors.forEach {
            if (!connectorIsValid(it)) {
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

    private fun placeConnector(coordinates: Coordinates) {
        val tileToNorth = dungeon.grid.get(coordinates.north())
        val tileToSouth = dungeon.grid.get(coordinates.south())
        val tileToEast = dungeon.grid.get(coordinates.east())
        val tileToWest = dungeon.grid.get(coordinates.west())

        if (connectorIsNearTile(tileToNorth, tileToSouth, tileToEast, tileToWest)) {
            return
        }

        if(tilesCanHaveConnector(tileToNorth, tileToSouth)) {
            mergedRegions[tileToNorth.regionId] = tileToSouth.regionId
            dungeon.grid.get(coordinates).type = DungeonTileTypes.Door
            placedConnectors.add(coordinates)
        } else if (tilesCanHaveConnector(tileToEast, tileToWest)) {
            mergedRegions[tileToEast.regionId] = tileToWest.regionId
            dungeon.grid.get(coordinates).type = DungeonTileTypes.Door
            placedConnectors.add(coordinates)
        }
    }

    private fun connectorIsNearTile(tileToNorth: DungeonTile, tileToSouth: DungeonTile, tileToEast: DungeonTile, tileToWest: DungeonTile): Boolean {
        return tileToNorth.type == DungeonTileTypes.Door ||
                tileToEast.type == DungeonTileTypes.Door ||
                tileToWest.type == DungeonTileTypes.Door ||
                tileToSouth.type == DungeonTileTypes.Door
    }

    private fun connectorIsValid(coordinates: Coordinates): Boolean {
        val tileToNorth = dungeon.grid.get(coordinates.north())
        val tileToSouth = dungeon.grid.get(coordinates.south())
        val tileToEast = dungeon.grid.get(coordinates.east())
        val tileToWest = dungeon.grid.get(coordinates.west())

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

        if (firstTile.type == DungeonTileTypes.Door || secondTile.type == DungeonTileTypes.Door) {
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
