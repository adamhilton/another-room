package com.nonnulldev.anotherroom.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.nonnulldev.anotherroom.AnotherRoomGame
import com.nonnulldev.anotherroom.common.config.GameConfig

object DesktopLauncher {

    @JvmStatic
    fun main(args: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = GameConfig.WIDTH
        config.height = GameConfig.HEIGHT
        LwjglApplication(AnotherRoomGame(), config)
    }
}
