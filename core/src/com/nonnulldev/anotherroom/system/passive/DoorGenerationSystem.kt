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

class DoorGenerationSystem : EntitySystem() {

    lateinit private var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        createDoors()
    }

    private fun createDoors() {
        val rooms = engine.getEntitiesFor(FAMILY)
        rooms.forEach {
            val bounds = Mappers.BOUNDS.get(it)
            val room = Mappers.ROOM.get(it)

            val doorDirection = DIRECTION.random()

            val roomRectangle = getRoomWithWalls(bounds)

            val door = createDoor(roomRectangle, doorDirection)
            room.addDoor(doorDirection, door)
        }
    }

    private fun getRoomWithWalls(bounds: BoundsComponent): Rectangle {
        val room = Rectangle(bounds.rectangle)

        room.setSize(
                room.width + GameConfig.ROOM_BUFFER,
                room.height + GameConfig.ROOM_BUFFER
        )

        return room
    }

    private fun createDoor(rectangle: Rectangle, direction: DIRECTION): Entity {
        val door = engine.createEntity()

        val doorRectangle = createDoorRectangleFrom(rectangle, direction)
        val position = positionComponent(doorRectangle)
        val dimension =  dimensionComponent(doorRectangle)
        val bounds = boundsComponent(position, dimension)
        bounds.color = Color.GREEN

        door.add(position)
        door.add(dimension)
        door.add(bounds)

        engine.addEntity(door)
        return door
    }

    private fun positionComponent(rectangle: Rectangle): PositionComponent {
        val position = engine.createComponent(PositionComponent::class.java)
        position.x = rectangle.x
        position.y = rectangle.y
        return position
    }

    private fun dimensionComponent(rectangle: Rectangle): DimensionComponent {
        val dimension = engine.createComponent(DimensionComponent::class.java)
        dimension.width = rectangle.width
        dimension.height = rectangle.height
        return dimension
    }

    private fun boundsComponent(position: PositionComponent, dimension: DimensionComponent): BoundsComponent {
        val bounds = engine.createComponent(BoundsComponent::class.java)
        bounds.rectangle.setPosition(position.x, position.y)
        bounds.rectangle.setSize(dimension.width, dimension.height)
        return bounds
    }

    private fun createDoorRectangleFrom(rectangle: Rectangle, direction: DIRECTION): Rectangle {
        val doorRectangle = Rectangle()

        val rectangleHalfWidth = Math.round(rectangle.width / 2f).toFloat()
        val rectangleHalfHeight = Math.round(rectangle.height / 2f).toFloat()
        val centerX = rectangle.x + rectangleHalfWidth
        val centerY = rectangle.y + rectangleHalfHeight

        val doorOffset = GameConfig.DOOR_SIZE + GameConfig.DOOR_HALF_SIZE
        var doorX = centerX - 2f
        var doorY = centerY - 2f

        if (direction == DIRECTION.NORTH) {
            doorY += rectangleHalfWidth - doorOffset
        } else if (direction == DIRECTION.SOUTH) {
            doorY += -rectangleHalfHeight + doorOffset
        } else if (direction == DIRECTION.EAST) {
            doorX += rectangleHalfWidth - doorOffset
        } else if (direction == DIRECTION.WEST) {
            doorX += -rectangleHalfWidth + doorOffset
        }

        doorRectangle.setPosition(doorX, doorY)
        doorRectangle.setSize(GameConfig.DOOR_SIZE)

        return doorRectangle
    }

    companion object {
        @JvmStatic
        private val FAMILY = Family.all(RoomComponent::class.java, DimensionComponent::class.java).get()
    }
}

