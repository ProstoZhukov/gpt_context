package ru.tensor.sbis.swipeablelayout.util

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import curtains.DispatchState
import curtains.TouchEventInterceptor
import curtains.phoneWindow
import curtains.touchEventInterceptors
import ru.tensor.sbis.common.util.DeviceUtils
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.MenuOpened
import timber.log.Timber

/**
 * Инструмент для принудительного закрытия свайп-меню по клику на любой области экрана.
 * Устанавливается для каждого [Window], содержащего [SwipeableLayout], и оперирует всеми доступными свайп-меню на
 * экране.
 *
 * @author us.bessonov
 */
internal object CloseOnOutsideTouchEventInterceptor : TouchEventInterceptor {

    private val attachedViews = mutableSetOf<SwipeableLayout>()

    private val tempRect = Rect()

    /** @SelfDocumented */
    fun attach(view: SwipeableLayout) {
        attachedViews.add(view)
        initWindowTouchInterceptorIfNeeded(view)
    }

    /** @SelfDocumented */
    fun detach(view: SwipeableLayout) {
        attachedViews.remove(view)
        removeWindowTouchInterceptorIfNeeded(view)
    }

    override fun intercept(motionEvent: MotionEvent, dispatch: (MotionEvent) -> DispatchState): DispatchState {
        var isViewClosed = false
        attachedViews.forEach { view ->
            if (isMatchingDownEventOutsideWhenMenuOpen(view, motionEvent)) {
                view.close()
                isViewClosed = true
            }
        }
        return try {
            dispatch(motionEvent)
        } catch (e: Exception) {
            logDiagnosticsInfo(e, isViewClosed, motionEvent)
            DispatchState.Consumed
        }
    }

    private fun initWindowTouchInterceptorIfNeeded(view: View) {
        val interceptors = view.phoneWindow?.touchEventInterceptors
            ?: run {
                Timber.w(
                    "Cannot access window from SwipeableLayout. Swipe menu won't be closed on outside touch"
                )
                return
            }
        if (!interceptors.contains(this)) {
            interceptors.add(this)
        }
    }

    private fun removeWindowTouchInterceptorIfNeeded(view: View) {
        if (attachedViews.isEmpty()) {
            view.phoneWindow?.touchEventInterceptors?.remove(this)
        }
    }

    private fun isMatchingDownEventOutsideWhenMenuOpen(view: SwipeableLayout, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && view.lastEvent is MenuOpened) {
            tempRect.apply { view.getGlobalVisibleRect(this) }
            return !tempRect.contains(event.x.toInt(), event.y.toInt())
        }
        return false
    }

    private fun logDiagnosticsInfo(e: Exception, isMenuClosed: Boolean, event: MotionEvent) {
        val parentList = attachedViews.firstNotNullOfOrNull { it.parent as? RecyclerView? }
        val displaySize = attachedViews.firstOrNull()?.context?.let {
            DeviceUtils.getScreenWidthInPx(it) to DeviceUtils.getScreenHeightInPx(it)
        }
        Timber.w(e, "Cannot dispatch event $event (${e.message}). Displayed list is $parentList. Is menu closed: $isMenuClosed. Display size: $displaySize")
    }

}