package com.nonnulldev.anotherroom.system.physics.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.nonnulldev.anotherroom.component.RoomComponent
import com.nonnulldev.anotherroom.util.Mappers

class RoomPhysicsSystem(private val world: World) : EntitySystem() {

    companion object {
        val FAMILY: Family = Family.all(RoomComponent::class.java).get()
    }

    private lateinit var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        createRooms()
    }

    private fun createRooms() {
        val rooms = engine.getEntitiesFor(FAMILY)
        rooms.forEach {
            val room = Mappers.ROOM.get(it)
            createBox(room)
        }
    }

    private fun createBox(room: RoomComponent) {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(room.centerX, room.centerY)
        val body = world.createBody(bodyDef)
        val fixtureDef = FixtureDef()
        val square = PolygonShape()
        square.setAsBox(room.dimension.width / 2f, room.dimension.height / 2f)
        fixtureDef.shape = square
        body.createFixture(fixtureDef)
        square.dispose()
    }
}