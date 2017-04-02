package com.nonnulldev.anotherroom.dungeon.system.creation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.common.assets.AssetDescriptors
import com.nonnulldev.anotherroom.common.assets.RegionNames
import com.nonnulldev.anotherroom.common.component.DimensionComponent
import com.nonnulldev.anotherroom.common.component.PositionComponent
import com.nonnulldev.anotherroom.common.component.TextureComponent
import com.nonnulldev.anotherroom.common.component.ZOrderComponent
import com.nonnulldev.anotherroom.dungeon.component.DungeonTileComponent
import com.nonnulldev.anotherroom.dungeon.component.StartingRoomComponent
import com.nonnulldev.anotherroom.common.config.GameConfig
import com.nonnulldev.anotherroom.dungeon.data.DungeonCreationObject
import com.nonnulldev.anotherroom.dungeon.enum.DungeonTileTypes
import com.nonnulldev.anotherroom.common.enum.Orientation
import com.nonnulldev.anotherroom.dungeon.types.array2dOfDungeonTiles


class DungeonGenerationSystem(private val listener: DungeonGenerationSystem.Listener, assetManager: AssetManager) : EntitySystem(), RegionConnectorSystem.Listener {

    private val log = Logger(DungeonGenerationSystem::class.simpleName, Logger.DEBUG)

    private var dungeon = DungeonCreationObject(array2dOfDungeonTiles(GameConfig.Companion.WORLD_WIDTH.toInt(), GameConfig.Companion.WORLD_HEIGHT.toInt()))

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
        engine.addSystem(DungeonBoundariesSystem(dungeon))
    }

    private fun processEntities() {
        for (x in 0..dungeon.grid.lastIndex) {
            for (y in 0..dungeon.grid[x].lastIndex) {

                var tile = dungeon.grid[x][y]

                val position = positionComponent(x.toFloat(), y.toFloat())
                val dimension = dimensionComponent(1f, 1f)
                val texture = textureComponent()
                val dungeonTile = engine.createComponent(DungeonTileComponent::class.java)
                val zOrder = engine.createComponent(ZOrderComponent::class.java)
                zOrder.z = 0

                if (tile.type == DungeonTileTypes.Earth) {
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_EARTH)
                } else if (tile.type == DungeonTileTypes.Room) {
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_FLOOR)
                } else if (tile.type == DungeonTileTypes.Path) {
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_FLOOR)
                } else if (tile.type == DungeonTileTypes.Door) {
                    texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_DOOR)
                    if(tile.orientation == Orientation.VERTICAL)
                        texture.region = gameAtlas.findRegion(RegionNames.DUNGEON_ROOM_DOOR_VERTICAL)
                }

                val entity = engine.createEntity()
                entity.add(position)
                entity.add(dimension)
                entity.add(texture)
                entity.add(dungeonTile)
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

    private fun textureComponent(): TextureComponent {
        val texture = engine.createComponent(TextureComponent::class.java)
        return texture
    }

    interface Listener {
        fun dungeonGenerationSystemFailed()
    }
}
