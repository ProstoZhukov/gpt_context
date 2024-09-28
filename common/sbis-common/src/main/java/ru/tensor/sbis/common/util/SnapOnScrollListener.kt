package ru.tensor.sbis.common.util

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import android.view.View

/**
 * Слушатель прокрутки RecyclerView с завязкой на прикреплённый к RecyclerView SnapHelper
 *
 * После завершения прокрутки (состояние прокрутки изменяется на SCROLL_STATE_IDLE)
 * определятся "привязанный" SnapHelper-ом элемент и его позиция, затем передаётся в [onSnapItemChanged],
 * который следует переопределить в наследниках.
 *
 * @author sa.nikitin
 */
abstract class SnapOnScrollListener(private val snapHelper: SnapHelper) : RecyclerView.OnScrollListener() {

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            onSnapPositionMaybeChanged(recyclerView)
        }
    }

    private fun onSnapPositionMaybeChanged(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager == null) {
            snapPosition = RecyclerView.NO_POSITION
            return
        }
        val snapView = snapHelper.findSnapView(layoutManager)
        if (snapView == null) {
            snapPosition = RecyclerView.NO_POSITION
            return
        }
        val newSnapPosition = layoutManager.getPosition(snapView)
        if (snapPosition != newSnapPosition) {
            snapPosition = newSnapPosition
            if (newSnapPosition != RecyclerView.NO_POSITION) {
                onSnapItemChanged(newSnapPosition, snapView)
            }
        }
    }

    /**
     * Изменился "привязанный" SnapHelper-ом элемент
     *
     * @param newSnapPosition   Новая позиция "привязанного" SnapHelper-ом элемента
     * @param newSnapView       Новый "привязанный" SnapHelper-ом элемент
     */
    abstract fun onSnapItemChanged(newSnapPosition: Int, newSnapView: View)
}