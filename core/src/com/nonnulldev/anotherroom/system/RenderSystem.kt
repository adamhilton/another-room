package com.nonnulldev.anotherroom.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.component.TextureComponent
import com.nonnulldev.anotherroom.enum.Orientation
import com.nonnulldev.anotherroom.util.Mappers

class RenderSystem(val viewport: Viewport, val batch: SpriteBatch) : EntitySystem() {

    val FAMILY = Family.all(
            TextureComponent::class.java,
            PositionComponent::class.java,
            DimensionComponent::class.java
    ).get()

    val renderQueue = ArrayList<Entity>()

    override fun update(deltaTime: Float) {
        val entities = engine.getEntitiesFor(FAMILY)
        renderQueue.addAll(entities)

        viewport.apply()
        batch.projectionMatrix = viewport.camera.combined
        batch.begin()

        draw()

        batch.end()

        renderQueue.clear()
    }

    private fun draw() {
        renderQueue.forEach {
            val position = Mappers.POSITION.get(it)
            val dimension = Mappers.DIMENSION.get(it)
            val texture = Mappers.TEXTURE.get(it)

            batch.draw(texture.region,
                    position.x, position.y,
                    dimension.width, dimension.height
            )
        }
    }
}
