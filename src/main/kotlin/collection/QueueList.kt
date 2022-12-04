package collection

import java.util.function.Predicate

/**
 * Custom ArrayList implementation which can hold only [maxSize] number of elements.
 *
 * The implementation ensures that new elements are added at the end of the list.
 * If the size exceeds the [maxSize] then elements are removed from the start of the list.
 *
 * Only the following two methods can be used for adding elements:
 * - [add]
 * - [addAll]
 * Every other method will throw an [IllegalAccessException].
 *
 * Only the following method can be used for removing elements:
 * - [remove]
 * Every other method will throw an [IllegalAccessException].
 */
class QueueList<T>(
    private val maxSize: Int
) : ArrayList<T>(maxSize) {

    fun isAtMaxSize(): Boolean = size == maxSize

    fun last(): T? = if (isEmpty()) null else get(size - 1)

    fun remove(): T = super.removeAt(0)

    override fun add(element: T): Boolean {
        if (isAtMaxSize()) {
            remove()
        }

        if (size >= maxSize) {
            throw IllegalArgumentException("Current size ($size) is equal to or greater than maxSize ($maxSize)!")
        }

        return super.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val didAddAll = super.addAll(elements)

        if (didAddAll.not()) {
            if (size > maxSize) {
                throw IllegalArgumentException("Current size ($size) greater than maxSize ($maxSize)!")
            }

            return false
        }

        while (size > maxSize) {
            super.removeAt(0)
        }

        if (size > maxSize) {
            throw IllegalArgumentException("Current size ($size) greater than maxSize ($maxSize)!")
        }

        return didAddAll
    }

    override fun add(index: Int, element: T) {
        throw IllegalAccessException("Cannot use add(index: Int, element: T) method. Use add(element: T) instead!")
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        throw IllegalAccessException("Cannot use addAll(index: Int, elements: Collection<T>) method. Use addAll(elements: Collection<T> instead!")
    }

    override fun remove(element: T): Boolean {
        throw IllegalAccessException("Cannot use remove(element: T) method. Use remove() instead!")
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        throw IllegalAccessException("Cannot use removeAll(elements: Collection<T>) method. Use remove() or clear() instead!")
    }

    override fun removeAt(index: Int): T {
        throw IllegalAccessException("Cannot use removeAt(index: Int) method. Use remove() instead!")
    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        throw IllegalAccessException("Cannot use removeIf(filter: Predicate<in T>) method. Use remove() instead!")
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        throw IllegalAccessException("Cannot use removeRange(fromIndex: Int, toIndex: Int) method. Use remove() instead!")
    }
}