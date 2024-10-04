package ru.tensor.sbis.design.recipient_selection.ui.menu

import ru.tensor.sbis.communication_decl.selection.SelectionMenu
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuConfig
import ru.tensor.sbis.design.recipient_selection.R
import ru.tensor.sbis.design.recipient_selection.domain.RecipientSelectionDependenciesProvider
import ru.tensor.sbis.design_selection.SelectionMenuFragmentFactory

/**
 * Фабрика для создания меню выбора получателей.
 *
 * @author vv.chekurda
 */
internal object RecipientSelectionMenuFactory {

    /**
     * Создать меню выбора получателей.
     *
     * @param config конфигурация меню выбора получателей.
     */
    fun createRecipientSelectionMenuFragment(
        config: RecipientSelectionMenuConfig
    ): SelectionMenu =
        SelectionMenuFragmentFactory.createSelectionMenuFragment(
            config = config,
            dependenciesProvider = RecipientSelectionDependenciesProvider(),
            themeAttr = R.attr.recipientSelectionMenuTheme
        )
}