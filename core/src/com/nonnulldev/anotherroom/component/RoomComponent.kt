package com.nonnulldev.anotherroom.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.nonnulldev.anotherroom.enum.Direction
import kotlin.collections.HashMap

class RoomComponent : Component, Pool.Poolable{

    var doors = HashMap<Direction, Entity>()

    override fun reset() {
        doors = HashMap()
    }
}