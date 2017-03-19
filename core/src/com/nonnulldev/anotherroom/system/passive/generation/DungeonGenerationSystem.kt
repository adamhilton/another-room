package com.nonnulldev.anotherroom.system.passive.generation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.types.array2dOfDungeonTiles


class DungeonGenerationSystem(private val listener: Listener) : EntitySystem(), RegionConnectorSystem.Listener {

    private val log = Logger(DungeonGenerationSystem::class.simpleName, Logger.DEBUG)

    private var dungeon = Dungeon(array2dOfDungeonTiles(GameConfig.Companion.WORLD_HEIGHT.toInt(), GameConfig.Companion.WORLD_WIDTH.toInt()))

    private lateinit var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        runGenerationSystems()
        processEntities()
    }

    private fun runGenerationSystems() {
        engine.addSystem(RoomGenerationSystem(dungeon))
        engine.addSystem(PathGenerationSystem(dungeon))
        engine.addSystem(RegionConnectorSystem(dungeon, this))
        engine.addSystem(PathCleanupSystem(dungeon))
    }

    private fun processEntities() {
        for (x in 0..dungeon.grid.lastIndex) {
            for (y in 0..dungeon.grid[x].lastIndex) {

                var tile = dungeon.grid[x][y]

                val position = positionComponent(x.toFloat(), y.toFloat())
                val dimension = dimensionComponent(1f, 1f)
                val bounds = boundsComponent(position, dimension)

                if (tile.type == DungeonTileTypes.Earth) {
                    bounds.color = Color.CLEAR
                } else if (tile.type == DungeonTileTypes.Room) {
                    bounds.color = Color.BLUE
                } else if (tile.type == DungeonTileTypes.Path) {
                    bounds.color = Color.GOLD
                } else if (tile.type == DungeonTileTypes.Door) {
                    bounds.color = Color.GREEN
                }

                val entity = engine.createEntity()
                entity.add(position)
                entity.add(dimension)
                entity.add(bounds)

                engine.addEntity(entity)
            }
        }
    }

    override fun regionConnectorSystemFailed() {
        listener.dungeonGenerationSystemFailed()
    }

    private fun positionComponent(x: Float, y: Float): PositionComponent {
        val position = engine.createComponent(PositionComponent::class.java)
        position.x = x
        position.y = y
        return position
    }

    private fun dimensionComponent(width: Float, height: Float): DimensionComponent {
        val dimension = engine.createComponent(DimensionComponent::class.java)
        dimension.width = width
        dimension.height = height
        return dimension
    }

    private fun boundsComponent(position: PositionComponent, dimension: DimensionComponent): BoundsComponent {
        val bounds = engine.createComponent(BoundsComponent::class.java)
        bounds.rectangle.setPosition(position.x, position.y)
        bounds.rectangle.setSize(dimension.width, dimension.height)
        return bounds
    }

    interface Listener {
        fun dungeonGenerationSystemFailed()
    }
}
