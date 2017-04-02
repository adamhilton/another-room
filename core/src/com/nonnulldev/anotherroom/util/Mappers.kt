package com.nonnulldev.anotherroom.util

import com.badlogic.ashley.core.ComponentMapper
import com.nonnulldev.anotherroom.component.*

class Mappers private constructor(){

    companion object {
        val BOUNDS: ComponentMapper<BoundsComponent> = ComponentMapper.getFor(BoundsComponent::class.java)

        val ROOM: ComponentMapper<RoomComponent> = ComponentMapper.getFor(RoomComponent::class.java)

        val POSITION: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)

        val DIMENSION: ComponentMapper<DimensionComponent> = ComponentMapper.getFor(DimensionComponent::class.java)

        val TEXTURE: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)

        val Z_ORDER: ComponentMapper<ZOrderComponent> = ComponentMapper.getFor(ZOrderComponent::class.java)

        val EARTH_BOUNDARY_SEGMENT: ComponentMapper<EarthBoundarySegmentComponent> = ComponentMapper.getFor(EarthBoundarySegmentComponent::class.java)

        val PLAYER_BODY_PHYSICS: ComponentMapper<PlayerPhysicsBodyComponent> = ComponentMapper.getFor(PlayerPhysicsBodyComponent::class.java)
    }
}
