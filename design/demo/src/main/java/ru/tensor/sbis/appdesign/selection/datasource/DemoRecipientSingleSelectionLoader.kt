package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.design.selection.ui.contract.SingleSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoRecipientSingleSelectionLoader(
    private val controller: DemoRecipientController,
    private val mapper: DemoRecipientDataMapper
) : SingleSelectionLoader<DefaultRecipientSelectorItemModel> {

    override fun loadSelectedItem(): DefaultRecipientSelectorItemModel? =
        controller.loadSelectedItems().run(mapper::mapServiceData).firstOrNull()
}