package com.nonnulldev.anotherroom.util

import com.badlogic.ashley.core.ComponentMapper
import com.nonnulldev.anotherroom.component.*
import com.nonnulldev.anotherroom.component.common.*
import com.nonnulldev.anotherroom.component.dungeon.EarthBoundarySegmentComponent
import com.nonnulldev.anotherroom.component.dungeon.RoomComponent
import com.nonnulldev.anotherroom.component.player.PlayerPhysicsBodyComponent

class Mappers private constructor(){

    companion object {
        val ROOM: ComponentMapper<RoomComponent> = ComponentMapper.getFor(RoomComponent::class.java)

        val POSITION: ComponentMapper<PositionComponent> = ComponentMapper.getFor(PositionComponent::class.java)

        val DIMENSION: ComponentMapper<DimensionComponent> = ComponentMapper.getFor(DimensionComponent::class.java)

        val TEXTURE: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)

        val Z_ORDER: ComponentMapper<ZOrderComponent> = ComponentMapper.getFor(ZOrderComponent::class.java)

        val EARTH_BOUNDARY_SEGMENT: ComponentMapper<EarthBoundarySegmentComponent> = ComponentMapper.getFor(EarthBoundarySegmentComponent::class.java)

        val PLAYER_BODY_PHYSICS: ComponentMapper<PlayerPhysicsBodyComponent> = ComponentMapper.getFor(PlayerPhysicsBodyComponent::class.java)
    }
}
