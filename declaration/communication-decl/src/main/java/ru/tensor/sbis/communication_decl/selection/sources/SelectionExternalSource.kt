package ru.tensor.sbis.communication_decl.selection.sources

import ru.tensor.sbis.communication_decl.selection.SelectionUseCase

/**
 * Внешний источник для импорта результата компонента выбора.
 *
 * Типы моделей намеренно скрыты для предотвращения двустороннего маппинга потенциально объемных списков.
 * В [importSelectionResult] передаются модельки контроллера,
 * которые в том же виде будут уходить на соответствующий источник.
 *
 * @author vv.chekurda
 */
abstract class SelectionExternalSource {

    /**
     * Проверка необходимости импорта результа по текущему use-case.
     */
    protected abstract val SelectionUseCase.requireImport: Boolean

    /**
     * Импорт результата компонента выбора в текущий источник.
     */
    protected abstract fun importToSource(result: List<*>)

    /**
     * Импортировать результат выбора [result] в источник для сценария [useCase].
     */
    fun importSelectionResult(useCase: SelectionUseCase, result: List<*>) {
        if (useCase.requireImport) importToSource(result)
    }
}