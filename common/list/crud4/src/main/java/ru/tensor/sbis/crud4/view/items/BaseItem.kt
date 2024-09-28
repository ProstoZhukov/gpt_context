package ru.tensor.sbis.crud4.view.items

import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.ItemOptions
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.list.view.item.comparator.DefaultComparable
import ru.tensor.sbis.list.view.item.merge.DefaultMergeable
import ru.tensor.sbis.list.view.item.merge.MergeableItem
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Ячейка с дефолтной реализацией
 *
 * @author ma.kolpakov
 */
internal class BaseItem<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER> constructor(
    data: DATA,
    hLevel: Int,
    viewHolderHelper: BaseItemViewHolderHelper<DATA, IDENTIFIER>,
    comparable: ComparableItem<DATA> = DefaultComparable(data),
    options: ItemOptions = Options(level = hLevel),
    mergeable: MergeableItem<DATA> = DefaultMergeable(),
) : Item<DATA, BaseItemViewHolder<DATA, IDENTIFIER>>(
    data,
    viewHolderHelper,
    comparable,
    options,
    mergeable,
)

