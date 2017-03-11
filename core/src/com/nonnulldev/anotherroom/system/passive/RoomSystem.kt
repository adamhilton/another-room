package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.component.RoomComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.enum.DIRECTION

class RoomSystem : EntitySystem() {

    private val log = Logger(RoomSystem::class.simpleName, Logger.DEBUG)

    lateinit private var engine: PooledEngine

    private var validRoomsWithWalls = ArrayList<Rectangle>()

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        createRooms()
    }

    fun createRooms() {
        validRoomsWithWalls.add(createFirstRoom(GameConfig.SMALL_ROOM_DIMENSION, GameConfig.SMALL_ROOM_DIMENSION))

        generateRooms(GameConfig.LARGE_ROOM_DIMENSION, GameConfig.LARGE_ROOM_DIMENSION)
        generateRooms(GameConfig.SMALL_ROOM_DIMENSION, GameConfig.SMALL_ROOM_DIMENSION)
        generateRooms(GameConfig.MEDIUM_ROOM_DIMENSION, GameConfig.MEDIUM_ROOM_DIMENSION)

        validRoomsWithWalls.forEach {
            val roomEntity = engine.createEntity()

            val position = positionComponent(it)
            val dimension = dimensionComponentWithRoomBuffer(it)
            val bounds = boundsComponent(position, dimension)
            bounds.color = Color.RED
            val room = roomComponent(it)

            roomEntity.add(position)
            roomEntity.add(dimension)
            roomEntity.add(bounds)
            roomEntity.add(room)

            engine.addEntity(roomEntity)
        }
    }

    private fun generateRooms(roomWidth: Float, roomHeight: Float) {
        for (i in 0..GameConfig.ROOM_CREATION_ATTEMPTS) {

            var rectangle = Rectangle()
            val roomWidthWithWalls = roomWidth + GameConfig.ROOM_BUFFER
            val roomHeightWithWalls = roomHeight + GameConfig.ROOM_BUFFER

            rectangle.setSize(
                    roomWidthWithWalls,
                    roomHeightWithWalls
            )

            setRandomPosition(rectangle)

            var roomCanNotBePlaced = false
            validRoomsWithWalls.forEach {
                if (rectangle.overlaps(it) || it.overlaps(rectangle)) {
                    roomCanNotBePlaced = true
                }
            }
            if (!roomCanNotBePlaced) {
                validRoomsWithWalls.add(rectangle)
            }
        }
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

    private fun dimensionComponentWithRoomBuffer(rectangle: Rectangle): DimensionComponent {
        val dimension = dimensionComponent(rectangle)
        dimension.width += -GameConfig.ROOM_BUFFER
        dimension.height += -GameConfig.ROOM_BUFFER
        return dimension
    }

    private fun boundsComponent(position: PositionComponent, dimension: DimensionComponent): BoundsComponent {
        val bounds = engine.createComponent(BoundsComponent::class.java)
        bounds.rectangle.setPosition(position.x, position.y)
        bounds.rectangle.setSize(dimension.width, dimension.height)
        return bounds
    }

    private fun roomComponent(rectangle: Rectangle): RoomComponent {
        val room = engine.createComponent(RoomComponent::class.java)
        val doorDirection = DIRECTION.random()
        val door = createDoor(rectangle, doorDirection)
        room.addDoor(doorDirection, door)
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

    private fun setRandomPosition(rectangle: Rectangle) {
        val maxX = GameConfig.WORLD_WIDTH - rectangle.width - 1f
        val minX = 1f
        val rectangleX = Math.round(MathUtils.random(
                minX, maxX)).toFloat()

        val maxY = GameConfig.WORLD_HEIGHT - rectangle.width - 1f
        val minY = 1f
        val rectangleY = Math.round(MathUtils.random(
                minY, maxY)).toFloat()

        rectangle.setPosition(rectangleX, rectangleY)
    }

    private fun createFirstRoom(roomWidth: Float, roomHeight: Float): Rectangle {
        return Rectangle(
                GameConfig.WORLD_CENTER_X - (roomWidth / 2f),
                GameConfig.WORLD_CENTER_Y - (roomHeight / 2f),
                roomWidth + GameConfig.ROOM_BUFFER,
                roomHeight + GameConfig.ROOM_BUFFER
        )
    }
}