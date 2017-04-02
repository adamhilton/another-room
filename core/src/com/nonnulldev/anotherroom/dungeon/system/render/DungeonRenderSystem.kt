package com.nonnulldev.anotherroom.dungeon.system.render

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import com.nonnulldev.anotherroom.common.component.DimensionComponent
import com.nonnulldev.anotherroom.common.component.PositionComponent
import com.nonnulldev.anotherroom.common.component.TextureComponent
import com.nonnulldev.anotherroom.common.component.ZOrderComponent
import com.nonnulldev.anotherroom.dungeon.component.DungeonTileComponent
import com.nonnulldev.anotherroom.common.util.Mappers
import com.nonnulldev.anotherroom.common.util.ZOrderComparator

class DungeonRenderSystem(val viewport: Viewport, val batch: SpriteBatch) : SortedIteratingSystem(FAMILY, ZOrderComparator.instance) {

    companion object {
        val FAMILY: Family = Family.all(
                TextureComponent::class.java,
                PositionComponent::class.java,
                DimensionComponent::class.java,
                DungeonTileComponent::class.java,
                ZOrderComponent::class.java
        ).get()
    }

    override fun update(deltaTime: Float) {
        viewport.apply()
        batch.projectionMatrix = viewport.camera.combined
        batch.begin()

        super.update(deltaTime)

        batch.end()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        draw(entity)
    }

    private fun draw(entity: Entity?) {
        val position = Mappers.POSITION.get(entity)
        val dimension = Mappers.DIMENSION.get(entity)
        val texture = Mappers.TEXTURE.get(entity)

        batch.draw(texture.region,
                position.x, position.y,
                dimension.width, dimension.height
        )
    }
}
