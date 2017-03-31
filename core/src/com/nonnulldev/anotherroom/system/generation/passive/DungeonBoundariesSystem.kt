package com.nonnulldev.anotherroom.system.generation.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.EarthBoundaryComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.*
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.types.loop

class DungeonBoundariesSystem(private val dungeon: Dungeon) : EntitySystem() {

    private lateinit var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {

        this.engine = engine as PooledEngine

        dungeon.grid.loop {
            val tile = dungeon.grid.get(it)
            if (tile.type == DungeonTileTypes.Earth) {
                val earthBoundary = engine.createComponent(EarthBoundaryComponent::class.java)
                val position = engine.createComponent(PositionComponent::class.java)
                position.x = it.x.toFloat()
                position.y = it.y.toFloat()
                val dimension = engine.createComponent(DimensionComponent::class.java)
                dimension.width = GameConfig.TILE_SIZE
                dimension.height = GameConfig.TILE_SIZE
                val entity = engine.createEntity()
                entity.add(earthBoundary)
                entity.add(position)
                entity.add(dimension)
                engine.addEntity(entity)
            }
        }
    }
}
