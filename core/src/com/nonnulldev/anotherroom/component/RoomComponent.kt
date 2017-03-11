package com.nonnulldev.anotherroom.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.nonnulldev.anotherroom.enum.DIRECTION
import kotlin.collections.HashMap

class RoomComponent : Component, Pool.Poolable{

    private var room = Entity()
    private var doors = HashMap<DIRECTION, Entity>()

    fun addDoor(direction: DIRECTION, door: Entity) {
        doors.set(direction, door)
    }

    fun removeDoor(direction: DIRECTION) {
        doors.remove(direction)
    }

    override fun reset() {
        room = Entity()
        doors = HashMap()
    }
}