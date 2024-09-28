package ru.tensor.sbis.design.navigation.view.view.tabmenu

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.widget.HorizontalScrollView

/**
 * Горизонтальная ScrollView с возможностью отследить момент окончания скролла.
 *
 * @author ma.kolpakov
 */
@SuppressLint("ClickableViewAccessibility")
internal class SbisHorizontalScrollView(context: Context) : HorizontalScrollView(context) {
    private lateinit var scrollerTask: Runnable
    private var initialPosition = 0

    /** @SelfDocumented */
    var onScrollStopped: (() -> Unit)? = null

    init {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                startScrollerTask()
            }
            false
        }

        scrollerTask = Runnable {
            val newPosition = scrollX
            if (initialPosition - newPosition == 0) {
                onScrollStopped?.invoke()
            } else {
                startScrollerTask()
            }
        }
    }

    private fun startScrollerTask() {
        initialPosition = scrollX
        postDelayed(scrollerTask, CHECK_DELAY)
    }

}

private const val CHECK_DELAY = 100L
