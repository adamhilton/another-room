package com.nonnulldev.anotherroom.common.system.debug

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World

class Box2DDebugRenderSystem(private val world: World, private val camera: OrthographicCamera)
    : IteratingSystem(Family.all().get()) {

    val box2DDebugRenderer = Box2DDebugRenderer()

    override fun update(deltaTime: Float) {
        box2DDebugRenderer.render(world, camera.combined)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

    }

    override fun removedFromEngine(engine: Engine?) {
        box2DDebugRenderer.dispose()
    }
}