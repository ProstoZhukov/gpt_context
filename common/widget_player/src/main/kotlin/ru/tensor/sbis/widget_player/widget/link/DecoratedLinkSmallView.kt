package ru.tensor.sbis.widget_player.widget.link

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.TextPaint
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkData
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener
import ru.tensor.sbis.richtext.converter.cfg.style.DecoratedLinkStyle
import ru.tensor.sbis.richtext.util.StaticLayoutDelegate
import timber.log.Timber
import kotlin.math.min

/**
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
internal class DecoratedLinkSmallView(
    context: Context,
    linkOpener: DecoratedLinkOpener,
    linkStyle: DecoratedLinkStyle.Small
) : BaseDecoratedLinkView(context, linkOpener, null) {

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = linkStyle.textSize
        typeface = linkStyle.textTypeface
        color = linkStyle.textColor
    }
    private val textLayout = StaticLayoutDelegate(paint)
    private val fontMetrics = FontMetricsInt()

    private val imageMarginRight = linkStyle.imageMarginRight
    private val maxWidth = linkStyle.maxWidth

    private var maxTextSizePx = 0

    fun setTextSize(textSize: Float) {
        textLayout.setTextSize(textSize)
        if (textLayout.get() == null) {
            requestLayout()
        }
    }

    override fun onMeasureLink(linkData: DecoratedLinkData, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        paint.getFontMetricsInt(fontMetrics)
        val height = fontMetrics.descent - fontMetrics.ascent
        val imageHeight = paint.textSize.toInt().let {
            it + (maxOf(height - it, 0)) / 2 // среднее значение между размером текста и высотой строки
        }
        drawable?.setBounds(0, 0, imageHeight, imageHeight)
        val imageWidth = drawable?.let {
            it.bounds.width() + imageMarginRight
        } ?: 0
        val width = getSpanWidth(imageWidth + maxTextSizePx)
        setMeasuredDimension(minOf(width, maxWidth), height)
    }

    override fun onDrawLink(linkData: DecoratedLinkData, canvas: Canvas) {
        val maxWidth = getSpanWidth(width)
        var left = x
        val right = maxWidth + left

        // image
        drawable?.let {
            val imageHeight = it.bounds.height()
            val topOffset = if (height > imageHeight) {
                top.toFloat() + (height - imageHeight) / 2
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
    }

    override fun onLinkDataUpdated() {
        super.onLinkDataUpdated()
        maxTextSizePx = detectMaxTextSize().toInt()
    }

    private fun detectMaxTextSize(): Float {
        return linkData?.let {
            textLayout.measureText(it.title)
        } ?: 0f
    }

    private fun getSpanWidth(calculated: Int): Int {
        return min(maxWidth, calculated)
    }
}