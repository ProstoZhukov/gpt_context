package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.calback.ItemMoveCallback
import ru.tensor.sbis.list.view.calback.ItemTouchCallback

/**
 * Логика добавления колбека жестов в [RecyclerView].
 * @property itemTouchCallback ItemTouchCallback
 * @property attachToRecyclerView Function2<RecyclerView, ItemTouchCallback, Unit>
 * @constructor
 */
internal class ItemTouchHelperAttacher(
    sectionsHolder: ListDataHolder,
    private var itemTouchCallback: ItemTouchCallback = ItemTouchCallback(sectionsHolder),
    private val attachToRecyclerView: (RecyclerView, ItemTouchCallback) -> Unit = ::attach
) {
    /**
     * Прикрепление [ItemTouchHelper].
     * @param recyclerView RecyclerView
     */
    fun attach(recyclerView: RecyclerView) {
        attachToRecyclerView(recyclerView, itemTouchCallback)
    }

    /**
     * Добавление колбека в [ItemTouchHelper], добавленные методом [attach].
     * @param itemMoveCallback ItemMoveCallback
     */
    fun setItemMoveCallback(itemMoveCallback: ItemMoveCallback) {
        itemTouchCallback.itemMoveCallback = itemMoveCallback
    }
}

/**
 * Вспомогательный метод, используется для тестирования.
 * @param recyclerView RecyclerView
 * @param itemTouchCallback ItemTouchCallback
 */
internal fun attach(recyclerView: RecyclerView, itemTouchCallback: ItemTouchCallback) {
    ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView)
}