package com.nonnulldev.anotherroom.system.player

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.component.*
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.util.Mappers

class AddPlayerToStartingRoomSystem : EntitySystem() {

    val log = Logger(AddPlayerToStartingRoomSystem::class.java.simpleName, Logger.DEBUG)


    val PLAYER_FAMILY: Family = Family.all(
            PlayerComponent::class.java,
            PositionComponent::class.java
    ).get()

    val STARTING_ROOM_FAMILY: Family = Family.all(
            StartingRoomComponent::class.java,
            PositionComponent::class.java,
            DimensionComponent::class.java
    ).get()

    private lateinit var engine: PooledEngine

    override fun checkProcessing(): Boolean {
        return false
    }

    override fun addedToEngine(engine: Engine?) {
        this.engine = engine as PooledEngine

        val player = engine.getEntitiesFor(PLAYER_FAMILY).first()
        val startingRoom = engine.getEntitiesFor(STARTING_ROOM_FAMILY).first()

        val startingRoomPosition = Mappers.POSITION.get(startingRoom)
        val startingRoomDimension = Mappers.DIMENSION.get(startingRoom)

        val playerPosition = Mappers.POSITION.get(player)
        playerPosition.x = startingRoomPosition.x + (startingRoomDimension.width / 2f) - (GameConfig.PLAYER_SIZE / 2f)
        playerPosition.y = startingRoomPosition.y + (startingRoomDimension.height / 2f) - (GameConfig.PLAYER_SIZE / 2f)


        log.debug("Player position: x: ${playerPosition.x} y: ${playerPosition.y}")
    }

}
