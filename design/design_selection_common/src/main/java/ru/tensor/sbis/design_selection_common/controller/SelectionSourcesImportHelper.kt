package ru.tensor.sbis.design_selection_common.controller

import androidx.annotation.WorkerThread
import ru.tensor.sbis.communication_decl.selection.SelectionUseCase
import ru.tensor.sbis.communication_decl.selection.sources.SelectionExternalSource
import ru.tensor.sbis.recipients.generated.RecipientViewModel

/**
 * Вспомогательная реализация для импорта результата выбора во внешние источники.
 *
 * @property useCase use-case компонента выбора.
 * @property externalSources список внешних источников, которым необходим импорт результата выбора.
 *
 * @author vv.chekurda
 */
class SelectionSourcesImportHelper(
    private val useCase: SelectionUseCase,
    private val externalSources: List<SelectionExternalSource>
) {

    /**
     * Импортировать выбранных получателей во внешние источники.
     */
    @WorkerThread
    fun importSelectedRecipients(selected: List<RecipientViewModel>) {
        externalSources.forEach { source ->
            source.importSelectionResult(useCase, selected)
        }
    }
}