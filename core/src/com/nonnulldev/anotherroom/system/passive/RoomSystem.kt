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

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        log.debug("Starting room creation")
        var entityCount = engine.entities.count()
        log.debug("Entity count: $entityCount")
        createRooms()
        entityCount = engine.entities.count()
        log.debug("Room completion complete!")
        log.debug("Entity count: $entityCount")
    }

    fun createRooms() {

        var validRooms = ArrayList<Rectangle>()
        validRooms.add(createFirstRoom(GameConfig.MEDIUM_ROOM_DIMENSION, GameConfig.MEDIUM_ROOM_DIMENSION))
        for (i in 0..1000) {

            var rectangle = Rectangle()
            val roomWidth = GameConfig.MEDIUM_ROOM_DIMENSION
            val roomHeight = GameConfig.MEDIUM_ROOM_DIMENSION
            val roomWidthWithWalls = roomWidth + (GameConfig.WALL_SIZE * 2)
            val roomHeightWithWalls = roomHeight + (GameConfig.WALL_SIZE * 2)

            rectangle.setSize(
                    roomWidthWithWalls,
                    roomHeightWithWalls
            )

            var numOfTries = 20
            for (i in 0..numOfTries) {

                setRandomPosition(rectangle)

                var roomCanNotBePlaced = false
                validRooms.forEach {
                    if(rectangle.overlaps(it)) {
                        roomCanNotBePlaced = true
                    }
                }
                if (!roomCanNotBePlaced) {
                    rectangle.setSize(GameConfig.MEDIUM_ROOM_DIMENSION, GameConfig.MEDIUM_ROOM_DIMENSION)
                    validRooms.add(rectangle)
                    break
                }
            }
        }

        validRooms.forEach {
            val position = engine.createComponent(PositionComponent::class.java)
            position.x = it.x
            position.y = it.y

            val dimension = engine.createComponent(DimensionComponent::class.java)
            dimension.width = it.width
            dimension.height = it.height

            val bounds = engine.createComponent(BoundsComponent::class.java)
            bounds.rectangle = it

            val roomEntity = engine.createEntity()
            roomEntity.add(position)
            roomEntity.add(dimension)
            roomEntity.add(bounds)

            engine.addEntity(roomEntity)
        }
    }

    private fun setRandomPosition(rectangle: Rectangle) {
        val maxX = GameConfig.WORLD_WIDTH - GameConfig.MEDIUM_ROOM_DIMENSION - 1f
        val minX = 1f
        val rectangleX = Math.round(MathUtils.random(
                minX, maxX)).toFloat()

        val maxY = GameConfig.WORLD_HEIGHT - GameConfig.MEDIUM_ROOM_DIMENSION - 1f
        val minY = 1f
        val rectangleY = Math.round(MathUtils.random(
                minY, maxY)).toFloat()

        rectangle.setPosition(rectangleX, rectangleY)
    }

    private fun createFirstRoom(roomWidth: Float, roomHeight: Float): Rectangle {
        return Rectangle(
                GameConfig.WORLD_CENTER_X - (roomWidth / 2f),
                GameConfig.WORLD_CENTER_Y - (roomHeight / 2f),
                roomWidth,
                roomHeight
        )
    }
}