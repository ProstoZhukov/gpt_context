package ru.tensor.sbis.design.selection.ui.list.recipients

import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientListModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel

/**
 * Реализация [RecipientsMapperFunction] для извлечения списка получателей из модели [RecipientListModel]
 *
 * @author ma.kolpakov
 */
internal class RecipientsMapperFunction<DATA : RecipientSelectorItemModel> :
    ListMapper<RecipientListModel<DATA>, DATA> {

    override fun invoke(model: RecipientListModel<DATA>): List<DATA> =
        model.items
}
