package ru.tensor.sbis.list.base.domain.entity.paging.filter

import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.entity.paging.CreateFilter
import ru.tensor.sbis.list.base.domain.entity.paging.PagingData

/**
 * Поставщик фильтра [FILTER] для микросервиса контроллера основе результата [SERVICE_RESULT] предыдущих полученных данных от него
 * c якорным объектом [ANCHOR] для построения запросов последующих страниц.
 */
internal class SubsequentPageFilterProvider<ANCHOR, SERVICE_RESULT, FILTER>(
    private val anchor: ANCHOR?,
    pagingData: PagingData<SERVICE_RESULT>,
    private val createFilter: CreateFilter<ANCHOR, FILTER>,
    private val itemsOnPage: Long,
    private val pageNumber: Int,
    private val includeAnchor: Boolean = pagingData.isEmpty()
) : FilterAndPageProvider<FILTER> {

    override fun getServiceFilter(): FILTER {
        return createFilter(
            anchor,
            includeAnchor,
            itemsOnPage,
            pageNumber
        )
    }

    override fun getPageNumber() = pageNumber
}