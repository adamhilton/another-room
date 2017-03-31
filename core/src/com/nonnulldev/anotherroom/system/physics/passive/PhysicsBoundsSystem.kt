package com.nonnulldev.anotherroom.system.physics.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.physics.box2d.*
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.EarthBoundaryComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.util.Mappers

class PhysicsBoundsSystem(private val world: World) : EntitySystem() {

    companion object {
        val FAMILY: Family = Family.all(
                EarthBoundaryComponent::class.java,
                DimensionComponent::class.java,
                PositionComponent::class.java
        ).get()
    }

    private lateinit var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        val earthTiles = engine.getEntitiesFor(FAMILY)

        earthTiles.forEach {
            val dimension = Mappers.DIMENSION.get(it)
            val position = Mappers.POSITION.get(it)

            createBoundary(position.x, position.y, dimension.width, dimension.height)

        }
    }

    fun createBoundary(x: Float, y: Float, width: Float, height: Float) {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(x + (width / 2f), y + (height / 2f))
        val body = world.createBody(bodyDef)
        val fixtureDef = FixtureDef()
        var shape = PolygonShape()
        shape.setAsBox(width/ 2f, height / 2f)
        fixtureDef.shape = shape
        body.createFixture(fixtureDef)
    }

}
