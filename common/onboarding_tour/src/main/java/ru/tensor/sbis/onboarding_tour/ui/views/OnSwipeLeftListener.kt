package ru.tensor.sbis.onboarding_tour.ui.views

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.Px
import androidx.core.view.GestureDetectorCompat
import ru.tensor.sbis.design.utils.dpToPx
import ru.tensor.sbis.design.view_ext.gesture.SimpleOnGestureListenerCompat
import kotlin.math.abs

/** Детектор жеста свайпа влево. */
private class OnSwipeLeftListener(
    view: View,
    firstPage: Boolean,
    private val onSwipeLeft: () -> Unit
) : OnTouchListener {

    @Px
    private val swipeThreshold = dpToPx(view.context, SWIPE_THRESHOLD_DP)

    @Px
    private val scrollThreshold = dpToPx(view.context, SCROLL_THRESHOLD_DP)

    @Px
    private val velocityThreshold = dpToPx(view.context, SWIPE_VELOCITY_THRESHOLD_DP)

    private val gestureListener = object : SimpleOnGestureListenerCompat {

        override fun onSingleTapConfirmed(e: MotionEvent) = view.performClick()

        // С false не работает onFling на первом экране
        override fun onDown(e: MotionEvent): Boolean {
            return firstPage
        }

        // Потребляем скролл вперед, скролл назад обрабатывается MotionLayout
        override fun onScrollCompat(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return !(abs(distanceX) > abs(distanceY) && distanceX < 0 && abs(distanceX) > scrollThreshold)
        }

        override fun onFlingCompat(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean =
            if (e1 != null) {
                val dY = e2.y - e1.y
                val dX = e2.x - e1.x
                if (abs(dX) > abs(dY) && dX < 0 && abs(dX) > swipeThreshold && abs(velocityX) > velocityThreshold) {
                    onSwipeLeft()
                    true
                } else {
                    false
                }
            } else {
                false
            }
    }

    private val gestureDetector = GestureDetectorCompat(view.context, gestureListener)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return event?.let(gestureDetector::onTouchEvent) ?: true
    }

    companion object {
        const val SCROLL_THRESHOLD_DP = 5
        const val SWIPE_THRESHOLD_DP = 32
        const val SWIPE_VELOCITY_THRESHOLD_DP = 32
    }
}

/**
 * Установить слушатель жеста свайпа влево.
 */
internal fun View.setOnSwipeLeftListener(firstPage: Boolean, onSwipeLeft: () -> Unit) {
    removeOnSwipeLeftListener()
    setOnTouchListener(OnSwipeLeftListener(this, firstPage, onSwipeLeft))
}

/**
 * Удалить слушатель жеста свайпа влево.
 */
internal fun View.removeOnSwipeLeftListener() {
    setOnTouchListener(null)
}