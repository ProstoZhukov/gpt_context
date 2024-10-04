package ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar

/**
 * Слушатель изменений числа строк заголовка при отображении
 *
 * @author us.bessonov
 */
interface OnExpandedTitleLineCountChangeListener {

    fun onExpandedTitleLineCountChanged(lineCount: Int)
}