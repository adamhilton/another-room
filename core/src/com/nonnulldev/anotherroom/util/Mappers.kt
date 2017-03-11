package com.nonnulldev.anotherroom.util

import com.badlogic.ashley.core.ComponentMapper
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.RoomComponent

class Mappers private constructor(){

    companion object {
        val BOUNDS = ComponentMapper.getFor(BoundsComponent::class.java)
        val ROOM = ComponentMapper.getFor(RoomComponent::class.java)
    }
}
