package ru.tensor.sbis.business.common.domain.filter.impl.search

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.SearchListFilter
import ru.tensor.sbis.business.common.domain.filter.impl.CursorListFilterImpl
import ru.tensor.sbis.design.view.input.searchinput.DEFAULT_SEARCH_QUERY

/**
 * Фильтр для получения списочных данных с "КУРСОРНОЙ" навигацией (пагинацией) и Поиском
 *
 * @property CPP_FILTER тип фильтра контроллера
 * @property CPP_CURSOR тип фильтра курсора в [CPP_FILTER]
 * @property readableStateChannel канал уведомления о изменении читаемого представления фильтра
 */
abstract class SearchCursorListFilterImpl<
        CPP_FILTER : Any,
        CPP_CURSOR : Any>(
    hashProvider: HashFilterProvider
) :
    CursorListFilterImpl<CPP_FILTER, CPP_CURSOR>(hashProvider),
    SearchListFilter<CPP_FILTER, CPP_CURSOR> {

    override var searchQuery: String = DEFAULT_SEARCH_QUERY
        get() = field.trim()

    override val asSearchFilter: Boolean
        get() = searchQuery.isNotBlank()

    /** Признак итеративной фильтрации */
    open val isIterative: Boolean
        get() = searchQuery.isNotBlank()

    /** Настройки итеративной фильтрации */
    var iterativeSettings: String? = null
        private set

    override fun observeReadableFiltersState(): Observable<List<String>> =
        readableStateChannel.map { transformToReadableFilterState() }

    override fun notifyReadableStateChanged() = readableStateChannel.onNext(Unit)

    override fun transformToReadableFilterState(): List<String> = emptyList()

    override fun reset() {
        super.reset()
        iterativeSettings = null
    }

    /**
     * Установить настройку итеративного поиска полученную с онлайн
     */
    fun setIterative(settings: String?) {
        iterativeSettings = settings
    }

    private val readableStateChannel by lazy { BehaviorSubject.create<Unit>() }
}