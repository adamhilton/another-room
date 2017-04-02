package com.nonnulldev.anotherroom.dungeon.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.nonnulldev.anotherroom.common.data.Coordinates
import com.nonnulldev.anotherroom.common.data.Dimension
import com.nonnulldev.anotherroom.common.enum.Direction
import kotlin.collections.HashMap

class RoomComponent : Component, Pool.Poolable {

    var centerX: Float = 0f
    var centerY: Float = 0f
    var dimension: Dimension = Dimension(0, 0)
    var doors = HashMap<Direction, Entity>()

    override fun reset() {
        doors = HashMap()
        dimension = Dimension(0, 0)
        centerX = 0f
        centerY = 0f
    }
}