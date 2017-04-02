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
import com.nonnulldev.anotherroom.common.assets.AssetDescriptors
import com.nonnulldev.anotherroom.common.config.GameConfig
import com.nonnulldev.anotherroom.screen.dungeon.input.DungeonScreenInput
import com.nonnulldev.anotherroom.dungeon.system.render.DungeonRenderSystem
import com.nonnulldev.anotherroom.player.system.PlayerRenderSystem
import com.nonnulldev.anotherroom.dungeon.system.creation.DungeonGenerationSystem
import com.nonnulldev.anotherroom.common.system.ProcessPhysicsSystem
import com.nonnulldev.anotherroom.common.system.debug.Box2DDebugRenderSystem
import com.nonnulldev.anotherroom.player.system.physics.CreatePlayerPhysicsSystem
import com.nonnulldev.anotherroom.dungeon.system.physics.EarthBoundaryPhysicsSystem
import com.nonnulldev.anotherroom.player.system.creation.AddPlayerToStartingRoomSystem
import com.nonnulldev.anotherroom.player.system.PlayerCameraSystem
import com.nonnulldev.anotherroom.player.system.PlayerMovementSystem
import com.nonnulldev.anotherroom.player.system.creation.CreatePlayerSystem
import com.nonnulldev.anotherroom.common.util.GdxUtils

class DungeonScreen(game: AnotherRoomGame) : ScreenAdapter(),
        DungeonScreenInput.Listener,
        DungeonGenerationSystem.Listener {

    private val log = Logger(AnotherRoomGame::class.java.name, Logger.DEBUG)
    private val isDebug = true
    private val shouldRenderTextures = false
    private val shouldUsePlayerCamera = false

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

        if (shouldUsePlayerCamera) {
            engine.addSystem(PlayerCameraSystem(camera))
        }

        if (shouldRenderTextures) {
            engine.addSystem(DungeonRenderSystem(viewport, batch))
            engine.addSystem(PlayerRenderSystem(viewport, batch))
        }

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