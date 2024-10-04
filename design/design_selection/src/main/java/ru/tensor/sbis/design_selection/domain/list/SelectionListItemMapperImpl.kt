package ru.tensor.sbis.design_selection.domain.list

import ru.tensor.sbis.design_selection.ui.content.di.SelectionListItemMapper
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.list.view.item.*
import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * Реализация маппера [SelectionListItemMapper] компонента списка для создания [Item]
 * по моделям элементов [SelectionItem] в компоненте выбора.
 *
 * @property itemsCustomisation кастомизатор элементов списка, доступных для выбора.
 * @property viewHolderHelpers набор реализаций [ViewHolderHelper] для создания вью-холдеров.
 * @property clickDelegate делагат для обработки кликов по ячейкам.
 * @property isMultiSelection true, если необходимо отображение ячеек в режиме мультивыбора,
 * false - для одиночного выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionListItemMapperImpl(
    private val itemsCustomisation: SelectableItemsCustomization<SelectionItem>,
    private val viewHolderHelpers: Map<Any, ViewHolderHelper<SelectionItem, *>>,
    private val clickDelegate: SelectionClickDelegate,
    private val isMultiSelection: Boolean = true
) : SelectionListItemMapper {

    override fun map(
        item: SelectionItem,
        defaultClickAction: (SelectionItem) -> Unit,
    ): AnyItem {
        val viewHolderType = itemsCustomisation.getViewHolderType(item, isMultiSelection)
        val viewHolderHelper = checkNotNull(viewHolderHelpers[viewHolderType]) {
            "Unable to get ViewHolderHelper for key $viewHolderType. Available keys: ${viewHolderHelpers.keys}"
        }

        return Item(
            data = item,
            viewHolderHelper = viewHolderHelper,
            comparable = SelectionComparable(item),
            options = SelectionOptions(
                options = Options(
                    clickAction = { clickDelegate.onItemClicked(item) },
                    longClickAction = { clickDelegate.onItemLongClicked(item) },
                    customSidePadding = true
                ),
                isMultiSelection = isMultiSelection
            )
        )
    }
}

private class SelectionComparable(val item: SelectionItem) : ComparableItem<SelectionItem> {
    override fun areTheSame(otherItem: SelectionItem): Boolean =
        item.id == otherItem.id
}

/**
 * Опции элемента списка компонента выбора.
 * @see ItemOptions
 *
 * @property options оригинальные опции компонента списка.
 * @property isMultiSelection true, если необходимо отображение ячеек в режиме мультивыбора,
 * false - для одиночного выбора.
 */
private data class SelectionOptions(
    val options: Options,
    val isMultiSelection: Boolean
) : ItemOptions by options