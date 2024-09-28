package ru.tensor.sbis.widget_player

import android.content.Context
import android.graphics.Rect
import android.os.Trace
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.widget_player.api.WidgetPlayerApi
import ru.tensor.sbis.widget_player.api.WidgetSource
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.WidgetStoreOwner
import ru.tensor.sbis.widget_player.layout.VisibleOnScreenContentView
import ru.tensor.sbis.widget_player.layout.internal.GlobalScrollObserver
import ru.tensor.sbis.widget_player.layout.internal.WidgetTreeMonitor
import ru.tensor.sbis.widget_player.renderer.WidgetPlayerApiMediator
import ru.tensor.sbis.widget_player.renderer.WidgetPlayerBodyRenderer
import ru.tensor.sbis.widget_player.converter.frame.WidgetFrameJsonConverterProvider
import ru.tensor.sbis.widget_player.layout.internal.WidgetStateStore
import ru.tensor.sbis.widget_player.layout.internal.WidgetViewLifecycleOwner
import ru.tensor.sbis.widget_player.layout.internal.WidgetViewModelStoreOwner
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStoreFactory
import ru.tensor.sbis.widget_player.util.update
import ru.tensor.sbis.widget_player.util.viewModelLazy

/**
 * @author am.boldinov
 */
private const val GLOBAL_SCROLL_THROTTLE_MILLIS = 50L

class WidgetPlayer private constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    private val apiMediator: WidgetPlayerApiMediator,
    private val lifecycleOwner: WidgetViewLifecycleOwner,
    private val viewModelStoreOwner: WidgetViewModelStoreOwner
) : ViewGroup(context, attrs, defStyleAttr),
    WidgetPlayerApi by apiMediator,
    WidgetStoreOwner by apiMediator,
    LifecycleOwner by lifecycleOwner {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : this(
        context,
        attrs,
        defStyleAttr,
        WidgetPlayerApiMediator(),
        WidgetViewLifecycleOwner(),
        WidgetViewModelStoreOwner()
    )

    private val rootRect = Rect()

    private val treeMonitor = WidgetTreeMonitor(this) {
        dispatchChildrenScreenPositionChanged()
    }

    init {
        isSaveEnabled = true
        apiMediator.attachTo(
            lifecycle,
            WidgetPlayerBodyRenderer(
                player = this,
                stateStore = viewModelStoreOwner.viewModelLazy {
                    WidgetStateStore()
                }
            ),
            WidgetPlayerStoreFactory(
                storeFactory = AndroidStoreFactory.default(),
                frameConverterProvider = WidgetFrameJsonConverterProvider()
            )
        )
        GlobalScrollObserver.subscribeOn(this, GLOBAL_SCROLL_THROTTLE_MILLIS) {
            dispatchChildrenScreenPositionChanged()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewModelStoreOwner.dispatchAttached(this)
        lifecycleOwner.dispatchAttachedToPlayer(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleOwner.dispatchDetachedFromPlayer(this)
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        if (childCount > 1) {
            throw IllegalStateException("WidgetPlayer can host only one direct child")
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        @Suppress("UNNECESSARY_SAFE_CALL")
        treeMonitor?.dispatchLayoutRequested()
    }

    @Deprecated("Используйте новое API", ReplaceWith("setWidgetSource(WidgetSource.Body(body))"))
    fun setBody(body: WidgetBody?) {
        apiMediator.setWidgetSource(WidgetSource.Body(body))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Trace.beginSection("WidgetPlayer#onMeasure")
        var width = 0
        var height = 0
        takeRootWidget()?.let {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            width = it.measuredWidth
            height = it.measuredHeight
        }
        width += paddingStart + paddingEnd
        height = maxOf(height, suggestedMinimumHeight)
        height += paddingTop + paddingBottom
        setMeasuredDimension(width, height)
        Trace.endSection()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Trace.beginSection("WidgetPlayer#onLayout")
        takeRootWidget()?.let {
            val left = paddingLeft
            val top = paddingTop
            val right = left + it.measuredWidth
            val bottom = top + it.measuredHeight
            it.layout(left, top, right, bottom)
        }
        if (changed) {
            dispatchChildrenScreenPositionChanged()
        }
        Trace.endSection()
    }

    private fun takeRootWidget(): View? {
        return children.firstOrNull()
    }

    private fun dispatchChildrenScreenPositionChanged() {
        Trace.beginSection("WidgetPlayer#dispatchChildrenScreenPositionChanged")
        treeMonitor.scan().apply {
            (parentScrollingView ?: this@WidgetPlayer).let { root ->
                val rootChanged = rootRect.update {
                    root.getDrawingRect(it)
                }
                dynamicScreenViews.forEach { dynamic ->
                    if ((dynamic as View).isAttachedToWindow && dynamic.isVisible) {
                        val dynamicRect = dynamic.getPositionInRoot()
                        val dynamicChanged = dynamicRect.update {
                            dynamic.getDrawingRect(it)
                            root.offsetDescendantRectToMyCoords(dynamic, it)
                        }
                        if (dynamicChanged || rootChanged) {
                            dynamic.screenPositionHandler.onScreenPositionChanged(dynamic, dynamicRect, rootRect)
                        }
                    }
                }
            }
        }
        Trace.endSection()
    }

    private fun VisibleOnScreenContentView.getPositionInRoot(): Rect {
        return with(this as View) {
            getTag(R.id.widget_player_view_position) as? Rect ?: Rect().also {
                setTag(R.id.widget_player_view_position, it)
            }
        }
    }
}