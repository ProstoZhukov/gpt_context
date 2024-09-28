package ru.tensor.sbis.communication_decl.selection.universal.manager

import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultDelegate
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalSelectionData
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Делегат для передачи результата компонента универсального выбора.
 *
 * @author vv.chekurda
 */
interface UniversalSelectionResultDelegate : SelectionResultDelegate<UniversalSelectionData> {

    interface Provider : Feature {
        fun getUniversalSelectionResultDelegate(): UniversalSelectionResultDelegate
    }
}