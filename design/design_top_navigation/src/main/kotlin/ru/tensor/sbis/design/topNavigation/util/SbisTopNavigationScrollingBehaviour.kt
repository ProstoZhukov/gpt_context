package ru.tensor.sbis.design.topNavigation.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterBehavior
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import kotlin.math.abs

/**
 * Поведение для скрывания шапки при скроле View.
 *
 * @author da.zolotarev
 */

class SbisTopNavigationScrollingBehaviour(context: Context?, attrs: AttributeSet? = null) :
    CoordinatorLayout.Behavior<View>() {

    private var topNav: SbisTopNavigationView? = null
    private var needSendDown = false
    private var oldY: Float? = null

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return if (dependency is SbisTopNavigationView) {
            topNav = dependency
            true
        } else {
            false
        }
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        val rv = child as? RecyclerView
        return if (rv != null) {
            parent.onLayoutChild(child, layoutDirection)
            // двигаем список под шапкой на высоту шапки, чтобы он ее не перекрывал.
            child.offsetTopAndBottom(topNav?.height ?: 0)
            true
        } else {
            false
        }
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_CANCEL) {
            return false
        }

        val footerItemsMargins = topNav?.controller?.getFootersMarginSum() ?: 0

        val isNotMinimumHeight =
            (topNav?.controller?.getFootersMeasuredHeight() ?: 0) != abs(footerItemsMargins)

        if (isMoveDownDirection(ev) == false && isNotMinimumHeight) {
            needSendDown = true
            topNav?.controller?.gestureDetector?.onTouchEvent(ev)
            oldY = ev.y
        } else if (isMoveDownDirection(ev) == true && footerItemsMargins != 0) {
            needSendDown = true
            topNav?.controller?.gestureDetector?.onTouchEvent(ev)
            oldY = ev.y
        } else {
            if (needSendDown) {
                /*
                посылаем фейковый ивент, с координатами последнего ивента,
                чтобы список не "дергался" после скрытия или показа подвала
                 */
                child.onTouchEvent(
                    MotionEvent.obtain(
                        ev.downTime,
                        ev.eventTime,
                        MotionEvent.ACTION_DOWN,
                        ev.x,
                        ev.y,
                        ev.metaState
                    )
                )
                needSendDown = false
            }
            child.onTouchEvent(ev)
            oldY = ev.y
        }
        return true
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        val isExistScrollableFooters =
            (topNav?.footerItems?.count { it.behaviour != SbisTopNavigationFooterBehavior.FIXED } ?: 0) > 0

        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            oldY = ev.y
        }

        // перехватываем ивенты скролла view, чтобы перенаправить их шапке
        return isExistScrollableFooters && ev.actionMasked == MotionEvent.ACTION_MOVE
    }

    private fun isMoveDownDirection(ev: MotionEvent): Boolean? {
        if (ev.actionMasked == MotionEvent.ACTION_MOVE && oldY != null) {
            val dy = ev.y - (oldY ?: 0f)
            return dy > 0
        }
        return null
    }

}