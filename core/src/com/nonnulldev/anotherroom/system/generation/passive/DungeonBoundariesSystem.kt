package com.nonnulldev.anotherroom.system.generation.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.EarthBoundarySegmentComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.*
import com.nonnulldev.anotherroom.enum.Direction
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.extension.areWithinWorldBounds
import com.nonnulldev.anotherroom.types.loop

class DungeonBoundariesSystem(private val dungeon: Dungeon) : EntitySystem() {

    val log = Logger(DungeonBoundariesSystem::class.simpleName, Logger.DEBUG)

    private lateinit var engine: PooledEngine

    val visitedTiles = array2dOfVisitedTiles(GameConfig.WORLD_WIDTH.toInt(), GameConfig.WORLD_HEIGHT.toInt())

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        dungeon.grid.loop {
            visitedTiles.get(it).visited = true
            checkNeighborsForWallBoundaries(it)
        }
    }


    private fun checkNeighborsForWallBoundaries(coordinates: Coordinates) {
        fillWallBoundary(coordinates.north(), Direction.NORTH, coordinates)
        fillWallBoundary(coordinates.south(), Direction.SOUTH, coordinates)
        fillWallBoundary(coordinates.east(), Direction.EAST, coordinates)
        fillWallBoundary(coordinates.west(), Direction.WEST, coordinates)
    }

    private fun fillWallBoundary(coordinates: Coordinates, direction: Direction, previousCoordinates: Coordinates) {
        if (!coordinates.areWithinWorldBounds()) return

        val visitedTile = visitedTiles.get(coordinates)

        val tileType = dungeon.grid.get(coordinates).type
        val previousTileType = dungeon.grid.get(previousCoordinates).type

        if (canPlaceEarthBoundarySegment(tileType, previousTileType)) {
            addBoundarySegmentToEngine(coordinates, direction)
        }

        visitedTile.visited = true
    }

    private fun canPlaceEarthBoundarySegment(tileType: DungeonTileTypes, previousTileType: DungeonTileTypes): Boolean {
        return (tileType == DungeonTileTypes.Earth && previousTileType != DungeonTileTypes.Earth) ||
                (tileType != DungeonTileTypes.Earth && previousTileType == DungeonTileTypes.Earth)
    }

    private fun addBoundarySegmentToEngine(coordinates: Coordinates, direction: Direction) {
        val x = coordinates.x.toFloat()
        val y = coordinates.y.toFloat()
        val earthBoundarySegment = engine.createComponent(EarthBoundarySegmentComponent::class.java)

        if(direction == Direction.NORTH) earthBoundarySegment.set(x , y, x + 1, y)
        else if(direction == Direction.SOUTH) earthBoundarySegment.set(x, y + 1, x + 1 , y + 1)
        else if(direction == Direction.EAST) earthBoundarySegment.set(x, y, x, y + 1)
        else if(direction == Direction.WEST) earthBoundarySegment.set(x + 1, y, x + 1, y + 1)

        val entity = engine.createEntity()
        entity.add(earthBoundarySegment)
        engine.addEntity(entity)
    }

    data class VisitedTile(var visited: Boolean = false)
    fun array2dOfVisitedTiles(sizeOuter: Int, sizeInner: Int): Array<Array<VisitedTile>>
            = Array(sizeOuter) { Array(sizeInner){ VisitedTile() } }

    fun Array<Array<VisitedTile>>.get(coordinates: Coordinates): VisitedTile {
        return this[coordinates.x][coordinates.y]
    }
}
