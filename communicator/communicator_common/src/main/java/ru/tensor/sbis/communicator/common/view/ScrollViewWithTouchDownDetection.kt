package ru.tensor.sbis.communicator.common.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.widget.ScrollView

/**
 * ScrollView с дополнительной возможностью получения событий [MotionEvent].action == ACTION_DOWN
 *
 * @author vv.chekurda
 */
class ScrollViewWithTouchDownDetection @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var touchListener: OnTouchListener? = null

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        touchListener?.let {
            if (ev.action == ACTION_DOWN) it.onTouch(this, ev)
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(l: OnTouchListener?) {
        super.setOnTouchListener(l)
        touchListener = l
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        touchListener = null
    }
}