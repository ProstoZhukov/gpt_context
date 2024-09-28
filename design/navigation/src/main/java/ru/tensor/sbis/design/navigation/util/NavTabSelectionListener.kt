package ru.tensor.sbis.design.navigation.util

/**
 * Слушатель смены режима выделения вкладки в ННП.
 */
fun interface NavTabSelectionListener {
    /** @SelfDocumented */
    fun changeSelection(isSelected: Boolean)
}