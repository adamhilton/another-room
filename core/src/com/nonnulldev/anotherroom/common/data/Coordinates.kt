package com.nonnulldev.anotherroom.common.data

data class Coordinates(var x: Int, var y: Int) {
    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}

fun Coordinates.north(): Coordinates {
    return Coordinates(x, y + 1)
}

fun Coordinates.northEast(): Coordinates {
    return Coordinates(x + 1, y + 1)
}

fun Coordinates.south(): Coordinates {
    return Coordinates(x, y - 1)
}

fun Coordinates.southEast(): Coordinates {
    return Coordinates(x + 1, y - 1)
}

fun Coordinates.east(): Coordinates {
    return Coordinates(x + 1, y)
}

fun Coordinates.northWest(): Coordinates {
    return Coordinates(x - 1, y + 1)
}

fun Coordinates.west(): Coordinates {
    return Coordinates(x - 1, y)
}

fun Coordinates.southWest(): Coordinates {
    return Coordinates(x - 1, y - 1)
}

