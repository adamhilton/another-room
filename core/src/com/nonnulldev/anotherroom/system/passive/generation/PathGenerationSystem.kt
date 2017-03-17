package com.nonnulldev.anotherroom.system.passive.generation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.*
import com.nonnulldev.anotherroom.enum.Direction
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.extension.areWithinWorldBounds
import com.nonnulldev.anotherroom.types.loopDungeon
import java.util.ArrayList

class PathGenerationSystem(private val dungeon: Dungeon) : EntitySystem() {

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)


        loopDungeon(dungeon, { x, y ->
            val coordinates = Coordinates(x, y)
            val regionId = dungeon.regions.size + 1
            var tileIsEarth = dungeon.grid[x][y].type == DungeonTileTypes.Earth
            if (tileIsEarth && coordinates.areWithinWorldBounds() && spaceInAnyDirectionForPath(coordinates)) {
                generatePaths(coordinates, regionId)
            }
        })
    }

    private fun spaceInAnyDirectionForPath(coordinates: Coordinates): Boolean {
        return enoughSpaceAhead(coordinates, Direction.NORTH) ||
                enoughSpaceAhead(coordinates, Direction.SOUTH) ||
                enoughSpaceAhead(coordinates, Direction.EAST) ||
                enoughSpaceAhead(coordinates, Direction.WEST)
    }

    private fun generatePaths(coordinates: Coordinates, regionId: Int) {
        visitNeighbor(coordinates.west(), Direction.WEST, regionId)
        visitNeighbor(coordinates.east(), Direction.EAST, regionId)
        visitNeighbor(coordinates.south(), Direction.SOUTH, regionId)
        visitNeighbor(coordinates.north(), Direction.NORTH, regionId)
    }

    private fun visitNeighbor (coordinates: Coordinates, direction: Direction, regionId: Int) {
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

        if (!dungeon.regions.contains(regionId)) {
            dungeon.regions.add(regionId)
        }

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
            val coordinates = Coordinates(it.x, it.y)
            if (dungeon.grid.get(coordinates).type != DungeonTileTypes.Earth) {
                return false
            }
        }
        return true
    }
}