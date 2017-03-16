package com.nonnulldev.anotherroom.extension

import com.nonnulldev.anotherroom.config.GameConfig
import com.nonnulldev.anotherroom.data.Coordinates

fun Coordinates.isWithinWorldBounds(): Boolean {
    return (this.x > 0 && this.x <= GameConfig.WORLD_WIDTH - 2f) &&
            (this.y > 0 && this.y <= GameConfig.WORLD_HEIGHT -2f)
}