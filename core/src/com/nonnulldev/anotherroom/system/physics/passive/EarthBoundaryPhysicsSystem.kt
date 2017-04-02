package com.nonnulldev.anotherroom.system.physics.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.physics.box2d.*
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.EarthBoundarySegmentComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.util.Mappers

class EarthBoundaryPhysicsSystem(private val world: World) : EntitySystem() {

    companion object {
        val FAMILY: Family = Family.all(
                EarthBoundarySegmentComponent::class.java
        ).get()
    }

    private lateinit var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        val earthBoundarySegments = engine.getEntitiesFor(FAMILY)

        earthBoundarySegments.forEach {
            val boundarySegment = Mappers.EARTH_BOUNDARY_SEGMENT.get(it)
            createBoundary(boundarySegment)
        }
    }

    fun createBoundary(boundarySegment: EarthBoundarySegmentComponent) {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(0f, 0f)
        val body = world.createBody(bodyDef)
        val fixtureDef = FixtureDef()
        val shape = EdgeShape()
        shape.set(
                boundarySegment.v1X,
                boundarySegment.v1Y,
                boundarySegment.v2X,
                boundarySegment.v2Y
        )
        fixtureDef.shape = shape
        body.createFixture(fixtureDef)
        shape.dispose()
    }

}
