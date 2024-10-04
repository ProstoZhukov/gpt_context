/**
 * Инструмент для определения индекса начального элемента очередной страницы на основе FilterMeta
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.PageDirection

/**
 * Позволяет получить индекс начального элемента для использования в фильтре при загрузке очередной страницы.
 * Актуально для случаев, когда невозможно получение этого значения на основе лишь прошлого результата загрузки
 */
fun getFilterPageStartIndexForMeta(filterMeta: FilterMeta<*, Int>): Int = with(filterMeta) {
    return if (pageDirection == PageDirection.NEXT) {
        filterMeta.itemsOnPage * (pageIndex - 1).coerceAtLeast(0) + (anchor ?: 0)
    } else {
        filterMeta.itemsOnPage * pageIndex
    }
}