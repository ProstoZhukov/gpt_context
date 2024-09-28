package ru.tensor.sbis.app_file_browser.util

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.Px
import ru.tensor.sbis.design.utils.dpToPx
import ru.tensor.sbis.design.view_ext.gesture.SimpleOnGestureListenerCompat
import kotlin.math.abs

private const val SWIPE_THRESHOLD_DP = 32
private const val SWIPE_VELOCITY_THRESHOLD_DP = 32

/**
 * Инструмент для определения жеста свайпа влево.
 *
 * @author us.bessonov
 */
internal class OnSwipeLeftTouchListener(private val view: View, private val onSwipeLeft: () -> Unit) : OnTouchListener {

    @Px
    private val swipeThreshold = dpToPx(view.context, SWIPE_THRESHOLD_DP)

    @Px
    private val velocityThreshold = dpToPx(view.context, SWIPE_VELOCITY_THRESHOLD_DP)

    private val gestureListener = object : SimpleOnGestureListenerCompat {

        override fun onDown(e: MotionEvent) = true

        override fun onFlingCompat(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null) return false
            val dY = e2.y - e1.y
            val dX = e2.x - e1.x
            if (abs(dX) > abs(dY) && dX < 0 && abs(dX) > swipeThreshold && abs(velocityX) > velocityThreshold) {
                onSwipeLeft()
                return true
            }
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent) = view.performClick()
    }

    private val gestureDetector = GestureDetector(view.context, gestureListener)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent) = gestureDetector.onTouchEvent(event)
}