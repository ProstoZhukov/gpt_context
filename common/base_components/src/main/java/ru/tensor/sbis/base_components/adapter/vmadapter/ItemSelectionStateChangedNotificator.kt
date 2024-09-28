package ru.tensor.sbis.base_components.adapter.vmadapter

import android.view.View
import android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SELECTED
import androidx.recyclerview.widget.RecyclerView

/**
 *  Нотификатор [adapter]`а о изменения состояния `selected` у View, сообщаяющий о изменении элемента
 *  с указанным [itemPosition].
 *  Используется, например, для обновления декорации ячеек.
 */
internal class ItemSelectionStateChangedNotificator(private val itemPosition: Int, private val adapter: RecyclerView.Adapter<*>) : View.AccessibilityDelegate() {

    override fun sendAccessibilityEvent(host: View, eventType: Int) {
        if (eventType == TYPE_VIEW_SELECTED) {
            host.post {
                adapter.notifyItemChanged(itemPosition)
            }
        }
        super.sendAccessibilityEvent(host, eventType)
    }
}