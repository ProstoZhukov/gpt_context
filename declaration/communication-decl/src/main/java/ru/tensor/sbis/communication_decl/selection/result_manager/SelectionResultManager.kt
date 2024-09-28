package ru.tensor.sbis.communication_decl.selection.result_manager

import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.selection.SelectionConfig

/**
 * Менеджер для работы с результатами компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionResultManager<PRESELECTION_DATA, DATA, RESULT : SelectionResult<DATA>> {

    /**
     * Получить текущий результат компонента выбора.
     */
    val selectionResult: RESULT

    /**
     * Предустановленные данные для отображения выбранных элементов.
     */
    val preselectedData: PRESELECTION_DATA?

    /**
     * Получить [Observable] для подписки на результат выбора, где [requestKeys] являются набором интересующий ключей,
     * указанных в конфигурации [SelectionConfig.requestKey], с которыми запускается компонент выбора.
     * Для пустого списка ключей будут приходить все результаты.
     */
    fun getSelectionResultObservable(vararg requestKeys: String): Observable<RESULT>

    /**
     * Установить данные, которые будут отображены при открытии компонента выбора.
     */
    fun preselect(data: PRESELECTION_DATA?)

    /**
     * Очистить текущее состояние менеджера для сброса выбранных.
     */
    fun clear()
}