package com.nonnulldev.anotherroom.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ZOrderComponent : Component, Pool.Poolable {

    var z: Int = -1

    override fun reset() {
        z = -1
    }
}

