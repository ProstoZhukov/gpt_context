package ru.tensor.sbis.list.view.item.merge

/**
 * Класс-заглушка.
 */
class DefaultMergeable<ITEM> : MergeableItem<ITEM> {

    override fun areTheSame(otherItem: ITEM) = this == otherItem

    override fun mergeFrom(otherItem: ITEM) = Unit
}