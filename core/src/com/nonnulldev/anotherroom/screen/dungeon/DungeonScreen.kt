package com.nonnulldev.anotherroom.screen.dungeon

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.nonnulldev.anotherroom.AnotherRoomGame
import com.nonnulldev.anotherroom.assets.AssetDescriptors
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.input.DungeonScreenInput
import com.nonnulldev.anotherroom.system.DungeonRenderSystem
import com.nonnulldev.anotherroom.system.PlayerRenderSystem
import com.nonnulldev.anotherroom.system.generation.passive.DungeonGenerationSystem
import com.nonnulldev.anotherroom.system.physics.ProcessPhysicsSystem
import com.nonnulldev.anotherroom.system.physics.debug.Box2DDebugRenderSystem
import com.nonnulldev.anotherroom.system.physics.passive.CreatePlayerPhysicsSystem
import com.nonnulldev.anotherroom.system.physics.passive.EarthBoundaryPhysicsSystem
import com.nonnulldev.anotherroom.system.player.AddPlayerToStartingRoomSystem
import com.nonnulldev.anotherroom.system.player.PlayerCameraSystem
import com.nonnulldev.anotherroom.system.player.PlayerMovementSystem
import com.nonnulldev.anotherroom.system.player.passive.CreatePlayerSystem
import com.nonnulldev.anotherroom.util.GdxUtils

class DungeonScreen(game: AnotherRoomGame) : ScreenAdapter(),
        DungeonScreenInput.Listener,
        DungeonGenerationSystem.Listener {

    private val log = Logger(AnotherRoomGame::class.java.name, Logger.DEBUG)
    private val isDebug = true

    private val batch = game.batch
    private val assetManager = game.assetManager

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var renderer: ShapeRenderer
    private lateinit var engine: PooledEngine
    private lateinit var world: World

    private var dungeonGenerationFailed = false

    override fun show() {
        camera = OrthographicCamera()
        viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
        renderer = ShapeRenderer()
        engine = PooledEngine()
        world = createWorld()

        assetManager.load(AssetDescriptors.GAME)
        assetManager.finishLoading()

        addSystemsToEngine()

        Gdx.input.inputProcessor = DungeonScreenInput(this)
    }

    private fun createWorld() = World(Vector2(0f, 0f), true)

    private fun addSystemsToEngine() {
        engine.addSystem(DungeonGenerationSystem(this, assetManager))

        if (dungeonGenerationFailed) {
            refresh()
            return
        }
        engine.addSystem(EarthBoundaryPhysicsSystem(world))

        engine.addSystem(CreatePlayerSystem(assetManager))
        engine.addSystem(AddPlayerToStartingRoomSystem())
        engine.addSystem(CreatePlayerPhysicsSystem(world))

        engine.addSystem(PlayerMovementSystem())
        engine.addSystem(ProcessPhysicsSystem(world))
        engine.addSystem(PlayerCameraSystem(camera))

        engine.addSystem(DungeonRenderSystem(viewport, batch))
        engine.addSystem(PlayerRenderSystem(viewport, batch))

        if (isDebug) {
            engine.addSystem(Box2DDebugRenderSystem(world, camera))
        }
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }
        GdxUtils.clearScreen()
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        renderer.dispose()
        engine.removeAllEntities()
        world.dispose()
    }

    override fun refresh() {
        dungeonGenerationFailed = false

        engine.clearPools()
        engine.removeAllEntities()
        engine.systems.forEach {
            engine.removeSystem(it)
        }
        engine = PooledEngine()

        world.dispose()
        world = createWorld()
        
        addSystemsToEngine()

        return
    }

    override fun dungeonGenerationSystemFailed() {
        dungeonGenerationFailed = true
    }
}