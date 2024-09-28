package ru.tensor.sbis.communicator.dialog_selection.data.factory.filter

import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionSearchFilter
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.model.FilterMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import javax.inject.Inject

/**
 * Реализация фабрики комбинированного фильтра для контроллеров получателей и диалогов
 * @property recipientsFilterFactory фабрика фильтра контроллера получателей
 * @property dialogsFilterFactory    фабрика фильтра контроллера диалогов
 *
 * @author vv.chekurda
 */
internal class DialogSelectionFilterFactory @Inject constructor(
    private val recipientsFilterFactory: RecipientsFilterFactory,
    private val dialogsFilterFactory: DialogsFilterFactory
) : FilterFactory<SelectorItemModel, DialogSelectionSearchFilter, Int> {

    override fun createFilter(meta: FilterMeta<SelectorItemModel, Int>): DialogSelectionSearchFilter =
        DialogSelectionSearchFilter(
            recipientsFilterFactory.createFilter(meta),
            dialogsFilterFactory.createFilter(meta)
        )
}