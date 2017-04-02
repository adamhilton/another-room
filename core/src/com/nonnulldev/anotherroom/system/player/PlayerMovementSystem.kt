package com.nonnulldev.anotherroom.system.player

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.PlayerComponent
import com.nonnulldev.anotherroom.component.PlayerPhysicsBodyComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.util.Mappers
import com.nonnulldev.anotherroom.util.player.ControlCharacterInfo

class PlayerMovementSystem : EntitySystem() {

    val log = Logger(PlayerMovementSystem::class.java.simpleName, Logger.DEBUG)

    val PLAYER_PHYSICS_BODY_FAMILY: Family = Family.all(PlayerPhysicsBodyComponent::class.java).get()
    val PLAYER_POSITION_FAMILY: Family = Family.all(PlayerComponent::class.java, PositionComponent::class.java).get()

    private lateinit var playerPosition: PositionComponent

    private lateinit var playerBody: Body

    private lateinit var engine: PooledEngine

    private val info = ControlCharacterInfo()

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        log.info("Player Movement Info= $info")

        val playerBodyPhysics = engine.getEntitiesFor(PLAYER_PHYSICS_BODY_FAMILY).first()
        playerBody = Mappers.PLAYER_BODY_PHYSICS.get(playerBodyPhysics).body as Body
        playerPosition = Mappers.POSITION.get(engine.getEntitiesFor(PLAYER_POSITION_FAMILY).first())
    }

    override fun update(deltaTime: Float) {
        if (Gdx.app.type != Application.ApplicationType.Desktop) {
            return
        }

        val moveSpeed = GameConfig.PLAYER_SPEED * deltaTime

        val velocity = playerBody.linearVelocity

        if (info.isLeftPressed && velocity.x > -GameConfig.MAX_VELOCITY) {
            moveLeft(moveSpeed)
        }

        if (info.isRightPressed && velocity.x < GameConfig.MAX_VELOCITY) {
            moveRight(moveSpeed)
        }

        if (info.isUpPressed && velocity.y < GameConfig.MAX_VELOCITY) {
            moveUp(moveSpeed)
        }

        if (info.isDownPressed && velocity.y > -GameConfig.MAX_VELOCITY ) {
            moveDown(moveSpeed)
        }

        playerPosition.x = playerBody.worldCenter.x
        playerPosition.y = playerBody.worldCenter.y
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
        playerBody.applyLinearImpulse(x, y, 0f, 0f, false)
    }
}
