package ru.tensor.sbis.list.view.item.comparator

/**
 * Реализация - заглушка. На случай, если обновление списка никогда не производится.
 * @param ITEM @SelDocumented.
 * @property firstItem ITEM @SelDocumented.
 */
class DefaultComparable<ITEM>(private val firstItem: ITEM) : ComparableItem<ITEM> {

    override fun areTheSame(otherItem: ITEM): Boolean {
        return firstItem == otherItem
    }

    override fun hasTheSameContent(otherItem: ITEM): Boolean {
        return firstItem == otherItem
    }
}

