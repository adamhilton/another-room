package com.nonnulldev.anotherroom.system.passive.generation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.Coordinates
import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.enum.Direction
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.extension.isWithinWorldBounds
import com.nonnulldev.anotherroom.types.loopDungeon
import java.util.ArrayList

class PathGenerationSystem(private val dungeon: Dungeon) : EntitySystem() {

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)


        loopDungeon(dungeon, { x, y ->
            val regionId = dungeon.regions.size + 1
            var tileIsEarth = dungeon.grid[x][y].type == DungeonTileTypes.Earth
            if (tileIsEarth && Coordinates(x, y).isWithinWorldBounds() && spaceInAnyDirectionForPath(x, y)) {
                generatePaths(x, y, regionId)
            }
        })
    }

    private fun spaceInAnyDirectionForPath(x: Int, y: Int): Boolean {
        return enoughSpaceAhead(x, y, Direction.NORTH) ||
                enoughSpaceAhead(x, y, Direction.SOUTH) ||
                enoughSpaceAhead(x, y, Direction.EAST) ||
                enoughSpaceAhead(x, y, Direction.WEST)
    }

    private fun generatePaths(x: Int, y: Int, regionId: Int) {
        visitNeighbor( x-1, y, Direction.WEST, regionId)
        visitNeighbor( x+1, y, Direction.EAST, regionId)
        visitNeighbor( x, y-1, Direction.SOUTH, regionId)
        visitNeighbor( x, y+1, Direction.NORTH, regionId)
    }

    private fun visitNeighbor (x: Int, y: Int, direction: Direction, regionId: Int) {
        if (x < GameConfig.WALL_SIZE || x >= GameConfig.WORLD_WIDTH - GameConfig.WALL_SIZE)
            return

        if (y < GameConfig.WALL_SIZE || y >= GameConfig.WORLD_HEIGHT - GameConfig.WALL_SIZE)
            return

        if (dungeon.grid[x][y].type != DungeonTileTypes.Earth) {
            return
        }

        if (!enoughSpaceAhead(x, y, direction)) {
            return
        }

        var dungeonTile = dungeon.grid[x][y]
        dungeonTile.regionId = regionId
        dungeonTile.type = DungeonTileTypes.Path

        if (!dungeon.regions.contains(regionId)) {
            dungeon.regions.add(regionId)
        }

        generatePaths(x, y, regionId)
    }

    private fun enoughSpaceAhead(x: Int, y: Int, direction: Direction): Boolean {

        var spacesToCheck = ArrayList<Coordinates>()

        if (direction == Direction.NORTH) {
            spacesToCheck.add(northCoordinates(x, y))
            spacesToCheck.add(northEastCoordinates(x, y))
            spacesToCheck.add(northWestCoordinates(x, y))
            spacesToCheck.add(eastCoordinates(x, y))
            spacesToCheck.add(westCoordinates(x, y))
        } else if (direction == Direction.SOUTH) {
            spacesToCheck.add(southCoordinates(x, y))
            spacesToCheck.add(southEastCoordinates(x, y))
            spacesToCheck.add(southWestCoordinates(x, y))
            spacesToCheck.add(eastCoordinates(x, y))
            spacesToCheck.add(westCoordinates(x, y))
        } else if (direction == Direction.EAST) {
            spacesToCheck.add(eastCoordinates(x, y))
            spacesToCheck.add(southEastCoordinates(x, y))
            spacesToCheck.add(northEastCoordinates(x, y))
            spacesToCheck.add(northCoordinates(x, y))
            spacesToCheck.add(southCoordinates(x, y))
        } else if (direction == Direction.WEST) {
            spacesToCheck.add(westCoordinates(x, y))
            spacesToCheck.add(southWestCoordinates(x, y))
            spacesToCheck.add(northWestCoordinates(x, y))
            spacesToCheck.add(northCoordinates(x, y))
            spacesToCheck.add(southCoordinates(x, y))
        }

        spacesToCheck.forEach {
            if (dungeon.grid[it.x][it.y].type != DungeonTileTypes.Earth) {
                return false
            }
        }

        return true
    }

    private fun northCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x, y + 1)
    }

    private fun northEastCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y + 1)
    }

    private fun southCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x, y - 1)
    }

    private fun southEastCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y - 1)
    }

    private fun eastCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x + 1, y)
    }

    private fun northWestCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y + 1)
    }

    private fun westCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y)
    }

    private fun southWestCoordinates(x: Int, y: Int): Coordinates {
        return Coordinates(x - 1, y - 1)
    }

}