package ru.tensor.sbis.richtext.span.decoratedlink

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.text.TextPaint
import android.text.style.ReplacementSpan
import android.view.View
import com.facebook.common.lifecycle.AttachDetachListener
import ru.tensor.sbis.design.text_span.span.DebounceClickableSpan
import ru.tensor.sbis.richtext.converter.cfg.style.DecoratedLinkStyle
import ru.tensor.sbis.richtext.span.LongClickSpan
import ru.tensor.sbis.richtext.util.RichTextAndroidUtil
import ru.tensor.sbis.richtext.util.StaticLayoutDelegate
import ru.tensor.sbis.richtext.view.RichTextView
import timber.log.Timber
import kotlin.math.min

/**
 * Span декорированной ссылки внутри текста
 * https://www.figma.com/file/NYw7EXwWbsT7bYFboaWobm/%D0%94%D0%B5%D0%BA%D0%BE%D1%80%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D1%8B%D0%B5-%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B8?node-id=659%3A358
 * https://online.sbis.ru/shared/disk/65f1f663-3946-4fd7-9c8d-1f552a521408
 *
 * @param context контекст приложения
 * @param linkData данный для рендера
 * @param linkStyle набор стилей для отрисовки ссылки
 * @param linkOpener интерфейс для открытия ссылки
 *
 * TODO вынести общий код с DecoratedLinkSpan
 * @author am.boldinov
 */
internal class InlineDecoratedLinkSpan(
    private val context: Context,
    private val linkData: DecoratedLinkData,
    private val linkStyle: DecoratedLinkStyle.Small,
    linkOpener: DecoratedLinkOpener
) : ReplacementSpan(), AttachDetachListener, LongClickSpan {

    val clickableUrlSpan = object : DebounceClickableSpan() {
        override fun onDebounceClick(widget: View) {
            linkOpener.open(widget.context, linkData.linkPreview, linkData.title)
        }
    }

    private val textLayout = StaticLayoutDelegate(TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = linkStyle.textSize
        typeface = linkStyle.textTypeface
        color = linkStyle.textColor
    })
    private val imageMarginRight = linkStyle.imageMarginRight
    private val maxWidth = linkStyle.maxWidth
    private var boundView: View? = null

    // drawable внутри оборачивает в weak reference - храним callback
    private val drawableCallback = DrawableCallback()
    private val drawable = linkData.draweeHolder.topLevelDrawable?.apply {
        if (bounds.isEmpty) {
            setBounds(0, 0, linkData.imageWidth, linkData.imageHeight)
        }
    }

    init {
        measureSpanText()
    }

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (boundView != null) {
            val textSize = paint.textSize
            textLayout.setTextSize(textSize) // адаптируется под размер текста во View
            drawable?.setBounds(0, 0, textSize.toInt(), textSize.toInt())
        }
        val imageWidth = drawable?.let {
            it.bounds.width() + imageMarginRight
        } ?: 0
        return getSpanWidth(imageWidth + measureSpanText())
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        val maxWidth = getSpanWidth(canvas.width)
        var left = x
        val right = maxWidth + left

        // image
        drawable?.let {
            val imageHeight = it.bounds.height()
            val lineHeight = bottom - top
            val topOffset = if (lineHeight > imageHeight) {
                top.toFloat() + (lineHeight - imageHeight) / 2
            } else top.toFloat()
            canvas.translate(left, topOffset)
            it.draw(canvas)
            canvas.translate(-left, -topOffset)
            left += (it.bounds.width() + imageMarginRight).toFloat()
        }

        // text
        val availableWidth = (right - left).toInt()
        if (availableWidth <= 0) {
            Timber.e("Does not fit the text into the InlineDecoratedLinkSpan")
            canvas.restore()
            return
        }
        canvas.translate(left, top.toFloat())
        textLayout.measure(linkData.title, availableWidth).draw(canvas)

        canvas.restore()
    }

    override fun onLongClick(widget: View) {
        RichTextAndroidUtil.copyToClipboard(widget.context, linkData.sourceUrl)
    }

    /**
     * Событие о том, что Span присоединился ко View
     */
    override fun onAttachToView(view: View?) {
        if (boundView != null && boundView !== view) {
            onDetachFromView(boundView)
        }
        boundView = view
        attachDraweeHolder()
    }

    /**
     * Событие о том, что Span отсоединился от View
     */
    override fun onDetachFromView(view: View?) {
        if (view !== boundView) {
            return
        }
        boundView = null
        detachDraweeHolder()
    }

    private fun attachDraweeHolder() {
        if (linkData.hasImage() && !linkData.draweeHolder.isAttached) {
            drawable?.callback = drawableCallback
            linkData.draweeHolder.onAttach()
        }
    }

    private fun detachDraweeHolder() {
        if (linkData.hasImage()) {
            drawable?.callback = null
            linkData.draweeHolder.onDetach()
        }
    }

    private fun dispatchViewInvalidateSpan() {
        boundView?.let { view ->
            if (view is RichTextView) {
                view.postInvalidateSpan(this)
            } else {
                Timber.e("Need to use RichTextView for correct rendering DecoratedLinkSpan")
                view.invalidate()
            }
        }
    }

    private fun getSpanWidth(calculated: Int): Int {
        return min(maxWidth, calculated)
    }

    private fun measureSpanText(): Int {
        return textLayout.measureText(linkData.title).toInt()
    }

    /**
     * Кастомная реализация callback.
     * View не знает о существовании изменяемого Drawable (verifyDrawable вернет false) и его границ, поэтому если перенаправить вызов инвалидации во вью - ничего не произойдет.
     */
    private inner class DrawableCallback : Drawable.Callback {

        override fun invalidateDrawable(who: Drawable) {
            dispatchViewInvalidateSpan()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            boundView?.let {
                val delay = `when` - SystemClock.uptimeMillis()
                it.postDelayed(what, delay)
            }
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            boundView?.removeCallbacks(what)
        }

    }
}