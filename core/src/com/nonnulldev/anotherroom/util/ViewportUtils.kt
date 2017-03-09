package com.nonnulldev.anotherroom.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.viewport.Viewport


class ViewportUtils private constructor() {

    companion object {

        private val log = Logger(ViewportUtils::class.java.name, Logger.DEBUG)

        private val DEFAULT_CELL_SIZE = 1

        fun drawGrid(viewport: Viewport?, renderer: ShapeRenderer?, cellSize: Int = DEFAULT_CELL_SIZE) {
            if (viewport == null) {
                throw IllegalArgumentException("viewport param is required.")
            }

            if (renderer == null) {
                throw IllegalArgumentException("renderer param is required.")
            }

            val oldColor = Color(renderer.color)

            val worldWidth = viewport.worldWidth.toInt()
            val worldHeight = viewport.worldHeight.toInt()
            val doubleWorldWidth = worldWidth * 2
            val doubleWorldHeight = worldHeight * 2

            renderer.projectionMatrix = viewport.camera.combined
            renderer.begin(ShapeRenderer.ShapeType.Line)

            renderer.color = Color.WHITE

            var x = -doubleWorldWidth
            while (x < doubleWorldWidth) {
                renderer.line(x.toFloat(), (-doubleWorldHeight).toFloat(), x.toFloat(), doubleWorldHeight.toFloat())
                x += cellSize
            }

            var y = -doubleWorldHeight
            while (y < doubleWorldHeight) {
                renderer.line((-doubleWorldWidth).toFloat(), y.toFloat(), doubleWorldWidth.toFloat(), y.toFloat())
                y += cellSize
            }

            renderer.color = Color.RED
            renderer.line(0f, (-doubleWorldHeight).toFloat(), 0f, doubleWorldHeight.toFloat())
            renderer.line((-doubleWorldWidth).toFloat(), 0f, doubleWorldWidth.toFloat(), 0f)

            renderer.color = Color.GREEN
            renderer.line(0f, worldHeight.toFloat(), worldWidth.toFloat(), worldHeight.toFloat())
            renderer.line(worldWidth.toFloat(), 0f, worldWidth.toFloat(), worldHeight.toFloat())

            renderer.end()
            renderer.color = oldColor
        }

        fun debugPixelsPerUnit(viewport: Viewport?) {
            if (viewport == null) {
                throw IllegalArgumentException("viewport param is required.")
            }

            val screenWidth = viewport.screenWidth.toFloat()
            val screenHeight = viewport.screenHeight.toFloat()

            val worldWidth = viewport.worldWidth
            val worldHeight = viewport.worldHeight

            val xPPU = screenWidth / worldWidth
            val yPPU = screenHeight / worldHeight

            log.debug("x PPU= $xPPU yPPU= $yPPU")
        }
    }
}
