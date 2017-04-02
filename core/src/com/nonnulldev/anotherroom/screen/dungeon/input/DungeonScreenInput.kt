package com.nonnulldev.anotherroom.screen.dungeon.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter

class DungeonScreenInput(private val listener: Listener) : InputAdapter() {
    override fun keyUp(keycode: Int): Boolean {
        if (keycode == Input.Keys.R) {
            listener.refresh()
        }
        return true
    }

    interface Listener {
        fun refresh()
    }
}
