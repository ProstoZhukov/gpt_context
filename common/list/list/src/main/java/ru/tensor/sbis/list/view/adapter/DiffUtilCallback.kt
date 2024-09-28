package ru.tensor.sbis.list.view.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Item

/**
 * Реализация коллбека сравнения списков, делегирующая сравнение контента элементов самим элементам.
 *
 * Для элементов, имеющих флаг [Item.isMergeable], добавляет в полезную нагрузку элемент, который нужно смержить
 * (см. [getChangePayload]).
 *
 * @property oldList уже имеющийся список.
 * @property newList новый список.
 */
internal class DiffUtilCallback(
    private val oldList: List<AnyItem>,
    private val newList: List<AnyItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        val old = oldList[oldIndex]
        val new = newList[newIndex]
        return areItemsTheSame(old, new)
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        val old = oldList[oldIndex]
        val new = newList[newIndex]
        if (old::class.java != new::class.java
            || old.data::class.java != new.data::class.java
        ) {
            return false
        }
        return oldList[oldIndex].hasTheSameContent(newList[newIndex])
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        if (oldList[oldItemPosition].isMergeable()) return ItemPayload(newList[newItemPosition])

        return null
    }

    companion object {

        fun areItemsTheSame(
            first: AnyItem,
            second: AnyItem
        ): Boolean {
            if (first::class.java != second::class.java
                || first.data::class.java != second.data::class.java
            ) {
                return false
            }

            return first.areTheSame(second)
        }
    }
}

internal class ItemPayload(val item: AnyItem)