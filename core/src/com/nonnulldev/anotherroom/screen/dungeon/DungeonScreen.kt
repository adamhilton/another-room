package com.nonnulldev.anotherroom.screen.dungeon

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.nonnulldev.anotherroom.AnotherRoomGame
import com.nonnulldev.anotherroom.assets.AssetDescriptors
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.input.DungeonScreenInput
import com.nonnulldev.anotherroom.system.AddPlayerToStartingRoomSystem
import com.nonnulldev.anotherroom.system.PlayerCameraSystem
import com.nonnulldev.anotherroom.system.RenderSystem
import com.nonnulldev.anotherroom.system.debug.DebugCameraSystem
import com.nonnulldev.anotherroom.system.debug.DebugInputSystem
import com.nonnulldev.anotherroom.system.debug.DebugRenderSystem
import com.nonnulldev.anotherroom.system.debug.GridRenderSystem
import com.nonnulldev.anotherroom.system.passive.entities.CreatePlayerSystem
import com.nonnulldev.anotherroom.system.passive.generation.DungeonGenerationSystem
import com.nonnulldev.anotherroom.util.GdxUtils

class DungeonScreen(game: AnotherRoomGame) : ScreenAdapter(),
        DungeonScreenInput.Listener,
        DungeonGenerationSystem.Listener {

    private val log = Logger(AnotherRoomGame::class.java.name, Logger.DEBUG)
    private val isDebug = false

    private val batch = game.batch
    private val assetManager = game.assetManager

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var renderer: ShapeRenderer
    private lateinit var engine: PooledEngine

    override fun show() {
        camera = OrthographicCamera()
        viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
        renderer = ShapeRenderer()
        engine = PooledEngine()

        assetManager.load(AssetDescriptors.GAME)
        assetManager.finishLoading()

        addSystemsToEngine()

        Gdx.input.inputProcessor = DungeonScreenInput(this)
    }

    private fun addSystemsToEngine() {
        engine.addSystem(DungeonGenerationSystem(this, assetManager))

        engine.addSystem(CreatePlayerSystem(assetManager))

        engine.addSystem(AddPlayerToStartingRoomSystem())

        engine.addSystem(PlayerCameraSystem(camera, batch))

        engine.addSystem(RenderSystem(viewport, batch))

        if (isDebug) {
            addDebugSystemsToEngine()
        }
    }

    private fun addDebugSystemsToEngine() {
        engine.addSystem(GridRenderSystem(viewport, renderer))
        engine.addSystem(DebugCameraSystem(
                GameConfig.WORLD_CENTER_X,
                GameConfig.WORLD_CENTER_Y,
                GameConfig.PLAYER_ZOOM,
                camera
        ))
        engine.addSystem(DebugRenderSystem(viewport, renderer))
        engine.addSystem(DebugInputSystem())
    }

    override fun render(delta: Float) {
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
    }

    override fun refresh() {
        engine.clearPools()
        engine.removeAllEntities()
        engine.systems.forEach {
            engine.removeSystem(it)
        }
        engine = PooledEngine()
        addSystemsToEngine()
    }

    override fun dungeonGenerationSystemFailed() {
        refresh()
    }
}