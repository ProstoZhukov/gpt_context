package ru.tensor.sbis.list.view.binding

import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.ItemOptions
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.list.view.item.comparator.DefaultComparable
import ru.tensor.sbis.list.view.item.merge.DefaultMergeable
import ru.tensor.sbis.list.view.item.merge.MergeableItem

/**
 * Элемент списка с использованием DataBinding, должен подойти в большинстве случаев.
 *
 * @param DATA тип данных для биндинга.
 * @constructor
 *
 * @param data данные для биндинга.

 * @param comparable методы для сравнения содержимого значений поля [data].
 * @param options дополнительные опции, вроде события клика, отступов и пр. см. README_list.md.
 * @param mergeable метод обновления вью модели данными другой вью модели.
 */
@Suppress("UNCHECKED_CAST")
open class BindingItem<DATA : Any> constructor(
    data: DATA,
    dataBindingViewHolderHelper: DataBindingViewHolderHelper<DATA>,
    comparable: ComparableItem<DATA> = DefaultComparable(data),
    options: ItemOptions = Options(),
    mergeable: MergeableItem<DATA> = DefaultMergeable(),
) : Item<DATA, DataBindingViewHolder>(
    data,
    dataBindingViewHolderHelper,
    comparable,
    options,
    mergeable
) {
    /**
     * @param layoutId ресурс для создания View через DataBinding.
     * !!!Должен содержать тег "layout" и элемент "data" с ключом "viewModel"!!!
     */
    constructor(
        data: DATA,
        layoutId: Int,
        comparable: ComparableItem<DATA> = DefaultComparable(data),
        options: ItemOptions = Options(),
        mergeable: MergeableItem<DATA> = DefaultMergeable(),
    ) : this(
        data,
        DataBindingViewHolderHelper(LayoutIdViewFactory(layoutId)),
        comparable,
        options,
        mergeable
    )
}