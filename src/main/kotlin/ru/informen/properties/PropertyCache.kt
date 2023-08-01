package ru.informen.properties

object PropertyCache {

    private val cache = mutableMapOf<Long, Property>()
    private val cacheTime = mutableMapOf<Long, Long>()
    private const val CACHE_DURATION = 24 * 60 * 60 * 1000

    fun add(id: Long, property: Property): Property {
        cache[id] = property
        cacheTime[id] = System.currentTimeMillis() + CACHE_DURATION
        return property
    }

    fun has(id: Long): Boolean {
        check(id)
        return cache.contains(id)
    }

    fun get(id: Long) = cache.getValue(id)

    private fun check(id: Long) {
        if (cacheTime.contains(id)) {
            if (System.currentTimeMillis() > cacheTime.getValue(id)) {
                cacheTime.remove(id)
                cache.remove(id)
            }
        }
    }
}