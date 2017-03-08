package com.nonnulldev.anotherroom.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.nonnulldev.anotherroom.AnotherRoomGame

object DesktopLauncher {

    @JvmStatic
    fun main(args: Array<String>) {
        val config = LwjglApplicationConfiguration()
        LwjglApplication(AnotherRoomGame(), config)
    }
}
