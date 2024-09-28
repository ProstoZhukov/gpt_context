package ru.tensor.sbis.richtext.span.decoratedlink

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.text.Spanned
import android.text.TextPaint
import android.text.style.LineHeightSpan
import android.view.View
import com.facebook.common.lifecycle.AttachDetachListener
import ru.tensor.sbis.design.text_span.span.DebounceClickableSpan
import ru.tensor.sbis.richtext.converter.cfg.style.DecoratedLinkStyle
import ru.tensor.sbis.richtext.span.LineReplacementSpan
import ru.tensor.sbis.richtext.span.LongClickSpan
import ru.tensor.sbis.richtext.util.RichTextAndroidUtil
import ru.tensor.sbis.richtext.util.SpannableUtil
import ru.tensor.sbis.richtext.util.StaticLayoutDelegate
import ru.tensor.sbis.richtext.view.RichTextView
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

/**
 * Span декорированной ссылки
 * https://www.figma.com/file/NYw7EXwWbsT7bYFboaWobm/%D0%94%D0%B5%D0%BA%D0%BE%D1%80%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D1%8B%D0%B5-%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B8?node-id=659%3A358
 * https://online.sbis.ru/shared/disk/65f1f663-3946-4fd7-9c8d-1f552a521408
 *
 * @param context контекст приложения
 * @param linkData данный для рендера
 * @param linkDataSubscriber интерфейс для установки колбэка обновления данных
 * @param linkStyle набор стилей для отрисовки ссылки
 * @param linkOpener интерфейс для открытия ссылки
 *
 * @property clickableUrlSpan [android.text.style.ClickableSpan] для установки в текст на те же позиции, что и текущий Span
 *
 * @author am.boldinov
 */
internal class DecoratedLinkSpan(
    private val context: Context,
    private var linkData: DecoratedLinkData,
    private val linkDataSubscriber: DecoratedLinkDataSubscriber,
    private val linkStyle: DecoratedLinkStyle.Medium,
    linkOpener: DecoratedLinkOpener
) : LineReplacementSpan(), AttachDetachListener, LongClickSpan, LineHeightSpan {

    val clickableUrlSpan = object : DebounceClickableSpan() {
        override fun onDebounceClick(widget: View) {
            linkOpener.open(widget.context, linkData.linkPreview, linkData.title)
        }
    }

    private val dataRefreshCallback = DecoratedLinkDataSubscriber.DataRefreshCallback { data ->
        if (linkData != data) {
            linkData = data
            invalidateLinkDataPaint()
            if (boundView != null) {
                attachDraweeHolder()
                dispatchViewInvalidateSpan()
            }
        }
    }
    private val backgroundDrawable = GradientDrawable().apply {
        setColor(linkStyle.backgroundColor)
        cornerRadius = linkStyle.backgroundCornerRadius
        setStroke(linkStyle.backgroundStrokeWidth, linkStyle.backgroundStrokeColor)
    }
    private val titleLayout = StaticLayoutDelegate(TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = linkStyle.titleSize
        typeface = linkStyle.titleTypeface
        color = linkStyle.titleColor
    })
    private val subtitleLayout = StaticLayoutDelegate(TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = linkStyle.subtitleSize
        typeface = linkStyle.subtitleTypeface
        color = linkStyle.subtitleColor
    })
    private val detailsLayout = StaticLayoutDelegate(TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = linkStyle.detailsSize
        typeface = linkStyle.detailsTypeface
        color = linkStyle.detailsColor
    })
    private val additionalInfoLayout = StaticLayoutDelegate(TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = linkStyle.additionalSize
        typeface = linkStyle.additionalTypeface
        color = linkStyle.additionalColor
    })
    private val padding = linkStyle.padding
    private val verticalMargin = linkStyle.verticalMargin
    private val imageMarginRight = linkStyle.imageMarginRight
    private val titleMarginHorizontal = linkStyle.titleMarginHorizontal
    private val minWidth = linkStyle.minWidth
    private val maxWidth = linkStyle.maxWidth

    // drawable внутри оборачивает в weak reference - храним callback
    private val drawableCallback = DrawableCallback()
    private var drawable: Drawable? = null
    private var maxTextSizePx = 0
    private var boundView: View? = null

    init {
        maxLineWidth = maxWidth
        invalidateLinkDataPaint()
        linkDataSubscriber.attachDataRefreshCallback(linkData.sourceUrl, dataRefreshCallback, false)
    }

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt?
    ) {
        val spanStart = (text as Spanned).getSpanStart(this)
        if (spanStart == start) {
            SpannableUtil.setSpanHeight(linkData.imageHeight, padding + verticalMargin, fm)
        } else if (start > spanStart) {
            SpannableUtil.invalidateSpanHeight(fm)
        }
    }

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val imageSize = if (linkData.hasImage()) {
            linkData.imageWidth + imageMarginRight
        } else {
            0
        }
        return getSpanWidth(SpannableUtil.LEADING_MARGIN_OFFSET_X + padding * 2 + imageSize + maxTextSizePx)
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        topExt: Int,
        y: Int,
        bottomExt: Int,
        paint: Paint
    ) {
        // начиная с Android Pie есть возможность рисовать Multiline ReplacementSpan
        // соответственно не рисуем на пустых строках, которые были обнулены в chooseHeight
        val spanStart = (text as Spanned).getSpanStart(this)
        if (bottomExt - topExt == 0 || spanStart != start) {
            return
        }
        canvas.save()
        val maxWidth = min(getSpanWidth(canvas.width), canvas.width)
        var left = x + SpannableUtil.LEADING_MARGIN_OFFSET_X
        var right = maxWidth
        var top = topExt + verticalMargin
        var bottom = bottomExt - verticalMargin

        // background
        backgroundDrawable.setBounds(left.toInt(), top, right, bottom)
        backgroundDrawable.draw(canvas)

        top += padding
        bottom -= padding
        left += padding
        right -= padding

        // image
        if (linkData.hasImage()) {
            drawable?.let {
                canvas.translate(left, top.toFloat())
                it.draw(canvas)
                canvas.translate(-left, -top.toFloat())
                left += (linkData.imageWidth + imageMarginRight).toFloat()
            }
        }

        val availableWidth = (right - left).toInt()
        if (availableWidth <= 0) { // никогда не должно произойти, защита на всякий случай
            Timber.e("Does not fit the text into the DecoratedLinkSpan")
            canvas.restore()
            return
        }
        val lineHeight = (bottom + top) / 2 - top

        // region FIRST LINE
        val additionalLayout = takeAdditionalData { layout, additionalInfo ->
            val maxAdditionalWidth = min(availableWidth * 0.5f, layout.getMeasuredWidth()).toInt()
            layout.measure(additionalInfo, maxAdditionalWidth)
            layout
        }

        // title
        val titleMaxWidth = availableWidth - (additionalLayout?.let {
            it.getRenderedWidth() + titleMarginHorizontal
        } ?: 0f).toInt()
        val titleLayout = this.titleLayout.measure(linkData.title, titleMaxWidth)
        val titleHeight = titleLayout.height
        val titleTopOffset = if (lineHeight > titleHeight) {
            (lineHeight - titleHeight) / 2
        } else 0
        val titleDy = top + titleTopOffset
        canvas.translate(left, titleDy.toFloat())
        titleLayout.draw(canvas)

        // additional info
        additionalLayout?.let { layout ->
            layout.get()?.let { static ->
                val dx = titleMaxWidth + titleMarginHorizontal
                val height = static.height
                val dy = if (titleHeight > height) {
                    titleHeight - height - 2
                } else 0
                canvas.save()
                canvas.translate(dx.toFloat(), dy.toFloat())
                static.draw(canvas)
                canvas.restore()
            }
        }
        //endregion

        // region SECOND LINE
        // subtitle
        takeSubtitleData { layout, subtitle ->
            val subtitleLayout = layout.measure(subtitle, availableWidth)
            val subtitleHeight = subtitleLayout.height
            var subtitleTopOffset = 0
            if (lineHeight > subtitleHeight) {
                // прижимаем subtitle к нижней части картинки с отступом как у title
                subtitleTopOffset = lineHeight - subtitleHeight - titleTopOffset
            }
            val subtitleDy = titleHeight + titleTopOffset + subtitleTopOffset
            canvas.translate(0f, subtitleDy.toFloat())
            subtitleLayout.draw(canvas)
        }
        canvas.restore()
        // endregion
    }

    /**
     * Событие о том, что Span присоединился ко View
     */
    override fun onAttachToView(view: View?) {
        if (boundView != null && boundView !== view) {
            onDetachFromView(boundView)
        }
        boundView = view
        linkDataSubscriber.attachDataRefreshCallback(linkData.sourceUrl, dataRefreshCallback, true)
        attachDraweeHolder()
    }

    /**
     * Событие о том, что Span отсоединился от View
     */
    override fun onDetachFromView(view: View?) {
        if (view !== boundView) {
            return
        }
        linkDataSubscriber.detachDataRefreshCallback(linkData.sourceUrl, dataRefreshCallback)
        boundView = null
        detachDraweeHolder()
    }

    override fun onLongClick(widget: View) {
        RichTextAndroidUtil.copyToClipboard(widget.context, linkData.sourceUrl)
    }

    override fun setMaxLineWidth(maxSize: Int) {
        super.setMaxLineWidth(min(maxWidth, maxSize))
    }

    private fun getSpanWidth(calculated: Int): Int {
        return min(maxLineWidth, max(calculated, minWidth))
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

    private fun invalidateLinkDataPaint() {
        drawable = if (linkData.hasImage()) {
            linkData.draweeHolder.topLevelDrawable?.apply {
                if (bounds.isEmpty) {
                    setBounds(0, 0, linkData.imageWidth, linkData.imageHeight)
                }
            }
        } else {
            null
        }
        maxTextSizePx = detectMaxTextSize().toInt()
    }

    private fun detectMaxTextSize(): Float {
        val titleWidth = titleLayout.measureText(linkData.title)
        val additionalWidth = takeAdditionalData { layout, additionalInfo ->
            layout.measureText(additionalInfo) + titleMarginHorizontal
        } ?: 0f
        val subtitleWidth = takeSubtitleData { layout, subtitle ->
            layout.measureText(subtitle)
        } ?: 0f
        return max(titleWidth + additionalWidth, subtitleWidth)
    }

    private inline fun <R> takeAdditionalData(block: (layout: StaticLayoutDelegate, additionalInfo: String) -> R): R? {
        return if (linkData.urlType.isInternal) {
            linkData.additionalInfo?.let {
                block.invoke(additionalInfoLayout, it)
            }
        } else null
    }

    private inline fun <R> takeSubtitleData(block: (layout: StaticLayoutDelegate, subtitle: String) -> R): R? {
        return if (linkData.urlType.isInternal) {
            linkData.subtitle?.let {
                block.invoke(subtitleLayout, it)
            }
        } else {
            (linkData.details ?: linkData.subtitle)?.let {
                block.invoke(detailsLayout, it)
            }
        }
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