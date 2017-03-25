package com.nonnulldev.anotherroom.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.PlayerComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.util.Mappers

class PlayerCameraSystem(val camera: OrthographicCamera, val batch: SpriteBatch) : EntitySystem() {

    val log = Logger(PlayerCameraSystem::class.java.simpleName, Logger.DEBUG)


    val FAMILY: Family = Family.all(
            PlayerComponent::class.java,
            PositionComponent::class.java
    ).get()

    override fun addedToEngine(engine: Engine?) {
        camera.zoom = GameConfig.PLAYER_ZOOM
    }

    override fun update(deltaTime: Float) {
        val players = engine.getEntitiesFor(FAMILY)
        if (players.count() != 1) {
            log.error("Players count is not equal to 1. Players entities found: ${players.count()}")
        }
        if (players.count() == 1) {
            val position = Mappers.POSITION.get(players.first())
            camera.position.set(position.x, position.y, 0f)
            camera.update()
            batch.projectionMatrix = camera.combined
        }
    }
}