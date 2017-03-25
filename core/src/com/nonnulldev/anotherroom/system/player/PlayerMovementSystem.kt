package com.nonnulldev.anotherroom.system.player

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.PlayerComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.util.Mappers
import com.nonnulldev.anotherroom.util.player.ControlCharacterInfo

class PlayerMovementSystem : EntitySystem() {

    val log = Logger(PlayerMovementSystem::class.java.simpleName, Logger.DEBUG)


    private lateinit var engine: PooledEngine

    private val info = ControlCharacterInfo()

    companion object {
        val FAMILY: Family = Family.all(
                PlayerComponent::class.java,
                PositionComponent::class.java
        ).get()
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        log.info("Player Movement Info= $info")
    }

    override fun update(deltaTime: Float) {
        if (Gdx.app.type != Application.ApplicationType.Desktop) {
            return
        }

        val moveSpeed = info.moveSpeed * deltaTime

        if (info.isLeftPressed) {
            moveLeft(moveSpeed)
        }

        if (info.isRightPressed) {
            moveRight(moveSpeed)
        }

        if (info.isUpPressed) {
            moveUp(moveSpeed)
        }

        if (info.isDownPressed) {
            moveDown(moveSpeed)
        }

    }

    private fun moveLeft(speed: Float) {
        updatePosition(-speed, 0f)

    }

    private fun moveRight(speed: Float) {
        updatePosition(speed, 0f)
    }

    private fun moveUp(speed: Float) {
        updatePosition(0f, speed)
    }

    private fun moveDown(speed: Float) {
        updatePosition(0f, -speed)
    }


    private fun updatePosition(x: Float, y: Float) {
        val player = engine.getEntitiesFor(FAMILY).first()
        val position = Mappers.POSITION.get(player)
        position.x += x
        position.y += y
    }
}
