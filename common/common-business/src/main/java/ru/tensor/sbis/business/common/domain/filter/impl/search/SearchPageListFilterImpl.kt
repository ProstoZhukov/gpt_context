package ru.tensor.sbis.business.common.domain.filter.impl.search

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.SearchListFilter
import ru.tensor.sbis.business.common.domain.filter.impl.PageListFilterImpl
import ru.tensor.sbis.design.view.input.searchinput.DEFAULT_SEARCH_QUERY

/**
 * Фильтр для получения списочных данных с "ПОСТРАНИЧНОЙ" навигацией (пагинацией) и Поиском
 *
 * @property CPP_FILTER тип фильтра контроллера
 * @property readableStateChannel канал уведомления о изменении читаемого представления фильтра
 */
abstract class SearchPageListFilterImpl<CPP_FILTER : Any>(
    hashProvider: HashFilterProvider
) :
    PageListFilterImpl<CPP_FILTER>(hashProvider),
    SearchListFilter<CPP_FILTER, Nothing> {

    override var searchQuery: String = DEFAULT_SEARCH_QUERY
        get() = field.trim()

    override val asSearchFilter: Boolean
        get() = searchQuery.isNotBlank()

    override fun observeReadableFiltersState(): Observable<List<String>> =
        readableStateChannel.map { transformToReadableFilterState() }

    override fun notifyReadableStateChanged() = readableStateChannel.onNext(Unit)

    override fun transformToReadableFilterState(): List<String> = emptyList()

    private val readableStateChannel by lazy { PublishSubject.create<Unit>() }
}

