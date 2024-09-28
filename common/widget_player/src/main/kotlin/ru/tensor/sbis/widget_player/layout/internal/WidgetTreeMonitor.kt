package ru.tensor.sbis.widget_player.layout.internal

import android.os.Trace
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.annotation.UiThread
import androidx.core.view.ScrollingView
import androidx.core.view.descendants
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.widget_player.layout.VisibleOnScreenContentView
import ru.tensor.sbis.widget_player.layout.internal.scheduler.ChoreographerFrameCallbackScheduler
import ru.tensor.sbis.widget_player.layout.internal.scheduler.FrameCallback

/**
 * @author am.boldinov
 */
@UiThread
internal class WidgetTreeMonitor(
    private val root: ViewGroup,
    private val frameScanCallback: FrameCallback? = null
) {

    private var parentScrollingView: ViewGroup? = null
    private val frameScheduler = ChoreographerFrameCallbackScheduler {
        onFrameCallbackAvailable()
    }
    private var bufferedTreeScan: WidgetTreeScan? = null

    init {
        root.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                parentScrollingView = findViewParent<ViewGroup>(v) {
                    it.isScrollable()
                }
                if (v.isLayoutRequested) {
                    frameScheduler.schedule()
                }
            }

            override fun onViewDetachedFromWindow(v: View) {
                frameScheduler.unschedule()
                bufferedTreeScan = null
                parentScrollingView = null
            }
        })
    }

    fun dispatchLayoutRequested() {
        if (root.isAttachedToWindow) {
            frameScheduler.schedule()
        }
    }

    fun scan(): WidgetTreeScan {
        return bufferedTreeScan ?: scanViewsInternal().also {
            bufferedTreeScan = it
        }
    }

    private fun onFrameCallbackAvailable() {
        scanViewsInternal().also {
            bufferedTreeScan = it
        }
        frameScanCallback?.run()
    }

    private fun scanViewsInternal(): WidgetTreeScan {
        Trace.beginSection("WidgetTreeMonitor#scanViewsInternal")
        val dynamicScreenViews = mutableListOf<VisibleOnScreenContentView>()
        root.descendants.forEach {
            if (it is VisibleOnScreenContentView) {
                dynamicScreenViews.add(it)
            }
        }
        Trace.endSection()
        return WidgetTreeScan(parentScrollingView, dynamicScreenViews)
    }

    private fun View.isScrollable(): Boolean {
        return this is ScrollingView || this is ScrollView || this is HorizontalScrollView
    }
}

internal class WidgetTreeScan(
    val parentScrollingView: ViewGroup?,
    val dynamicScreenViews: List<VisibleOnScreenContentView>
)