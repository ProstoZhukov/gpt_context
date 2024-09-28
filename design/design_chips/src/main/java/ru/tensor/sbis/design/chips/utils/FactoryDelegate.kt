/**
 * Фабричные методы для создания делегата изменения выбранных элементов.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.chips.utils

import ru.tensor.sbis.design.chips.api.SbisChipsSelectionDelegate

/** Создать делегат для отслеживания изменения списка выбранных элементов. */
inline fun sbisChipsChangeDelegate(crossinline onChange: (selectedItems: List<Int>) -> Unit) =
    object : SbisChipsSelectionDelegate {

        override fun onChange(selectedItems: List<Int>) = onChange(selectedItems)

        override fun onSelect(id: Int) = Unit

        override fun onDeselect(id: Int) = Unit
    }

/** Создать делегат для отслеживания изменения отдельных элементов. */
inline fun sbisChipsSingleChangeDelegate(
    crossinline onSelect: (id: Int) -> Unit,
    crossinline onDeselect: (id: Int) -> Unit
) = object : SbisChipsSelectionDelegate {

    override fun onChange(selectedItems: List<Int>) = Unit

    override fun onSelect(id: Int) = onSelect(id)

    override fun onDeselect(id: Int) = onDeselect(id)
}