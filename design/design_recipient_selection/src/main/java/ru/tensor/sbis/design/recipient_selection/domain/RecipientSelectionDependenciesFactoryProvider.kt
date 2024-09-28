package ru.tensor.sbis.design.recipient_selection.domain

import android.content.Context
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design.recipient_selection.domain.factory.getRecipientSelectionComponent
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory

/**
 * Поставщик фабрики зависимостей компонента выбора получателей.
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionDependenciesProvider
    : SelectionDependenciesFactory.Provider<RecipientItem, RecipientSelectionConfig> {

    override fun getFactory(
        appContext: Context,
        config: RecipientSelectionConfig,
    ): SelectionDependenciesFactory<RecipientItem> {
        val component = appContext.getRecipientSelectionComponent(config)
        return RecipientSelectionDependenciesFactory(component)
    }
}