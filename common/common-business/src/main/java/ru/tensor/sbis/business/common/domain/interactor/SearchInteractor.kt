package ru.tensor.sbis.business.common.domain.interactor

import io.reactivex.Observable

/**
 * Интерфейс использования списочного Интерактора для CRUD фасада контроллера
 * с предоставлением отдельного метода для поиска по онлайну
 */
interface SearchInteractor<DATA : Any, FILTER : Any> :
    RequestInteractor<DATA, FILTER> {

    /**
     * Выполнить поиска на онлайн. Не затрагивает локальный кэш
     *
     * @param searchQuery поисковой запрос
     *
     * @return Предоставляет [Observable] получения данных или ошибки
     */
    fun searchData(searchQuery: String): Observable<Result<DATA>>
}