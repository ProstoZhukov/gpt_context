package ru.tensor.sbis.design_selection_common

import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection_common.controller.PreselectedData

/**
 * Поставщик предвыбранных данных компонента выбора.
 *
 * @author vv.chekurda
 */
interface PreselectedDataProvider {

    /**
     * Получить предвыбранные данные для настройки [config].
     */
    fun getPreselectedData(config: SelectionConfig): PreselectedData
}