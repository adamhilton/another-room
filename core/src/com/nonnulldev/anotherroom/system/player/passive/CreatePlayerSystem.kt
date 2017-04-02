package com.nonnulldev.anotherroom.system.player.passive

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.nonnulldev.anotherroom.assets.AssetDescriptors
import com.nonnulldev.anotherroom.assets.RegionNames
import com.nonnulldev.anotherroom.component.*
import com.nonnulldev.anotherroom.config.GameConfig

class CreatePlayerSystem(assetManager: AssetManager) : EntitySystem() {

    private lateinit var engine: PooledEngine

    private val FAMILY: Family = Family.all(PlayerComponent::class.java).get()

    private val gameAtlas = assetManager.get(AssetDescriptors.GAME)

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        removeAnyOtherPlayers(engine)

        val player = engine.createComponent(PlayerComponent::class.java)
        val position = engine.createComponent(PositionComponent::class.java)
        val dimension = engine.createComponent(DimensionComponent::class.java)
        dimension.width = GameConfig.PLAYER_SIZE
        dimension.height = GameConfig.PLAYER_SIZE
        val texture = engine.createComponent(TextureComponent::class.java)
        texture.region = gameAtlas.findRegion(RegionNames.PLAYER)
        val zOrder = engine.createComponent(ZOrderComponent::class.java)
        zOrder.z = 1

        val entity = engine.createEntity()
        entity.add(player)
        entity.add(position)
        entity.add(dimension)
        entity.add(texture)
        entity.add(zOrder)

        engine.addEntity(entity)
    }

    private fun removeAnyOtherPlayers(engine: Engine) {
        val playerEntities = engine.getEntitiesFor(FAMILY)
        if (playerEntities.count() != 0) {
            playerEntities.forEach {
                engine.removeEntity(it)
            }
        }
    }
}

