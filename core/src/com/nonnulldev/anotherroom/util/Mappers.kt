package com.nonnulldev.anotherroom.util

import com.badlogic.ashley.core.ComponentMapper
import com.nonnulldev.anotherroom.component.BoundsComponent

object Mappers {

    val bounds = ComponentMapper.getFor(BoundsComponent::class.java)
}
