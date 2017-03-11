package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.config.GameConfig

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
        validRoomsWithWalls.add(createFirstRoom(GameConfig.MEDIUM_ROOM_DIMENSION, GameConfig.MEDIUM_ROOM_DIMENSION))

        generateRooms(GameConfig.LARGE_ROOM_DIMENSION, GameConfig.LARGE_ROOM_DIMENSION)
        generateRooms(GameConfig.SMALL_ROOM_DIMENSION, GameConfig.SMALL_ROOM_DIMENSION)
        generateRooms(GameConfig.MEDIUM_ROOM_DIMENSION, GameConfig.MEDIUM_ROOM_DIMENSION)

        validRoomsWithWalls.forEach {
            val roomEntity = engine.createEntity()

            val position = positionComponent(it)
            val dimension = dimensionComponent(it)
            val bounds = boundsComponent(position, dimension)

            roomEntity.add(position)
            roomEntity.add(dimension)
            roomEntity.add(bounds)

            engine.addEntity(roomEntity)
        }
    }

    private fun generateRooms(roomWidth: Float, roomHeight: Float) {
        for (i in 0..MathUtils.random(GameConfig.ROOM_CREATION_ATTEMPTS)) {

            var rectangle = Rectangle()
            val roomWidthWithWalls = roomWidth + GameConfig.ROOM_BUFFER
            val roomHeightWithWalls = roomHeight + GameConfig.ROOM_BUFFER

            rectangle.setSize(
                    roomWidthWithWalls,
                    roomHeightWithWalls
            )

            for (i in 0..GameConfig.ROOM_CREATION_REPEATED_ATTEMPTS) {

                setRandomPosition(rectangle)

                var roomCanNotBePlaced = false
                validRoomsWithWalls.forEach {
                    if (rectangle.overlaps(it) || it.overlaps(rectangle)) {
                        roomCanNotBePlaced = true
                    }
                }
                if (!roomCanNotBePlaced) {
                    validRoomsWithWalls.add(rectangle)
                    break
                }
            }
        }
    }

    private fun positionComponent(it: Rectangle): PositionComponent {
        val position = engine.createComponent(PositionComponent::class.java)
        position.x = it.x
        position.y = it.y
        return position
    }

    private fun boundsComponent(position: PositionComponent, dimension: DimensionComponent): BoundsComponent? {
        val bounds = engine.createComponent(BoundsComponent::class.java)
        bounds.rectangle.setPosition(position.x, position.y)
        bounds.rectangle.setSize(dimension.width, dimension.height)
        return bounds
    }

    private fun dimensionComponent(it: Rectangle): DimensionComponent {
        val dimension = engine.createComponent(DimensionComponent::class.java)
        dimension.width = it.width - GameConfig.ROOM_BUFFER
        dimension.height = it.height - GameConfig.ROOM_BUFFER
        return dimension
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