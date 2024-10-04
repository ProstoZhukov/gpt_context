package ru.tensor.sbis.design.selection.ui.list

import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy.IGNORE
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.ListItem
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Класс для преобразования элементов уровня данных в элементы уровня представления
 *
 * @author ma.kolpakov
 */
internal class ListItemMapper(
    private val selectorCustomisation: SelectorCustomisation,
    private val viewHolderHelpers: Map<Any, ViewHolderHelper<SelectorItemModel, *>>,
    private val clickDelegate: SelectionClickDelegate<SelectorItemModel>
) {

    fun toItem(model: SelectorItemModel): ListItem {
        val viewHolderType = selectorCustomisation.getViewHolderType(model)
        val viewHolderHelper = checkNotNull(viewHolderHelpers[viewHolderType]) {
            "Unable to get ViewHolderHelper for key $viewHolderType. Available keys: ${viewHolderHelpers.keys}"
        }
        val clickAction: () -> Unit =
            if (model.meta.handleStrategy == IGNORE) {
                { /* Обработка нажатия не требуется */ }
            } else {
                SelectionItemClickAction(model, clickDelegate)
            }

        return Item(
            model,
            viewHolderHelper,
            SelectorItemComparable(model),
            Options(
                clickAction = clickAction,
                longClickAction = { clickDelegate.onItemLongClicked(model) },
                customSidePadding = true
            )
        )
    }

    private class SelectionItemClickAction(
        private val model: SelectorItemModel,
        private val clickDelegate: SelectionClickDelegate<SelectorItemModel>
    ) : () -> Unit {

        private val hasNestedItems = model is HierarchySelectorItemModel && model.hasNestedItems

        /**
         * Отметка о том, что элемент нажат. Такой "одноразовый" подход можно применить, потому что
         * модели и подписки пересоздаются при изменении списка, а перенос в список выбранных -
         * изменение
         */
        private var isClicked = false

        override fun invoke() {
            if (!isClicked || hasNestedItems) {
                isClicked = true
                clickDelegate.onItemClicked(model)
            }
        }
    }
}