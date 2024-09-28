package ru.tensor.sbis.widget_player.layout.internal

import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.UiThread

/**
 * @author am.boldinov
 */
@UiThread
internal class GlobalScrollObserver private constructor(
    private val root: View,
    private val throttleTimeMillis: Long,
    private val listener: GlobalScrollListener
) {

    companion object {
        fun subscribeOn(
            root: View,
            throttleTimeMillis: Long,
            listener: GlobalScrollListener
        ): GlobalScrollObserver = GlobalScrollObserver(root, throttleTimeMillis, listener)
    }

    private var lastPostTime = 0L

    private val scrollChangedCallback = object : ViewTreeObserver.OnScrollChangedListener, Runnable {
        override fun onScrollChanged() {
            run()
        }

        override fun run() {
            root.removeCallbacks(this)
            val waitTimeMillis = System.currentTimeMillis() - lastPostTime
            if (waitTimeMillis > throttleTimeMillis) {
                lastPostTime = System.currentTimeMillis()
                listener.onScrollChanged()
            } else {
                root.postOnAnimationDelayed(this, waitTimeMillis) // for idle scroll state
            }
        }
    }

    init {
        root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.viewTreeObserver.addOnScrollChangedListener(scrollChangedCallback)
            }

            override fun onViewDetachedFromWindow(v: View) {
                v.viewTreeObserver.removeOnScrollChangedListener(scrollChangedCallback)
                v.removeCallbacks(scrollChangedCallback)
                lastPostTime = 0L
            }
        })
    }
}

internal fun interface GlobalScrollListener {
    fun onScrollChanged()
}