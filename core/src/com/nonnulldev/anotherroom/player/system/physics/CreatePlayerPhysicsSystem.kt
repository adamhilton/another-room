package com.nonnulldev.anotherroom.player.system.physics

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
import com.nonnulldev.anotherroom.common.component.PositionComponent
import com.nonnulldev.anotherroom.common.config.GameConfig
import com.nonnulldev.anotherroom.common.util.Mappers
import com.nonnulldev.anotherroom.player.component.PlayerComponent
import com.nonnulldev.anotherroom.player.component.PlayerPhysicsBodyComponent

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

        val position = Mappers.POSITION.get(player.first())

        val bodyDef = BodyDef()
        bodyDef.position.set(position.x, position.y)
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.linearDamping = GameConfig.DEFAULT_LINEAR_DAMPENING
        bodyDef.fixedRotation = true
        bodyDef.allowSleep = true

        val body = world.createBody(bodyDef)
        val fixtureDef = FixtureDef()
        val shape = CircleShape()
        shape.radius = GameConfig.PLAYER_SIZE / 2f
        shape.position = Vector2(GameConfig.PLAYER_SIZE / 2f, GameConfig.PLAYER_SIZE / 2f)
        fixtureDef.shape = shape
        fixtureDef.density = GameConfig.PLAYER_DENSITY
        body.createFixture(fixtureDef)

        val component = engine.createComponent(PlayerPhysicsBodyComponent::class.java)
        component.body = body
        val entity = engine.createEntity()
        entity.add(component)
        engine.addEntity(entity)
    }
}