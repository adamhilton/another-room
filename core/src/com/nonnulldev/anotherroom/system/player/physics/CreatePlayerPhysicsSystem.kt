package com.nonnulldev.anotherroom.system.player.physics

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.player.PlayerComponent
import com.nonnulldev.anotherroom.component.player.PlayerPhysicsBodyComponent
import com.nonnulldev.anotherroom.component.common.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.util.Mappers

class CreatePlayerPhysicsSystem(private val world: World) : EntitySystem() {

    private val log = Logger(CreatePlayerPhysicsSystem::class.simpleName, Logger.DEBUG)

    private lateinit var engine: PooledEngine

    val FAMILY: Family = Family.all(
            PlayerComponent::class.java,
            PositionComponent::class.java
    ).get()

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        val player = engine.getEntitiesFor(FAMILY)
        if (player.size() > 1) {
            log.error("MORE THAN ONE PLAYER IN ENGINE")
        }

        val position = Mappers.POSITION.get(player.first())

        val bodyDef = BodyDef()
        bodyDef.position.set(position.x, position.y)
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.linearDamping = 8f
        bodyDef.fixedRotation = true
        bodyDef.allowSleep = false

        val body = world.createBody(bodyDef)
        val fixtureDef = FixtureDef()
        val shape = CircleShape()
        shape.radius = GameConfig.PLAYER_SIZE / 2f
        shape.position = Vector2(GameConfig.Companion.PLAYER_SIZE / 2f, GameConfig.Companion.PLAYER_SIZE / 2f)
        fixtureDef.shape = shape
        fixtureDef.density = GameConfig.PLAYER_DENSITY
        body.createFixture(fixtureDef)

        log.debug("Created player....")

        val component = engine.createComponent(PlayerPhysicsBodyComponent::class.java)
        component.body = body
        val entity = engine.createEntity()
        entity.add(component)
        engine.addEntity(entity)
    }

    override fun removedFromEngine(engine: Engine?) {

    }
}