package ru.tensor.sbis.communication_decl.selection.recipient.menu

import ru.tensor.sbis.communication_decl.selection.SelectionMenu
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик компонента меню выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionMenuProvider : RecipientSelectionResultManager.Provider, Feature {

    /**
     * Создать меню выбора получателей.
     *
     * @param config конфигурация выбора получателей.
     */
    fun getRecipientSelectionMenu(
        config: RecipientSelectionMenuConfig
    ): SelectionMenu
}