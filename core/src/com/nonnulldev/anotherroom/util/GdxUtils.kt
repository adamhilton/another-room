package com.nonnulldev.anotherroom.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20

class GdxUtils private constructor() {

    companion object {
        fun clearScreen() {
            clearScreen(Color.BLACK)
        }

        fun clearScreen(color: Color) {
            Gdx.gl.glClearColor(color.r, color.g, color.b, color.a)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        }
    }
}

