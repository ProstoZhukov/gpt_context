package ru.tensor.sbis.communication_decl.selection.sources.edo

import androidx.annotation.WorkerThread
import ru.tensor.sbis.communication_decl.selection.sources.SelectionExternalSource
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Источник лиц для импорта результата компонента выбора.
 *
 * @author vv.chekurda
 */
@WorkerThread
abstract class SelectionFacesSource : SelectionExternalSource() {

    /**
     * Поставщик источника лиц.
     */
    interface Provider : Feature {

        /**
         * Получить источник лиц.
         */
        fun getSelectionFacesSource(): SelectionFacesSource
    }
}