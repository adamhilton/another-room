package com.nonnulldev.anotherroom.desktop

import com.badlogic.gdx.tools.texturepacker.TexturePacker

private val RAW_ASSETS_PATH = "desktop/assets-raw"
private val ASSETS_PATH = "core/assets"

fun main(args: Array<String>) {
    val settings = TexturePacker.Settings()
    settings.duplicatePadding = true

    TexturePacker.process(settings,
            RAW_ASSETS_PATH + "/game",
            ASSETS_PATH + "/game",
            "game")
}