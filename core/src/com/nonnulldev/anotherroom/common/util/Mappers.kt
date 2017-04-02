package com.nonnulldev.anotherroom.common.util

import com.badlogic.ashley.core.ComponentMapper
import com.nonnulldev.anotherroom.common.component.DimensionComponent
import com.nonnulldev.anotherroom.common.component.PositionComponent
import com.nonnulldev.anotherroom.common.component.TextureComponent
import com.nonnulldev.anotherroom.common.component.ZOrderComponent
import com.nonnulldev.anotherroom.dungeon.component.EarthBoundarySegmentComponent
import com.nonnulldev.anotherroom.dungeon.component.RoomComponent
import com.nonnulldev.anotherroom.player.component.PlayerPhysicsBodyComponent

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
