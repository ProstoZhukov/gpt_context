package ru.tensor.sbis.design.cloud_view.utils.swipe

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.cloud_view.CloudView

/**
 * Реализация колбэка [ItemTouchHelper.Callback] для цитирования по свайпу компонента ячейка-облако [CloudView].
 * @see MessageSwipeToQuoteBehavior
 * @see DefaultSwipeToQuoteBehavior
 *
 * @author vv.chekurda
 */
open class MessageSwipeToQuoteCallback : ItemTouchHelper.Callback() {

    private val RecyclerView.ViewHolder.swipeBehavior: MessageSwipeToQuoteBehavior?
        get() = this as? MessageSwipeToQuoteBehavior

    override fun getAnimationDuration(
        recyclerView: RecyclerView,
        animationType: Int,
        animateDx: Float,
        animateDy: Float
    ): Long = SWIPE_RECOVER_DURATION_MS

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
        viewHolder.swipeBehavior?.movementFlags
            ?: makeMovementFlags(0, 0)

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        viewHolder.swipeBehavior?.also {
            val needInvalidate = it.draw(c, dX, isCurrentlyActive)
            if (needInvalidate) recyclerView.invalidate()
        } ?: super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int
    ) = Unit
}