package ru.tensor.sbis.design.selection.ui.list

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.view.item.comparator.ComparableItem

/**
 * @author ma.kolpakov
 */
internal class SelectorItemComparable<DATA : SelectorItemModel>(
    private val item: DATA
) : ComparableItem<DATA> {

    /**
     * Внешний вид зависит от состояния выбора, но [SelectorItemModel.meta] не копируется
     *
     * TODO: 2/27/2020 https://online.sbis.ru/opendoc.html?guid=79eae5a2-52e3-449f-a4d6-49a74e76b19e
     */
    private val meta = item.meta.copy()

    override fun areTheSame(otherItem: DATA): Boolean {
        return item.id == otherItem.id
    }

    override fun hasTheSameContent(otherItem: DATA): Boolean =
        item.compareTo(otherItem) && meta == otherItem.meta

    private fun DATA.compareTo(other: DATA): Boolean =
        id == other.id && title == other.title && subtitle == other.subtitle

}