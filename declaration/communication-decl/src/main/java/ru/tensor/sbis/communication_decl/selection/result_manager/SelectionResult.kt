package ru.tensor.sbis.communication_decl.selection.result_manager

/**
 * Результат компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionResult<out DATA> {

    /**
     * Данные результата.
     */
    val data: DATA

    /**
     * Статус результата.
     */
    val status: SelectionResultStatus

    /**
     * Ключ запроса, который указывается при открытии компонента выбора.
     */
    val requestKey: String

    /**
     * Признак успешного выбора.
     */
    val isSuccess: Boolean
        get() = status == SelectionResultStatus.SUCCESS

    /**
     * Признак отмены выбора.
     */
    val isCanceled: Boolean
        get() = status == SelectionResultStatus.CANCELED

    /**
     * Признак очищенного результата - пустое сотояние.
     */
    val isCleared: Boolean
        get() = status == SelectionResultStatus.CLEARED
}

/**
 * Статус результата компонента выбора.
 */
enum class SelectionResultStatus {
    /**
     * Выбор был закрыт.
     */
    CANCELED,

    /**
     * Выбор успешно завершился.
     */
    SUCCESS,

    /**
     * Чистый результат, выбор еще не завершился.
     */
    CLEARED
}