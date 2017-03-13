package com.nonnulldev.anotherroom.system.passive

import com.badlogic.ashley.core.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.BoundsComponent
import com.nonnulldev.anotherroom.component.DimensionComponent
import com.nonnulldev.anotherroom.component.PositionComponent
import com.nonnulldev.anotherroom.component.RoomComponent
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.enum.Direction
import com.nonnulldev.anotherroom.util.ListUtility
import com.nonnulldev.anotherroom.util.Mappers
import java.util.*

class PathGenerationSystem : EntitySystem() {

    private val log = Logger(PathGenerationSystem::class.simpleName, Logger.DEBUG)

    lateinit private var engine: PooledEngine

    private var validPaths = ArrayList<Rectangle>()
    private var roomBounds = ArrayList<Rectangle>()
    private var doorBounds = ArrayList<Rectangle>()

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine
        createPaths()
    }

    private fun createPaths() {
        initBoundsFromEngine()

        doorBounds.forEach {

            log.debug("Starting path from Door #${doorBounds.indexOf(it)}")
            var pathTileBounds = Rectangle(
                    it.x, it.y,
                    GameConfig.PATH_SIZE, GameConfig.PATH_SIZE
            )

            var pathCanBeGenerated = true
            while(pathCanBeGenerated) {
                try {
                    pathTileBounds = Rectangle(getNextPathTile(pathTileBounds))
                    log.debug("Adding path at x: ${pathTileBounds.x} y: ${pathTileBounds.y}")
                    validPaths.add(pathTileBounds)
                    addPathToEngine(pathTileBounds)
                } catch(e: Exception) {
                    pathCanBeGenerated = false
                }
            }
            log.debug("Ending path from Door #${doorBounds.indexOf(it)}")
        }
    }

    private fun initBoundsFromEngine() {
        val rooms = engine.getEntitiesFor(FAMILY)
        rooms.forEach {
            roomBounds.add(Mappers.BOUNDS.get(it).rectangle)

            val room = Mappers.ROOM.get(it)
            val doors = room.doors

            doors.forEach {
                doorBounds.add(Mappers.BOUNDS.get(it.value).rectangle)
            }
        }
    }

    private fun addPathToEngine(pathTileBounds: Rectangle) {
        val pathEntity = engine.createEntity()
        val position = positionComponent(pathTileBounds)
        val dimension = dimensionComponent(pathTileBounds)
        val bounds = boundsComponent(position, dimension)
        bounds.color = Color.BLUE

        pathEntity.add(position)
        pathEntity.add(dimension)
        pathEntity.add(bounds)

        engine.addEntity(pathEntity)
    }

    private fun overlapsOtherBounds(bounds: Rectangle): Boolean {

        validPaths.forEach {
            if (bounds.overlaps(it)) {
                log.debug("Overlaps a PATH")
                return true
            }
        }

        roomBounds.forEach {
            if (bounds.overlaps(getRoomWithWalls(it))) {
                log.debug("Overlaps a ROOM")
                return true
            }
        }

        doorBounds.forEach {
            if (bounds.overlaps(it)) {
                log.debug("Overlaps a DOOR")
                return true
            }
        }

        if (bounds.x < GameConfig.WALL_SIZE || bounds.x >= GameConfig.WORLD_WIDTH - GameConfig.WALL_SIZE||
                bounds.y < GameConfig.WALL_SIZE || bounds.y >= GameConfig.WORLD_HEIGHT - GameConfig.WALL_SIZE) {
            return true
        }

        return false
    }

    fun getNextPathTile(pathTileBounds: Rectangle): Rectangle {
        val directions = ListUtility.shuffle(Direction.values().toList() as MutableList<Direction>)

        directions.forEach {
            val nextPathTile = move(pathTileBounds, it)
            if (!overlapsOtherBounds(nextPathTile)) {
                return nextPathTile
            }
        }
        throw Exception()
    }

    fun move(pathTileBounds: Rectangle, direction: Direction): Rectangle {
        val nextPathTile = Rectangle(pathTileBounds)
        if (direction == Direction.NORTH) {
            nextPathTile.y += GameConfig.PATH_SIZE
        } else if (direction == Direction.SOUTH) {
            nextPathTile.y -= GameConfig.PATH_SIZE
        } else if (direction == Direction.EAST) {
            nextPathTile.x += GameConfig.PATH_SIZE
        } else if (direction == Direction.WEST) {
            nextPathTile.x -= GameConfig.PATH_SIZE
        }
        return nextPathTile
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

    private fun getRoomWithWalls(bounds: Rectangle): Rectangle {
        val room = Rectangle(bounds)

        room.setSize(
                room.width + GameConfig.ROOM_TO_ROOM_BUFFER,
                room.height + GameConfig.ROOM_TO_ROOM_BUFFER
        )

        room.setPosition(
                room.x - (GameConfig.WALL_SIZE * 2),
                room.y - (GameConfig.WALL_SIZE * 2)
        )

        return room
    }

    companion object {
        @JvmStatic
        private val FAMILY = Family.all(RoomComponent::class.java, DimensionComponent::class.java).get()
    }
}