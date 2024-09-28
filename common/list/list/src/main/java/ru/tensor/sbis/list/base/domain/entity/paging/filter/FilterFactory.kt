package ru.tensor.sbis.list.base.domain.entity.paging.filter

import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.entity.paging.CreateFilter
import ru.tensor.sbis.list.base.domain.entity.paging.PagingData

/**
 * Фабрика для создания фильтра [FILTER] для микросервиса контроллера.
 * Ранее подгруженные постраничные данные [pagingData] используются для получения определения номера страницы и якорного объекта,
 * [helper] получает из результата, возвращаемого микрсервисом [SERVICE_RESULT], которые будут использованы
 * как якорные элементы для получения данных следующей страницы.
 */
internal class FilterFactory<ANCHOR, SERVICE_RESULT, FILTER>(
    private val pagingData: PagingData<SERVICE_RESULT>,
    private val helper: ResultHelper<ANCHOR, SERVICE_RESULT>,
    private val itemsOnPage: Long = 20
) {
    /**
     * Получить фильтр для запроса следующей страницы используя [createFilter].
     */
    fun filterForNext(createFilter: CreateFilter<ANCHOR, FILTER>): FilterAndPageProvider<FILTER> {
        return SubsequentPageFilterProvider(
            getNextPageAnchor(),
            pagingData,
            createFilter,
            itemsOnPage,
            getNextPageNumber()
        )
    }

    /**
     * Получить фильтр для запроса предыдущей страницы используя [createFilter].
     */
    fun filterForPrevious(createFilter: CreateFilter<ANCHOR, FILTER>): FilterAndPageProvider<FILTER> {
        return SubsequentPageFilterProvider(
            getPreviousPageAnchor(),
            pagingData,
            createFilter,
            itemsOnPage,
            getPreviousPageNumber()
        )
    }

    private fun getNextPageAnchor(): ANCHOR? {
        val lastPage = pagingData.lastPageData()

        return if (lastPage == null) null else helper.getAnchorForNextPage(lastPage)
    }

    private fun getPreviousPageAnchor(): ANCHOR? {
        val firstPage = getFirstPage()
        return if (firstPage == null) null else helper.getAnchorForPreviousPage(firstPage)
    }

    private fun getNextPageNumber() =
        if (pagingData.isEmpty()) 0 else pagingData.lastKeyOrZeroIfEmpty() + 1

    private fun getPreviousPageNumber() = pagingData.firstKeyOrZeroIfEmpty() - 1

    private fun getFirstPage(): SERVICE_RESULT? = pagingData.firstPageData()
}