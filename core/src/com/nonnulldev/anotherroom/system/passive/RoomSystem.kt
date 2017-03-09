package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.*
import com.nonnulldev.anotherroom.component.BoundsComponent

abstract class RoomSystem : EntitySystem() {

    lateinit private var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        createRooms()
    }

    fun createRooms() {

        for (i in 0..4) {
            val bounds = engine.createComponent(BoundsComponent::class.java)
            val roomEntity = engine.createEntity()
            roomEntity.add(bounds)
            engine.addEntity(roomEntity)

        }
    }

}