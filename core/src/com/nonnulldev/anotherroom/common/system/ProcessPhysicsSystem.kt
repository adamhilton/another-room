package com.nonnulldev.anotherroom.common.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import com.nonnulldev.anotherroom.common.config.GameConfig

class ProcessPhysicsSystem(private val world: World) : IteratingSystem(Family.all().get()) {

    override fun update(deltaTime: Float) {
        world.step(1/ GameConfig.DEFAULT_REFRESH_RATE, 6, 2)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
    }
}
