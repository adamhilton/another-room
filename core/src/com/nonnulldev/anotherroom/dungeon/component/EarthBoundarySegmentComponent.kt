package com.nonnulldev.anotherroom.dungeon.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class EarthBoundarySegmentComponent : Component, Pool.Poolable {

    var v1X = -1f
    var v1Y = -1f
    var v2X = -1f
    var v2Y = -1f

    fun set(v1X: Float, v1Y: Float, v2X: Float, v2Y: Float) {
        this.v1X = v1X
        this.v1Y = v1Y
        this.v2X = v2X
        this.v2Y = v2Y
    }

    override fun reset() {
        v1X = -1f
        v1Y = -1f
        v2X = -1f
        v2Y = -1f
    }
}