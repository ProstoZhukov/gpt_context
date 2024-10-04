package ru.tensor.sbis.design.view_ext.gesture

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.annotation.DeprecatedSinceApi

interface SimpleOnGestureListenerCompat : GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean = false

    override fun onDoubleTap(e: MotionEvent): Boolean = false

    override fun onDoubleTapEvent(e: MotionEvent): Boolean = false

    override fun onDown(e: MotionEvent): Boolean = false

    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent): Boolean = false

    override fun onLongPress(e: MotionEvent) = Unit

    @DeprecatedSinceApi(34, "Use onFlingCompat")
    @ExperimentalStdlibApi
    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return onFlingCompat(e1, e2, velocityX, velocityY)
    }

    @DeprecatedSinceApi(34, "Use onScrollCompat")
    @ExperimentalStdlibApi
    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return onScrollCompat(e1, e2, distanceX, distanceY)
    }

    fun onFlingCompat(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean = false

    fun onScrollCompat(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean = false

}

