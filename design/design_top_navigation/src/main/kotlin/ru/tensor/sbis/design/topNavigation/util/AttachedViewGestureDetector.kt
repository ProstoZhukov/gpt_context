package ru.tensor.sbis.design.topNavigation.util

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationInternalApi
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.view_ext.gesture.SimpleOnGestureListenerCompat
import kotlin.math.abs

/**
 * Листенер событий для детектора кликов.
 *
 * @author da.zolotarev
 */
internal class AttachedViewGestureDetector(
    private val sbisTopNavigationView: SbisTopNavigationView,
    private val publishScope: CoroutineScope,
    private val controller: SbisTopNavigationInternalApi
) : SimpleOnGestureListenerCompat {

    /** @SelfDocumented */
    var rvHeight: Int? = null

    /** @SelfDocumented */
    var recycler: RecyclerView? = null

    /** @SelfDocumented */
    val dYShiftEvents = MutableSharedFlow<Int>()

    override fun onScrollCompat(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (e1 == null && abs(distanceX) == e2.x && abs(distanceY) == e2.y) {
            // Скролла нет, находимся в начальной позиции жеста, игнорируем.
            return true
        }
        val isDownDirection = distanceY < 0
        if (isDownDirection && isFooterExpanded() || !isDownDirection && isFooterCollapsed()) {
            recycler?.scrollBy(0, distanceY.toInt())
        }
        sendScrollDistanceYToTopNav(distanceY)
        return true
    }

    override fun onFlingCompat(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        sendScrollVelocityToTopNav(velocityY) {
            // после окончания анимации сворачивания/разворачивания потребуется дополнительный fling
            if (velocityY > 0 && isFooterExpanded() || velocityY < 0 && isFooterCollapsed()) {
                recycler?.fling(0, -velocityY.toInt())
            }
        }
        return true
    }

    private fun sendScrollDistanceYToTopNav(distanceY: Float) {
        if (recycler?.measuredHeight != rvHeight) {
            rvHeight = recycler?.measuredHeight ?: 0
            return
        }
        sbisTopNavigationView.footerView.stopAnimation()
        publishScope.launch { dYShiftEvents.emit(distanceY.toInt()) }
    }

    private fun sendScrollVelocityToTopNav(velocity: Float, onAnimationEnd: () -> Unit) {
        sbisTopNavigationView.footerView.animateItemsByYVelocity(velocity, onAnimationEnd)
    }

    private fun isFooterCollapsed() = controller.getFootersMeasuredHeight() == abs(controller.getFootersMarginSum())

    private fun isFooterExpanded() = controller.getFootersMarginSum() == 0

}