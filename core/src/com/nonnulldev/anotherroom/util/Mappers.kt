package com.nonnulldev.anotherroom.util

import com.badlogic.ashley.core.ComponentMapper
import com.nonnulldev.anotherroom.component.*

class Mappers private constructor(){

    companion object {
        val BOUNDS = ComponentMapper.getFor(BoundsComponent::class.java)
        val ROOM = ComponentMapper.getFor(RoomComponent::class.java)
        val POSITION = ComponentMapper.getFor(PositionComponent::class.java)
        val DIMENSION = ComponentMapper.getFor(DimensionComponent::class.java)
        val TEXTURE = ComponentMapper.getFor(TextureComponent::class.java)
        val Z_ORDER = ComponentMapper.getFor(ZOrderComponent::class.java)
    }
}
