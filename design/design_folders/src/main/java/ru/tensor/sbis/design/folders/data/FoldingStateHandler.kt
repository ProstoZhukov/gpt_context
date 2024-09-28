package ru.tensor.sbis.design.folders.data

/**
 * Обработчик сворачивания\разворачивания комонента папок
 *
 * @author ma.kolpakov
 */
fun interface FoldingStateHandler {

    /**
     * Обработка сворачивания\разворачивания комонента папок
     *
     * @param isFolded true - произошло сворачивание, false - произошло разворачивание
     */
    fun onChanged(isFolded: Boolean)
}
