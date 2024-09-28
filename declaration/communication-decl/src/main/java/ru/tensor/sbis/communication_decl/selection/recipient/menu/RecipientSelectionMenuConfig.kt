package ru.tensor.sbis.communication_decl.selection.recipient.menu

import ru.tensor.sbis.communication_decl.selection.SelectionMenuConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig

/**
 * Конфигурация компонента меню выбора получателей в шторке.
 * @see SelectionMenuConfig
 *
 * @author vv.chekurda
 */
data class RecipientSelectionMenuConfig(
    override val selectionConfig: RecipientSelectionConfig,
    override val autoHideEmptyMenu: Boolean = true,
    override val ignoreWindowInsets: Boolean = true,
    override val closable: Boolean = false
) : SelectionMenuConfig<RecipientSelectionConfig>