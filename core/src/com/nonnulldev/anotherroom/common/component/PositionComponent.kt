package com.nonnulldev.anotherroom.common.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class PositionComponent : Component, Pool.Poolable {

    var x: Float = 0f
    var y: Float = 0f

    override fun reset() {
        x = 0f
        y = 0f
    }
}
