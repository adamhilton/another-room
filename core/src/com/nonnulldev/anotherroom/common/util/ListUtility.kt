package com.nonnulldev.anotherroom.common.util

import java.util.*

object ListUtility {
    fun <T:Comparable<T>>shuffle(items:MutableList<T>):List<T>{
        val random : Random = Random()
        for (i in 0..items.size - 1) {
            val randomPosition = random.nextInt(items.size)
            val tmp : T = items[i]
            items[i] = items[randomPosition]
            items[randomPosition] = tmp
        }
        return items
    }
}

