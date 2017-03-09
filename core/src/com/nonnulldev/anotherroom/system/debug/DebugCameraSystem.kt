package com.nonnulldev.anotherroom.system.debug

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.nonnulldev.anotherroom.util.debug.DebugCameraController


class DebugCameraSystem(startX: Float, startY: Float, private val camera: OrthographicCamera) : EntitySystem() {

    init {
        DEBUG_CAMERA_CONTROLLER.setStartPosition(startX, startY)
    }

    override fun update(deltaTime: Float) {
        DEBUG_CAMERA_CONTROLLER.handleDebugInput(deltaTime)
        DEBUG_CAMERA_CONTROLLER.applyTo(camera)
    }

    companion object {

        private val DEBUG_CAMERA_CONTROLLER = DebugCameraController()
    }
}
