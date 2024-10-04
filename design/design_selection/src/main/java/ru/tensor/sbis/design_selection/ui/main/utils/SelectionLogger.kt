package ru.tensor.sbis.design_selection.ui.main.utils

import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import timber.log.Timber
import java.util.UUID

/**
 * Вспомогательная реализация для логирования и хранения истории сессии компонента выбора.
 *
 * @author vv.chekurda
 */
object SelectionLogger {

    private var history = StringBuilder()

    val sessionHistory: String
        get() = history.toString()

    fun onStartSession(config: SelectionConfig) {
        history.clear()
        log("Начало сессии выбора с настройками $config")
    }

    fun onCreateCollection(id: SelectionItemId?) {
        log("Создание коллекции в папке ${id ?: "корневой"}")
    }

    fun onSetSelected(ids: List<SelectionItemId>) {
        log("Обновлен список выбранных элементов $ids")
    }

    fun onSelect(id: SelectionItemId, append: Boolean) {
        log("Операция выбора элемента $id, клик по плюсу - $append")
    }

    fun onSelectComplete(id: SelectionItemId) {
        log("Операция подтверждения выбора по клику на элемент $id")
    }

    fun onUnselect(id: SelectionItemId) {
        log("Операция отмены выбора элемента $id")
    }

    fun onSearch(query: String) {
        log("Операция изменения поисковыого запроса $query")
    }

    fun onComplete(selected: List<SelectionItemId>) {
        log("Операция подтверждения выбора, список результата $selected")
    }

    fun onCancel() {
        log("Операция отмены выбора")
    }

    fun onEndSession() {
        log("Конец сессии выбора")
    }

    fun onSendSuccessResult(result: List<UUID>, requestKey: String) {
        log("Отправка результата выбора: $result для requestKey = $requestKey")
    }

    fun onSendCancelResult(requestKey: String) {
        log("Отмена результата выбора для requestKey = $requestKey")
    }

    private fun log(infoString: String) {
        val resultString = "SelectionLogger: $infoString"
        Timber.i(resultString)
        history.appendLine(resultString)
    }
}