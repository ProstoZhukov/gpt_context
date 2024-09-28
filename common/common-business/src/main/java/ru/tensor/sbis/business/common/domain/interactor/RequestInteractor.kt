package ru.tensor.sbis.business.common.domain.interactor

import androidx.annotation.AnyThread
import io.reactivex.Observable
import ru.tensor.sbis.business.common.ui.viewmodel.UpdateCause

/*
 * Интерфейс интерактора получения простых/списочных данных через CRUD фасад контроллера
 *
 * [FILTER] тип фильтра
 * [DATA] тип получаемых данных
 */
interface RequestInteractor<DATA : Any, FILTER : Any> {

    /**
     * Выполнить команду получения данных по стандартному сценарию
     * Т.е. отдает данные из КЭША (если они доступны локально) с/без запуском синхронизации с ОБЛАКОМ
     *
     * @param cause причина получения/обновления данных
     *
     * @return Предоставляет [Observable] получения данных или ошибки
     */
    @AnyThread
    fun requestData(cause: UpdateCause): Observable<Result<DATA>>
}