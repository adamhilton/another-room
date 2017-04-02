package com.nonnulldev.anotherroom.system.player

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.player.PlayerComponent
import com.nonnulldev.anotherroom.component.player.PlayerPhysicsBodyComponent
import com.nonnulldev.anotherroom.component.common.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.util.Mappers

class PlayerCameraSystem(val camera: OrthographicCamera) : EntitySystem() {

    val log = Logger(PlayerCameraSystem::class.java.simpleName, Logger.DEBUG)

    val FAMILY: Family = Family.all(PlayerComponent::class.java, PositionComponent::class.java).get()


    override fun addedToEngine(engine: Engine?) {
        camera.zoom = GameConfig.PLAYER_ZOOM
    }

    override fun update(deltaTime: Float) {
        val player = engine.getEntitiesFor(FAMILY).first()
        val playerPosition = Mappers.POSITION.get(player)

        val cameraPosition = camera.position
        cameraPosition.x = camera.position.x + (playerPosition.x - camera.position.x) * 0.1f
        cameraPosition.y = camera.position.y + (playerPosition.y - camera.position.y) * 0.1f
        camera.position.set(cameraPosition)

        camera.update()

    }
}