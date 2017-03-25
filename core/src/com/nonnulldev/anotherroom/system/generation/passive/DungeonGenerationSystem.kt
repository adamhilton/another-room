package com.nonnulldev.anotherroom.system.generation.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.assets.AssetDescriptors
import com.nonnulldev.anotherroom.assets.RegionNames
import com.nonnulldev.anotherroom.component.*
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.Dungeon
import com.nonnulldev.anotherroom.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.enum.Orientation
import com.nonnulldev.anotherroom.types.array2dOfDungeonTiles


class DungeonGenerationSystem(private val listener: Listener, assetManager: AssetManager) : EntitySystem(), RegionConnectorSystem.Listener {

    private val log = Logger(DungeonGenerationSystem::class.simpleName, Logger.DEBUG)

    private var dungeon = Dungeon(array2dOfDungeonTiles(GameConfig.Companion.WORLD_WIDTH.toInt(), GameConfig.Companion.WORLD_HEIGHT.toInt()))

    private lateinit var engine: PooledEngine

    private val gameAtlas = assetManager.get(AssetDescriptors.GAME)

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        runGenerationSystems()
        processEntities()
    }

    private fun runGenerationSystems() {
        engine.addSystem(RoomGenerationSystem(dungeon))
        engine.addSystem(PathGenerationSystem(dungeon))
        engine.addSystem(RegionConnectorSystem(dungeon, this))
        engine.addSystem(PathCleanupSystem(dungeon))
        engine.addSystem(ConnectorCleanupSystem(dungeon))
    }

    private fun processEntities() {
        for (x in 0..dungeon.grid.lastIndex) {
            for (y in 0..dungeon.grid[x].lastIndex) {

                var tile = dungeon.grid[x][y]

                val position = positionComponent(x.toFloat(), y.toFloat())
                val dimension = dimensionComponent(1f, 1f)
                val bounds = boundsComponent(position, dimension)
                val texture = textureComponent()
                val zOrder = engine.createComponent(ZOrderComponent::class.java)
                zOrder.z = 0

                if (tile.type == DungeonTileTypes.Earth) {
                    bounds.color = Color.WHITE
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_EARTH)
                } else if (tile.type == DungeonTileTypes.Room) {
                    bounds.color = Color.BLUE
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_FLOOR)
                } else if (tile.type == DungeonTileTypes.Path) {
                    bounds.color = Color.GOLD
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_FLOOR)
                } else if (tile.type == DungeonTileTypes.Door) {
                    bounds.color = Color.GREEN
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_DOOR)
                    if(tile.orientation == Orientation.VERTICAL)
                        texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_DOOR_VERTICAL)
                }

                val entity = engine.createEntity()
                entity.add(position)
                entity.add(dimension)
                entity.add(bounds)
                entity.add(texture)
                entity.add(zOrder)

                engine.addEntity(entity)
            }
        }

        val room = dungeon.rooms.first()

        val startingRoom = engine.createComponent(StartingRoomComponent::class.java)
        val position = engine.createComponent(PositionComponent::class.java)
        position.x = room.coordinates.x.toFloat()
        position.y = room.coordinates.y.toFloat()
        val dimension = engine.createComponent(DimensionComponent::class.java)
        dimension.width = room.dimension.width.toFloat()
        dimension.height = room.dimension.height.toFloat()
        val startingRoomEntity = engine.createEntity()

        startingRoomEntity.add(startingRoom)
        startingRoomEntity.add(position)
        startingRoomEntity.add(dimension)

        engine.addEntity(startingRoomEntity)
    }

    override fun regionConnectorSystemFailed() {
        listener.dungeonGenerationSystemFailed()
    }

    private fun positionComponent(x: Float, y: Float): PositionComponent {
        val position = engine.createComponent(PositionComponent::class.java)
        position.x = x
        position.y = y
        return position
    }

    private fun dimensionComponent(width: Float, height: Float): DimensionComponent {
        val dimension = engine.createComponent(DimensionComponent::class.java)
        dimension.width = width
        dimension.height = height
        return dimension
    }

    private fun boundsComponent(position: PositionComponent, dimension: DimensionComponent): BoundsComponent {
        val bounds = engine.createComponent(BoundsComponent::class.java)
        bounds.rectangle.setPosition(position.x, position.y)
        bounds.rectangle.setSize(dimension.width, dimension.height)
        return bounds
    }

    private fun textureComponent(): TextureComponent {
        val texture = engine.createComponent(TextureComponent::class.java)
        return texture
    }

    interface Listener {
        fun dungeonGenerationSystemFailed()
    }
}
