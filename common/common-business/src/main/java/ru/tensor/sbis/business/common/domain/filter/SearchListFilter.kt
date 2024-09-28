package ru.tensor.sbis.business.common.domain.filter

import io.reactivex.Observable

/**
 * Списочный фильтр с поиском.
 * Универсальный интерфейс фильтра для использования с фасадами имеющими списочные методы с Поиском
 *
 * @property CPP_FILTER тип фильтра контроллера
 * @property CPP_CURSOR тип фильтра курсора в [CPP_FILTER]
 */
interface SearchListFilter<CPP_FILTER, CPP_CURSOR> :
    ListFilter<CPP_FILTER, CPP_CURSOR> {

    /** Поисковый запрос */
    var searchQuery: String

    /** Индикатор поиска, т.е. true если помимо фильтрации используется и поиск по данным */
    val asSearchFilter: Boolean
        get() = searchQuery.isNotBlank()

    /** Предоставляет [Observable] события изменения "читаемого" представления фильтра */
    fun observeReadableFiltersState(): Observable<List<String>>

    /** Уведомить о том что читаемое представление фильтра могло измениться */
    fun notifyReadableStateChanged()

    /** Преобразовать состояние фильтра к читаемому виду */
    fun transformToReadableFilterState(): List<String>
}
