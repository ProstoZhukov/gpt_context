package ru.tensor.sbis.design.selection.ui.list.filter

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.PageDirection
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.domain.entity.paging.CreateFilter

/**
 * Определяет создание фильтров для постраничной загрузки, учитывая поисковый запрос и текущие выбранные элементы
 *
 * @author us.bessonov
 */
internal class SelectorFilterCreator<FILTER, ANCHOR>(
    private val filterFactory: FilterFactory<SelectorItemModel, FILTER, ANCHOR>,
    private val metaFactory: SelectorListFilterMetaFactory<ANCHOR>,
    private val parentId: SelectorItemId?
) {

    var searchQuery: String = ""

    var selection: List<SelectorItemModel>
        get() = metaFactory.selection
        set(value) { metaFactory.selection = value }

    var availableItems: List<SelectorItemModel>
        get() = metaFactory.items
        set(value) { metaFactory.items = value }

    /**
     * Функция, формирующая фильтр для получения следующей страницы
     */
    val createForNextPage: CreateFilter<ANCHOR, FILTER> = { anchor, _, itemsOnPage: Long, pageNumber: Int ->
        createFilter(anchor, itemsOnPage, pageNumber, PageDirection.NEXT)
    }

    /**
     * Функция, формирующая фильтр для получения предыдущей страницы
     */
    val createForPreviousPage: CreateFilter<ANCHOR, FILTER> = { anchor, _, itemsOnPage: Long, pageNumber: Int ->
        createFilter(anchor, itemsOnPage, pageNumber, PageDirection.PREVIOUS)
    }

    private fun createFilter(
        anchor: ANCHOR?,
        itemsOnPage: Long,
        pageNumber: Int,
        pageDirection: PageDirection
    ): FILTER {
        val filterMeta =
            metaFactory.createFilterMeta(searchQuery, anchor, parentId, itemsOnPage.toInt(), pageNumber, pageDirection)
        return filterFactory.createFilter(filterMeta)
    }
}