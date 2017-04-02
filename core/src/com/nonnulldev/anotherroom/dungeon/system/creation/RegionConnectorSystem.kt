package com.nonnulldev.anotherroom.dungeon.system.creation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.common.config.GameConfig
import com.nonnulldev.anotherroom.common.data.*
import com.nonnulldev.anotherroom.dungeon.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.common.enum.Orientation
import com.nonnulldev.anotherroom.common.extension.areWithinWorldBounds
import com.nonnulldev.anotherroom.dungeon.data.DungeonCreationObject
import com.nonnulldev.anotherroom.dungeon.data.DungeonTile
import com.nonnulldev.anotherroom.dungeon.data.get
import com.nonnulldev.anotherroom.dungeon.types.array2dOfRegionTiles
import com.nonnulldev.anotherroom.dungeon.types.loop
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class RegionConnectorSystem(private val dungeonCreationObject: DungeonCreationObject, private val listener: RegionConnectorSystem.Listener) : EntitySystem() {

    private val log = Logger(RegionConnectorSystem::class.simpleName, Logger.DEBUG)

    private var placedConnectors = HashMap<Int, Int>()
    private var mergedRegions = HashSet<Int>()
    private var regionTiles = array2dOfRegionTiles(GameConfig.Companion.WORLD_WIDTH.toInt(), GameConfig.Companion.WORLD_HEIGHT.toInt())

    override fun checkProcessing(): Boolean {
        return false
    }

    data class RegionTile(val id: Int = 0, val type: DungeonTileTypes, var visited: Boolean = false)

    override fun addedToEngine(engine: Engine?) {

        val availableConnectors = ArrayList<Coordinates>()

        dungeonCreationObject.grid.loop { coordinates ->
            if (coordinates.areWithinWorldBounds() && connectorIsValid(coordinates)) {
                availableConnectors.add(coordinates)
            }
        }

        val seed = System.nanoTime()
        Collections.shuffle(availableConnectors, Random(seed))

        var attemptsToMergeRegions = GameConfig.REGION_MERGING_ATTEMPTS
        while(!allConnectorsPlaced() && attemptsToMergeRegions > 0) {
            availableConnectors.forEach {
                val coordinates = Coordinates(it.x, it.y)
                val tile = dungeonCreationObject.grid.get(coordinates)
                if (tile.type == DungeonTileTypes.Earth && coordinates.areWithinWorldBounds()) {
                    placeConnector(coordinates)
                }
            }
            attemptsToMergeRegions--
        }

        if (!checkIfAllRegionsAreAccessible() ) {
            log.debug("Not all regions connected...")
            listener.regionConnectorSystemFailed()
        }
    }

    private fun checkIfAllRegionsAreAccessible(): Boolean {
        regionTiles = array2dOfRegionTiles(GameConfig.Companion.WORLD_WIDTH.toInt(), GameConfig.Companion.WORLD_HEIGHT.toInt())

        dungeonCreationObject.grid.loop { coordinates ->
            val tile = dungeonCreationObject.grid.get(coordinates)
            regionTiles[coordinates.x][coordinates.y] = RegionConnectorSystem.RegionTile(tile.regionId, tile.type)
        }

        var fillStarted = false
        dungeonCreationObject.grid.loop { coordinates ->
            if (!fillStarted) {
                val tile = regionTiles[coordinates.x][coordinates.y]
                if (tile.type == DungeonTileTypes.Room) {
                    regionFill(coordinates)
                    fillStarted = true
                }
            }
        }
        return allRegionsConnected()
    }

    private fun allRegionsConnected(): Boolean {
        return dungeonCreationObject.regions.count() == mergedRegions.count()
    }

    private fun regionFill(coordinates: Coordinates) {
        val regionId = regionTiles[coordinates.x][coordinates.y].id
        mergedRegions.add(regionId)
        regionFill(coordinates, regionId)
    }

    private fun regionFill(coordinates: Coordinates, regionId: Int) {
        sequenceOf(coordinates.north(), coordinates.south(), coordinates.east(), coordinates.west())
                .forEach { visitNeighbor(it, regionId) }
    }

    private fun visitNeighbor (coordinates: Coordinates, regionId: Int) {
        val dungeonTile = regionTiles[coordinates.x][coordinates.y]

        if (dungeonTile.type == DungeonTileTypes.Earth) {
            return
        }

        if (dungeonTile.visited) {
            return
        }

        dungeonTile.visited = true

        if (dungeonTile.type != DungeonTileTypes.Door && !mergedRegions.contains(dungeonTile.id)) {
            mergedRegions.add(dungeonTile.id)
        }

        regionFill(coordinates, regionId)
    }

    private fun allConnectorsPlaced(): Boolean {
        dungeonCreationObject.regions.forEach {
            if (!placedConnectors.containsValue(it) && !placedConnectors.containsKey(it)) {
                return false
            }
        }
        return true
    }

    private fun placeConnector(coordinates: Coordinates) {
        val tileToNorth = dungeonCreationObject.grid.get(coordinates.north())
        val tileToSouth = dungeonCreationObject.grid.get(coordinates.south())
        val tileToEast = dungeonCreationObject.grid.get(coordinates.east())
        val tileToWest = dungeonCreationObject.grid.get(coordinates.west())

        if (connectorIsNearTile(tileToNorth, tileToSouth, tileToEast, tileToWest)) {
            return
        }

        if(tilesCanHaveConnector(tileToNorth, tileToSouth)) {
            val tile = dungeonCreationObject.grid.get(coordinates)
            tile.type = DungeonTileTypes.Door
            tile.orientation = Orientation.HORIZONTAL
            placedConnectors[tileToNorth.regionId] = tileToSouth.regionId
        } else if (tilesCanHaveConnector(tileToEast, tileToWest)) {
            val tile = dungeonCreationObject.grid.get(coordinates)
            tile.type = DungeonTileTypes.Door
            tile.orientation = Orientation.VERTICAL
            placedConnectors[tileToEast.regionId] = tileToWest.regionId
        }
    }

    private fun connectorIsNearTile(tileToNorth: DungeonTile, tileToSouth: DungeonTile, tileToEast: DungeonTile, tileToWest: DungeonTile): Boolean {

        return tileToNorth.type == DungeonTileTypes.Door ||
                tileToEast.type == DungeonTileTypes.Door ||
                tileToWest.type == DungeonTileTypes.Door ||
                tileToSouth.type == DungeonTileTypes.Door
    }

    private fun connectorIsValid(coordinates: Coordinates): Boolean {
        val tileToNorth = dungeonCreationObject.grid.get(coordinates.north())
        val tileToSouth = dungeonCreationObject.grid.get(coordinates.south())
        val tileToEast = dungeonCreationObject.grid.get(coordinates.east())
        val tileToWest = dungeonCreationObject.grid.get(coordinates.west())

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
        return (placedConnectors.contains(firstRegionId) || placedConnectors.containsValue(firstRegionId))
                && (placedConnectors.contains(secondRegionId) || placedConnectors.containsValue(secondRegionId))
    }

    interface Listener {
        fun regionConnectorSystemFailed()
    }
}
