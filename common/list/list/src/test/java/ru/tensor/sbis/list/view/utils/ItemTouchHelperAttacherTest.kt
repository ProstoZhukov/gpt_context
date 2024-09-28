package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Test
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.calback.ItemMoveCallback
import ru.tensor.sbis.list.view.calback.ItemTouchCallback

internal class ItemTouchHelperAttacherTest {

    private val mockRecyclerView = mock<RecyclerView>()
    private val holder = mock<ListDataHolder>()
    private val mockAttachToRecyclerView = mock<(RecyclerView, ItemTouchCallback) -> Unit>()
    private val mockItemTouchCallback = mock<ItemTouchCallback>()
    private val itemTouchHelperAttacher = ItemTouchHelperAttacher(
        holder,
        mockItemTouchCallback,
        mockAttachToRecyclerView
    )

    @Test
    fun attach() {
        itemTouchHelperAttacher.attach(mockRecyclerView)

        verify(mockAttachToRecyclerView).invoke(mockRecyclerView, mockItemTouchCallback)
    }

    @Test
    fun setItemMoveCallback() {
        val itemMoveCallback = mock<ItemMoveCallback>()

        itemTouchHelperAttacher.setItemMoveCallback(itemMoveCallback)

        verify(mockItemTouchCallback).itemMoveCallback = itemMoveCallback
    }
}