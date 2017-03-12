package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.component.RoomComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.enum.DIRECTION
import com.nonnulldev.anotherroom.util.Mappers

class PathGenerationSystem : EntitySystem() {

    lateinit private var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        createPaths()
    }

    private fun createPaths() {
        val rooms = engine.getEntitiesFor(FAMILY)
        rooms.forEach {
            val bounds = Mappers.BOUNDS.get(it)

            val roomRectangle = getRoomWithWalls(bounds)


        }
    }

    private fun getRoomWithWalls(bounds: BoundsComponent): Rectangle {
        val room = Rectangle(bounds.rectangle)

        room.setSize(
                room.width + GameConfig.ROOM_TO_ROOM_BUFFER,
                room.height + GameConfig.ROOM_TO_ROOM_BUFFER
        )

        return room
    }


    companion object {
        @JvmStatic
        private val FAMILY = Family.all(RoomComponent::class.java, DimensionComponent::class.java).get()
    }
}




