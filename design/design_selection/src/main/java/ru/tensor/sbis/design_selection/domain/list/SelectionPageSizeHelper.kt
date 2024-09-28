package ru.tensor.sbis.design_selection.domain.list

import ru.tensor.sbis.crud3.defaultPageSize
import ru.tensor.sbis.crud3.defaultViewPostSize

/**
 * Вспомогательная реализация для определения размеров страниц для загрузки списка.
 *
 * @param itemsLimit лимит на количество элементов отображаемых в списке. null - без лимита.
 *
 * @author vv.chekurda
 */
internal class SelectionPageSizeHelper(itemsLimit: Int?) {

    /**
     * Размер страницы для запроса данных.
     */
    val pageSize: Int

    /**
     * Размер видимой части страницы.
     */
    val viewPostSize: Int

    init {
        val (pageSize, viewPostSize) = itemsLimit?.let { limit ->
            limit to limit
        } ?: (defaultPageSize to defaultViewPostSize)
        this.pageSize = pageSize
        this.viewPostSize = viewPostSize
    }
}