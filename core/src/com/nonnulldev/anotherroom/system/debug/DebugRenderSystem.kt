package com.nonnulldev.anotherroom.system.debug

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.util.Mappers

class DebugRenderSystem(private val viewport: Viewport, private val renderer: ShapeRenderer) : IteratingSystem(DebugRenderSystem.FAMILY) {

    override fun update(deltaTime: Float) {
        val oldColor = renderer.color.cpy()

        viewport.apply()
        renderer.color = Color.RED
        renderer.projectionMatrix = viewport.camera.combined
        renderer.begin(ShapeRenderer.ShapeType.Line)

        super.update(deltaTime)

        renderer.end()
        renderer.color = oldColor
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bounds = Mappers.BOUNDS.get(entity)

        renderer.color = bounds.color

        renderer.rect(bounds.rectangle.x, bounds.rectangle.y,
                bounds.rectangle.width, bounds.rectangle.height
        )
    }

    companion object {
        private val FAMILY = Family.all(BoundsComponent::class.java).get()
    }
}
