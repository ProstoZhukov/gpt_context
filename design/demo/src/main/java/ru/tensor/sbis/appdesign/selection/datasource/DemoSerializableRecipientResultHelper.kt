package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientListModel
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientsResultHelper
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoSerializableRecipientResultHelper : RecipientsResultHelper<Int, DefaultRecipientSelectorItemModel> {

    override fun hasNext(result: RecipientListModel<DefaultRecipientSelectorItemModel>): Boolean =
        result.hasMore

    override fun isEmpty(result: RecipientListModel<DefaultRecipientSelectorItemModel>): Boolean =
        result.items.isEmpty()

    override fun getAnchorForNextPage(result: RecipientListModel<DefaultRecipientSelectorItemModel>): Int? =
        null

    override fun getAnchorForPreviousPage(result: RecipientListModel<DefaultRecipientSelectorItemModel>): Int? =
        null
}