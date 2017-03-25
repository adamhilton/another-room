package com.nonnulldev.anotherroom.system.debug

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

class DebugInputSystem : EntitySystem() {

    private var debugGrid = true
    private var debugRender = true
    private var gridRenderSystem: EntitySystem? = null
    private var debugRenderSystem: EntitySystem? = null

    override fun addedToEngine(engine: Engine?) {
        gridRenderSystem = engine!!.getSystem(GridRenderSystem::class.java)
        debugRenderSystem = engine.getSystem(DebugRenderSystem::class.java)
        toggleSystems()
    }

    override fun update(deltaTime: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.F5)) {
            debugGrid = !debugGrid
            toggleSystems()
        } else if (Gdx.input.isKeyPressed(Input.Keys.F6)) {
            debugRender = !debugRender
            toggleSystems()
        }
    }

    private fun toggleSystems() {
        gridRenderSystem!!.setProcessing(debugGrid)
        debugRenderSystem!!.setProcessing(debugRender)
    }
}
