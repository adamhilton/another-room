package com.nonnulldev.anotherroom.common.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DimensionComponent : Component, Pool.Poolable {

    var width = 1f
    var height = 1f

    override fun reset() {
        width = 1f
        height = 1f
    }
}
