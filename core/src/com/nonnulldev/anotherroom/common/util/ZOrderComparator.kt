package com.nonnulldev.anotherroom.common.util

import com.badlogic.ashley.core.Entity

class ZOrderComparator private constructor(): Comparator<Entity> {

    companion object {
        val instance: ZOrderComparator = ZOrderComparator()
    }

    override fun compare(o1: Entity?, o2: Entity?): Int {
        return Mappers.Z_ORDER.get(o1).z.compareTo(Mappers.Z_ORDER.get(o2).z)
    }
}
