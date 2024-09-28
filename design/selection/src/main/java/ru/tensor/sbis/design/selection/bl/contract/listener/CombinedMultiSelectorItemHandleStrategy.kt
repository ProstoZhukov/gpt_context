package ru.tensor.sbis.design.selection.bl.contract.listener

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.TitleItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DialogSelectorItemModel

/**
 * Стратегия для комбинированного мультиселектора.
 * Нужна для корректного выбора одиночного диалога, если есть множественный выбор пользователей.
 *
 * @author ma.kolpakov
 */
internal class CombinedMultiSelectorItemHandleStrategy : SelectorItemHandleStrategy<SelectorItemModel> {

    override fun onItemClick(item: SelectorItemModel): ClickHandleStrategy =
        when (item) {
            is DialogSelectorItemModel -> ClickHandleStrategy.COMPLETE_SELECTION
            is TitleItemModel -> ClickHandleStrategy.IGNORE
            else -> ClickHandleStrategy.DEFAULT
        }
}
