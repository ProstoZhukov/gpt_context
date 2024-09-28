package ru.tensor.sbis.communication_decl.selection.result_manager

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Базовая реализация менеджера для работы с результатами компонентом выбора.
 *
 * @see SelectionResultManager
 * @see SelectionResultDelegate
 *
 * @author vv.chekurda
 */
abstract class AbstractSelectionResultManager<PRESELECTION_DATA, DATA, RESULT : SelectionResult<DATA>>
    : SelectionResultManager<PRESELECTION_DATA, DATA, RESULT>,
    SelectionResultDelegate<DATA> {

    private val selectionSubject by lazy {
        BehaviorSubject.createDefault(clearedResult)
    }

    override val selectionResult: RESULT
        get() = selectionSubject.value!!

    /**
     * Результат для чистого состояния.
     */
    abstract val clearedResult: RESULT

    override var preselectedData: PRESELECTION_DATA? = null

    /**
     * Создать модель результата.
     */
    abstract fun createResult(
        data: DATA?,
        status: SelectionResultStatus,
        requestKey: String = EMPTY_REQUEST_KEY
    ): RESULT

    override fun getSelectionResultObservable(vararg requestKeys: String): Observable<RESULT> =
        selectionSubject.skip(1)
            .filter { it.status != SelectionResultStatus.CLEARED }
            .filterByKeys(requestKeys)
            .share()

    override fun preselect(data: PRESELECTION_DATA?) {
        clear()
        preselectedData = data
    }

    override fun onSuccess(data: DATA?, requestKey: String) {
        val result = createResult(data, SelectionResultStatus.SUCCESS, requestKey)
        selectionSubject.onNext(result)
    }

    override fun onCancel(requestKey: String) {
        val result = createResult(selectionResult.data, SelectionResultStatus.CANCELED, requestKey)
        selectionSubject.onNext(result)
    }

    override fun clear() {
        selectionSubject.onNext(clearedResult)
        preselectedData = null
    }

    private fun Observable<RESULT>.filterByKeys(requestKeys: Array<out String>): Observable<RESULT> =
        if (requestKeys.isNotEmpty()) {
            filter { result -> requestKeys.contains(result.requestKey) }
        } else {
            this
        }
}