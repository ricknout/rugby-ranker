package dev.ricknout.rugbyranker.core.util

import java.util.concurrent.atomic.AtomicInteger

object IdUtils {

    private val atomicInteger = AtomicInteger(1)

    fun getID(): Int {
        return atomicInteger.incrementAndGet()
    }
}
