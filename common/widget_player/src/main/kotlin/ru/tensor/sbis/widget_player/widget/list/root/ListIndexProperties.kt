package ru.tensor.sbis.widget_player.widget.list.root

/**
 * @author am.boldinov
 */
internal data class ListIndexProperties(
    val level: Int = 0,
    val startIndex: Int = 0,
    val checkedIndexes: Array<Boolean> = emptyArray(),
    val checkedCount : Int = 0
) {

    val uncheckedCount get() = checkedIndexes.size - checkedCount

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ListIndexProperties

        if (level != other.level) return false
        if (startIndex != other.startIndex) return false
        if (!checkedIndexes.contentEquals(other.checkedIndexes)) return false
        if (checkedCount != other.checkedCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level
        result = 31 * result + startIndex
        result = 31 * result + checkedIndexes.contentHashCode()
        result = 31 * result + checkedCount
        return result
    }
}