package ru.tensor.sbis.widget_player.widget.link

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.View
import androidx.annotation.CallSuper
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkData
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkDataSubscriber
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener
import ru.tensor.sbis.richtext.util.RichTextAndroidUtil

/**
 * @author am.boldinov
 */
internal abstract class BaseDecoratedLinkView(
    context: Context,
    private val linkOpener: DecoratedLinkOpener,
    private val linkDataSubscriber: DecoratedLinkDataSubscriber?
) : View(context) {

    private val dataRefreshCallback = DecoratedLinkDataSubscriber.DataRefreshCallback { data ->
        setLinkDataInternal(data, update = true)
    }

    // drawable внутри оборачивает в weak reference - храним callback
    private val drawableCallback = DrawableCallback()
    protected var drawable: Drawable? = null

    protected var linkData: DecoratedLinkData? = null
        private set

    init {
        initListeners()
    }

    fun setLinksClickable(clickable: Boolean) {
        isClickable = clickable
    }

    fun setLinkData(linkData: DecoratedLinkData) {
        setLinkDataInternal(linkData)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachRefreshCallback(checkMissedEvent = true)
        attachDraweeHolder()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        detachRefreshCallback()
        detachDraweeHolder()
    }

    final override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        linkData?.let {
            onMeasureLink(it, widthMeasureSpec, heightMeasureSpec)
        } ?: run {
            setMeasuredDimension(0, 0)
        }
    }

    final override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        linkData?.let {
            canvas.save()
            onDrawLink(it, canvas)
            canvas.restore()
        }
    }

    protected abstract fun onMeasureLink(linkData: DecoratedLinkData, widthMeasureSpec: Int, heightMeasureSpec: Int)

    protected abstract fun onDrawLink(linkData: DecoratedLinkData, canvas: Canvas)

    @CallSuper
    protected open fun onLinkDataUpdated() {
        drawable = linkData.withImage {
            it.draweeHolder.topLevelDrawable?.apply {
                if (bounds.isEmpty) {
                    setBounds(0, 0, it.imageWidth, it.imageHeight)
                }
            }
        }
    }

    private fun initListeners() {
        setOnClickListener {
            linkData?.takeIf { isClickable }?.apply {
                linkOpener.open(context, linkPreview, title)
            }
        }
        setOnLongClickListener {
            linkData?.takeIf { isClickable }?.run {
                RichTextAndroidUtil.copyToClipboard(context, sourceUrl)
                true
            } ?: false
        }
    }

    private fun setLinkDataInternal(data: DecoratedLinkData?, update: Boolean = false) {
        if (linkData != data) {
            if (!update) {
                detachRefreshCallback() // detach previous
            }
            linkData = data
            onLinkDataUpdated()
            if (!update) {
                attachRefreshCallback()
            }
            if (isAttachedToWindow) {
                attachDraweeHolder()
            }
            requestLayout()
            invalidate()
        }
    }

    private fun attachDraweeHolder() {
        linkData.withImage {
            if (!it.draweeHolder.isAttached) {
                drawable?.callback = drawableCallback
                it.draweeHolder.onAttach()
            }
        }
    }

    private fun detachDraweeHolder() {
        linkData.withImage {
            drawable?.callback = null
            it.draweeHolder.onDetach()
        }
    }

    private fun attachRefreshCallback(checkMissedEvent: Boolean = false) {
        linkData?.let {
            linkDataSubscriber?.attachDataRefreshCallback(it.sourceUrl, dataRefreshCallback, checkMissedEvent)
        }
    }

    private fun detachRefreshCallback() {
        linkData?.let {
            linkDataSubscriber?.detachDataRefreshCallback(it.sourceUrl, dataRefreshCallback)
        }
    }

    protected inline fun <R> DecoratedLinkData?.withImage(action: (DecoratedLinkData) -> R): R? {
        return this?.takeIf { it.hasImage() }?.let(action)
    }

    /**
     * Кастомная реализация callback.
     * View не знает о существовании изменяемого Drawable (verifyDrawable вернет false) и его границ, поэтому если перенаправить вызов инвалидации во вью - ничего не произойдет.
     */
    private inner class DrawableCallback : Drawable.Callback {

        override fun invalidateDrawable(who: Drawable) {
            invalidate()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            val delay = `when` - SystemClock.uptimeMillis()
            postDelayed(what, delay)
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            removeCallbacks(what)
        }

    }
}