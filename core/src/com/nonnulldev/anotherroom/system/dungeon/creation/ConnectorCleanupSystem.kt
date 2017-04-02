package com.nonnulldev.anotherroom.system.dungeon.creation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.data.*
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.extension.areWithinWorldBounds
import com.nonnulldev.anotherroom.types.loop

class ConnectorCleanupSystem(val dungeonCreationObject: DungeonCreationObject) : EntitySystem() {

    val log = Logger(ConnectorCleanupSystem::class.java.simpleName, Logger.DEBUG)

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        dungeonCreationObject.grid.loop { coordinates ->
            val tile = dungeonCreationObject.grid.get(coordinates)
            if (tile.type == DungeonTileTypes.Door && doorIsDead(coordinates)) {
                log.debug("Removing dead door...")
                tile.type = DungeonTileTypes.Earth
            }
        }
    }

    private fun doorIsDead(coordinates: Coordinates): Boolean {
        var connectedRegions = HashSet<Int>()

        val northOneTile = coordinates.north()
        if(northOneTile.areWithinWorldBounds()) {
            val oneTileNorthOfCoordinate = dungeonCreationObject.grid.get(northOneTile)
            if (oneTileNorthOfCoordinate.type == DungeonTileTypes.Room ||
                    oneTileNorthOfCoordinate.type == DungeonTileTypes.Path &&
                    !connectedRegions.contains(oneTileNorthOfCoordinate.regionId)) {
                connectedRegions.add(oneTileNorthOfCoordinate.regionId)
            }
        }

        val southOneTile = coordinates.south()
        if(southOneTile.areWithinWorldBounds()) {
            val oneTileSouthOfCoordinate = dungeonCreationObject.grid.get(southOneTile)
            if (oneTileSouthOfCoordinate.type == DungeonTileTypes.Room ||
                    oneTileSouthOfCoordinate.type == DungeonTileTypes.Path &&
                    !connectedRegions.contains(oneTileSouthOfCoordinate.regionId)) {
                connectedRegions.add(oneTileSouthOfCoordinate.regionId)
            }
        }

        val eastOneTile = coordinates.east()
        if(eastOneTile.areWithinWorldBounds()) {
            val oneTileEastOfCoordinate = dungeonCreationObject.grid.get(eastOneTile)
            if (oneTileEastOfCoordinate.type == DungeonTileTypes.Room ||
                    oneTileEastOfCoordinate.type == DungeonTileTypes.Path &&
                    !connectedRegions.contains(oneTileEastOfCoordinate.regionId)) {
                connectedRegions.add(oneTileEastOfCoordinate.regionId)
            }
        }

        val westOneTile = coordinates.west()
        if(westOneTile.areWithinWorldBounds()) {
            val oneTileWestOfCoordinate = dungeonCreationObject.grid.get(westOneTile)
            if (oneTileWestOfCoordinate.type == DungeonTileTypes.Room ||
                    oneTileWestOfCoordinate.type == DungeonTileTypes.Path &&
                    !connectedRegions.contains(oneTileWestOfCoordinate.regionId)) {
                connectedRegions.add(oneTileWestOfCoordinate.regionId)
            }
        }

        return connectedRegions.size <= 1
    }
}