package com.nonnulldev.anotherroom.screen.dungeon

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.nonnulldev.anotherroom.AnotherRoomGame
import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.system.debug.DebugCameraSystem
import com.nonnulldev.anotherroom.system.debug.DebugInputSystem
import com.nonnulldev.anotherroom.system.debug.DebugRenderSystem
import com.nonnulldev.anotherroom.system.debug.GridRenderSystem
import com.nonnulldev.anotherroom.system.passive.DoorGenerationSystem
import com.nonnulldev.anotherroom.system.passive.PathGenerationSystem
import com.nonnulldev.anotherroom.system.passive.RoomGenerationSystem
import com.nonnulldev.anotherroom.util.GdxUtils

class DungeonScreen(private val game: AnotherRoomGame) : ScreenAdapter() {

    private val log = Logger(AnotherRoomGame::class.java.name, Logger.DEBUG)

    private val batch = game.batch

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var renderer: ShapeRenderer
    private lateinit var engine: PooledEngine

    override fun show() {
        camera = OrthographicCamera()
        viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
        renderer = ShapeRenderer()
        engine = PooledEngine()

        addSystemsToEngine()
    }

    private fun addSystemsToEngine() {
        addDebugSystemsToEngine()
        engine.addSystem(RoomGenerationSystem())
        engine.addSystem(DoorGenerationSystem())
        engine.addSystem(PathGenerationSystem())
    }

    private fun addDebugSystemsToEngine() {
        engine.addSystem(GridRenderSystem(viewport, renderer))
        engine.addSystem(DebugCameraSystem(
                GameConfig.WORLD_CENTER_X,
                GameConfig.WORLD_CENTER_Y,
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
}