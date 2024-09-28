package ru.tensor.sbis.design.recipient_selection.ui

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.design.recipient_selection.domain.RecipientSelectionDependenciesProvider
import ru.tensor.sbis.design.recipient_selection.R
import ru.tensor.sbis.design_selection.SelectionFragmentFactory

/**
 * Фабрика для создания фрагмента выбора получателей.
 *
 * @author vv.chekurda
 */
internal object RecipientSelectionFragmentFactory {

    /**
     * Создать фрагмент выбора получателей.
     *
     * @param config конфигурация выбора получателей.
     */
    fun createRecipientSelectionFragment(
        config: RecipientSelectionConfig
    ): Fragment =
        SelectionFragmentFactory.createSelectionFragment(
            config = config,
            dependenciesProvider = RecipientSelectionDependenciesProvider(),
            themeAttr = R.attr.recipientSelectionTheme
        )
}

