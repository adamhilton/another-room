package com.nonnulldev.anotherroom.util.debug

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Logger

class DebugCameraController {

    private val position = Vector2()
    private val startPosition = Vector2()
    private var zoom = 1.0f
    private var info: DebugCameraInfo? = null

    init {
        init()
    }

    private fun init() {
        info = DebugCameraInfo()

        log.info("cameraInfo= " + info!!)
    }

    fun setStartPosition(x: Float, y: Float) {
        startPosition.set(x, y)
        setPosition(x, y)
    }

    fun applyTo(camera: OrthographicCamera?) {
        if (camera == null) {
            throw IllegalArgumentException("camera cannot be null.")
        }

        camera.position.set(position, 0f)
        camera.zoom = zoom
        camera.update()
    }

    fun handleDebugInput(delta: Float) {
        if (Gdx.app.type != Application.ApplicationType.Desktop) {
            return
        }

        val moveSpeed = info!!.moveSpeed * delta
        val zoomSpeed = info!!.zoomSpeed * delta

        if (info!!.isLeftPressed) {
            moveLeft(moveSpeed)
        }

        if (info!!.isRightPressed) {
            moveRight(moveSpeed)
        }

        if (info!!.isUpPressed) {
            moveUp(moveSpeed)
        }

        if (info!!.isDownPressed) {
            moveDown(moveSpeed)
        }

        if (info!!.isZoomInPressed) {
            zoomIn(zoomSpeed)
        }
        if (info!!.isZoomOutPressed) {
            zoomOut(zoomSpeed)
        }

        if (info!!.isResetPressed) {
            reset()
        }

        if (info!!.isLogPressed) {
            logDebug()
        }

    }

    private fun setPosition(x: Float, y: Float) {
        position.set(x, y)
    }

    private fun setZoom(value: Float) {
        zoom = MathUtils.clamp(value, info!!.maxZoomIn, info!!.maxZoomOut)
    }

    private fun moveCamera(xSpeed: Float, ySpeed: Float) {
        setPosition(position.x + xSpeed, position.y + ySpeed)
    }

    private fun moveLeft(speed: Float) {
        moveCamera(-speed, 0f)
    }

    private fun moveRight(speed: Float) {
        moveCamera(speed, 0f)
    }

    private fun moveUp(speed: Float) {
        moveCamera(0f, speed)
    }

    private fun moveDown(speed: Float) {
        moveCamera(0f, -speed)
    }

    private fun zoomIn(zoomSpeed: Float) {
        setZoom(zoom + zoomSpeed)
    }

    private fun zoomOut(zoomSpeed: Float) {
        setZoom(zoom - zoomSpeed)
    }

    private fun reset() {
        position.set(startPosition)
        setZoom(1.0f)
    }

    private fun logDebug() {
        log.debug("position= $position zoom= $zoom")
    }

    companion object {

        private val log = Logger(DebugCameraController::class.java.name, Logger.DEBUG)
    }
}
