package com.nonnulldev.anotherroom.system.passive.generation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.*
import com.nonnulldev.anotherroom.enum.Direction
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.extension.areWithinWorldBounds
import com.nonnulldev.anotherroom.types.loop
import kotlin.collections.ArrayList

class PathGenerationSystem(private val dungeon: Dungeon) : EntitySystem() {

    val log = Logger(PathGenerationSystem::class.java.simpleName, Logger.DEBUG)

    private var paths = HashMap<Int, ArrayList<Coordinates>>()

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)


        dungeon.grid.loop { coordinates ->
            val regionId = dungeon.regions.size + 1
            var tileIsEarth = dungeon.grid.get(coordinates).type == DungeonTileTypes.Earth
            if (tileIsEarth && coordinates.areWithinWorldBounds() && spaceInAnyDirectionForPath(coordinates)) {
                generatePaths(coordinates, regionId).toString()
            }
        }

        log.debug("Number of paths: ${paths.count()}")

        paths.forEach { _, u ->
            var roomsNearPath = ArrayList<Int>()
            u.forEach {
                val nearbyRooms = getNearbyRooms(it)
                nearbyRooms.forEach {
                    if (!roomsNearPath.contains(it)) {
                        roomsNearPath.add(it)
                    }
                }
            }
            if (roomsNearPath.count() <= 1) {
                removePath(u)
            }
        }
    }

    private fun removePath(coordinates: ArrayList<Coordinates>) {
        coordinates.forEach {
            val dungeonTile = dungeon.grid.get(it)
            dungeonTile.type = DungeonTileTypes.Earth
            dungeonTile.regionId = 0 // TODO: Move this to a constant
        }
    }

    private fun getNearbyRooms(coordinates: Coordinates): ArrayList<Int> {
        var nearbyRooms = ArrayList<Int>()

        val northTwoTiles = coordinates.north().north()
        if(northTwoTiles.areWithinWorldBounds()) {
            val twoTilesNorthOfCoordinate = dungeon.grid.get(northTwoTiles)
            if (twoTilesNorthOfCoordinate.type == DungeonTileTypes.Room) {
                nearbyRooms.add(twoTilesNorthOfCoordinate.regionId)
            }
        }

        val southTwoTiles = coordinates.south().south()
        if(southTwoTiles.areWithinWorldBounds()) {
            val twoTilesSouthOfCoordinate = dungeon.grid.get(southTwoTiles)
            if (twoTilesSouthOfCoordinate.type == DungeonTileTypes.Room) {
                nearbyRooms.add(twoTilesSouthOfCoordinate.regionId)
            }
        }

        val eastTwoTiles = coordinates.east().east()
        if(eastTwoTiles.areWithinWorldBounds()) {
            val twoTilesEastOfCoordinate = dungeon.grid.get(eastTwoTiles)
            if (twoTilesEastOfCoordinate.type == DungeonTileTypes.Room) {
                nearbyRooms.add(twoTilesEastOfCoordinate.regionId)
            }
        }

        val westTwoTiles = coordinates.west().west()
        if(westTwoTiles.areWithinWorldBounds()) {
            val twoTilesWestOfCoordinate = dungeon.grid.get(westTwoTiles)
            if (twoTilesWestOfCoordinate.type == DungeonTileTypes.Room) {
                nearbyRooms.add(twoTilesWestOfCoordinate.regionId)
            }
        }

        return nearbyRooms
    }

    private fun spaceInAnyDirectionForPath(coordinates: Coordinates): Boolean {
        return enoughSpaceAhead(coordinates, Direction.NORTH) ||
                enoughSpaceAhead(coordinates, Direction.SOUTH) ||
                enoughSpaceAhead(coordinates, Direction.EAST) ||
                enoughSpaceAhead(coordinates, Direction.WEST)
    }

    private fun generatePaths(coordinates: Coordinates, regionId: Int) {
        generatePaths(coordinates, regionId, ArrayList())
    }

    private fun generatePaths(coordinates: Coordinates, regionId: Int, currentPath: ArrayList<Coordinates>) {
        visitNeighbor(coordinates.west(), Direction.WEST, regionId, currentPath)
        visitNeighbor(coordinates.east(), Direction.EAST, regionId, currentPath)
        visitNeighbor(coordinates.south(), Direction.SOUTH, regionId, currentPath)
        visitNeighbor(coordinates.north(), Direction.NORTH, regionId, currentPath)
    }

    private fun visitNeighbor (coordinates: Coordinates, direction: Direction, regionId: Int, currentPath: ArrayList<Coordinates>) {
        if (coordinates.x < GameConfig.WALL_SIZE || coordinates.x >= GameConfig.WORLD_WIDTH - GameConfig.WALL_SIZE)
            return

        if (coordinates.y < GameConfig.WALL_SIZE || coordinates.y >= GameConfig.WORLD_HEIGHT - GameConfig.WALL_SIZE)
            return

        var dungeonTile = dungeon.grid.get(coordinates)
        if (dungeonTile.type != DungeonTileTypes.Earth) {
            return
        }

        if (!enoughSpaceAhead(coordinates, direction)) {
            return
        }

        dungeonTile.regionId = regionId
        dungeonTile.type = DungeonTileTypes.Path

        currentPath.add(coordinates)

        if (!dungeon.regions.contains(regionId)) {
            dungeon.regions.add(regionId)
        }

        if (!paths.containsKey(regionId)) {
            paths[regionId] = ArrayList()
        }

        paths[regionId]?.add(coordinates)

        generatePaths(coordinates, regionId)
    }

    private fun enoughSpaceAhead(coordinates: Coordinates, direction: Direction): Boolean {
        var spacesToCheck = ArrayList<Coordinates>()

        if (direction == Direction.NORTH) {
            spacesToCheck.add(coordinates.north())
            spacesToCheck.add(coordinates.northEast())
            spacesToCheck.add(coordinates.northWest())
            spacesToCheck.add(coordinates.east())
            spacesToCheck.add(coordinates.west())
        } else if (direction == Direction.SOUTH) {
            spacesToCheck.add(coordinates.south())
            spacesToCheck.add(coordinates.southEast())
            spacesToCheck.add(coordinates.southWest())
            spacesToCheck.add(coordinates.east())
            spacesToCheck.add(coordinates.west())
        } else if (direction == Direction.EAST) {
            spacesToCheck.add(coordinates.east())
            spacesToCheck.add(coordinates.southEast())
            spacesToCheck.add(coordinates.northEast())
            spacesToCheck.add(coordinates.north())
            spacesToCheck.add(coordinates.south())
        } else if (direction == Direction.WEST) {
            spacesToCheck.add(coordinates.west())
            spacesToCheck.add(coordinates.southWest())
            spacesToCheck.add(coordinates.northWest())
            spacesToCheck.add(coordinates.north())
            spacesToCheck.add(coordinates.south())
        }

        spacesToCheck.forEach {
            if (dungeon.grid.get(it).type != DungeonTileTypes.Earth) {
                return false
            }
        }
        return true
    }
}