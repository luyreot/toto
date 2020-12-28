package model.chain

/**
 *
 */
data class Chain(val entity: String) : Comparable<Chain> {

    /**
     * A map which represents an entity, must be of the same "type" like [entity],
     * and an index of how many times it occurred after the one in [entity].
     */
    val chainMap = mutableMapOf<String, Int>()

    init {
        if (entity.isEmpty()) {
            throw IllegalArgumentException("Entity cannot be empty!")
        }
    }

    /**
     *
     */
    fun updateChainMap(key: String) {
        if (chainMap.containsKey(key)) {
            chainMap[key]?.inc()
            return
        }

        chainMap[key] = 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chain

        if (entity != other.entity) return false

        return true
    }

    override fun hashCode(): Int = entity.hashCode()

    override fun compareTo(other: Chain): Int = entity.compareTo(other.entity)

}