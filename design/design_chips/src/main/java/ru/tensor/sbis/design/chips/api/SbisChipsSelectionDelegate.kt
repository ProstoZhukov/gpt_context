package ru.tensor.sbis.design.chips.api

/**
 * Класс для отслеживания изменения выбранных элементов.
 *
 * @author ps.smirnyh
 */
interface SbisChipsSelectionDelegate {

    /** Изменение списка выбранных элементов [selectedItems]. */
    fun onChange(selectedItems: List<Int>)

    /** Выбора элемента с [id]. */
    fun onSelect(id: Int)

    /** Снятие выбора с элемента с [id]. */
    fun onDeselect(id: Int)
}