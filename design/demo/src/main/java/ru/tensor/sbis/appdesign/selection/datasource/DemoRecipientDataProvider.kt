package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoRecipientFilter
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientListModel
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientSelectorDataProvider
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel
import ru.tensor.sbis.list.base.domain.entity.paging.ITEMS_ON_PAGE

/**
 * @author ma.kolpakov
 */
class DemoRecipientDataProvider(
    private val controller: DemoRecipientController,
    private val mapper: DemoRecipientDataMapper
) : RecipientSelectorDataProvider<DefaultRecipientSelectorItemModel> {

    override fun fetchItems(
        selected: Set<DefaultRecipientSelectorItemModel>,
        items: List<DefaultRecipientSelectorItemModel>,
        searchText: String
    ): RecipientListModel<DefaultRecipientSelectorItemModel> {
        val filter = DemoRecipientFilter(searchText, items.size, ITEMS_ON_PAGE.toInt())
        return RecipientListModel(mapper.mapServiceData(controller.list(filter)), false)
    }
}