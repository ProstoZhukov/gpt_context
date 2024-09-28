package ru.tensor.sbis.base_components.adapter.universal

/**
 * Класс для фиксации позиций вставок в список
 *
 * @author sa.nikitin
 */
@Suppress("MemberVisibilityCanBePrivate")
class InsertionResult(private val insertionPositions: MutableList<Int>) {

    init {
        insertionPositions.sort()
    }

    /**
     * Метод определяет, был ли вставлен только один элемент
     */
    fun isSingleInsert() = insertionPositions.size == 1

    /**
     * Метод определяет, были ли позиции вставки последовательными (например: 18, 19, 20, но не: 18, 19, 21)
     */
    fun isSequentialInsertion(): Boolean {
        if (insertionPositions.isEmpty()) {
            return false
        }
        insertionPositions.forEachIndexed { index, insertionPosition ->
            //Начиная с первой позиции сравниваем с предыдущей
            //Если разница больше 1, значит вставки не были последовательными, возвращаем false
            if (index > 0 && insertionPosition - insertionPositions[index - 1] != 1) {
                return false
            }
        }
        return true
    }

    /**
     * Метод определяет, был ли вставлен только один элемент либо были ли позиции вставки последовательными
     */
    fun isSingleOrSequentialInsertion() = isSingleInsert() || isSequentialInsertion()

    /**
     * Метод возвращает минимальную позицию вставки либо -1, если позиции вставки отсутствуют
     */
    fun minInsertionPosition() = if (insertionPositions.size > 0) insertionPositions[0] else -1
}