package com.nonnulldev.anotherroom

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import com.nonnulldev.anotherroom.screen.dungeon.DungeonScreen

class AnotherRoomGame : Game() {

    internal lateinit var batch: SpriteBatch
    internal lateinit var assetManager: AssetManager

    override fun create() {
        Gdx.app.logLevel = Logger.DEBUG

        batch = SpriteBatch()
        assetManager = AssetManager()
        assetManager.logger.level = Logger.DEBUG

        setScreen(DungeonScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        assetManager.dispose()
    }
}
