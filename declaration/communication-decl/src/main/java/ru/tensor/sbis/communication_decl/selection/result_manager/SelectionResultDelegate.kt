package ru.tensor.sbis.communication_decl.selection.result_manager

/**
 * Делегат для передачи результата компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionResultDelegate<in DATA> {

    /**
     * Обработать успешное подтверждение выбранных данных.
     *
     * @param data выбранные данные.
     * @param requestKey ключ запроса, используемый при открытии компонента выбора.
     */
    fun onSuccess(
        data: DATA? = null,
        requestKey: String = EMPTY_REQUEST_KEY
    )

    /**
     * Обработать закрытие выбора.
     *
     * @param requestKey ключ запроса, используемый при открытии компонента выбора.
     */
    fun onCancel(requestKey: String = EMPTY_REQUEST_KEY)
}

const val EMPTY_REQUEST_KEY = "EMPTY_REQUEST_KEY"