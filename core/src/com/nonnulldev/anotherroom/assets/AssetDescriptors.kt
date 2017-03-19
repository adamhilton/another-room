package com.nonnulldev.anotherroom.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.g2d.TextureAtlas

object AssetDescriptors {
    val GAME = AssetDescriptor<TextureAtlas>(AssetPaths.GAME, TextureAtlas::class.java)
}