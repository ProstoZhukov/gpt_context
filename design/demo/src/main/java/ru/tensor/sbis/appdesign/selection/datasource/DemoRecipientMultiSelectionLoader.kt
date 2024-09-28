package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoRecipientMultiSelectionLoader(
    private val controller: DemoRecipientController,
    private val mapper: DemoRecipientDataMapper
) : MultiSelectionLoader<DefaultRecipientSelectorItemModel> {

    override fun loadSelectedItems(): List<DefaultRecipientSelectorItemModel> =
        controller.loadSelectedItems().run(mapper::mapServiceData)
}