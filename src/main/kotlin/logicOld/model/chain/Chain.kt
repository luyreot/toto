package logicOld.model.chain

import logicOld.data.Chains

/**
 * Class that holds a chain between the tracked [entity]
 * and every possible subsequent occurrence of a new entity from the same "type".
 * See [Chains].
 */
data class Chain(val entity: String) : Comparable<Chain> {

    /**
     * A map which represents an entity, must be of the same "type" like [entity],
     * and an index of how many times it occurred after the one in [entity].
     */
    val entityMap = mutableMapOf<String, Int>()

    init {
        if (entity.isEmpty()) {
            throw IllegalArgumentException("Entity cannot be empty!")
        }
    }

    /**
     *
     */
    fun updateEntityMap(key: String) {
        if (entityMap.containsKey(key)) {
            entityMap[key]?.inc()
            return
        }

        entityMap[key] = 1
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