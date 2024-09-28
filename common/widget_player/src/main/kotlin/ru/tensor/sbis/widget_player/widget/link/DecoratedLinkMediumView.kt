package ru.tensor.sbis.widget_player.widget.link

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkData
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkDataSubscriber
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkOpener
import ru.tensor.sbis.richtext.converter.cfg.style.DecoratedLinkStyle
import ru.tensor.sbis.richtext.util.StaticLayoutDelegate
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

/**
 * @author am.boldinov
 */
@SuppressLint("ViewConstructor")
internal class DecoratedLinkMediumView(
    context: Context,
    linkOpener: DecoratedLinkOpener,
    linkDataSubscriber: DecoratedLinkDataSubscriber,
    linkStyle: DecoratedLinkStyle.Medium
) : BaseDecoratedLinkView(context, linkOpener, linkDataSubscriber) {

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

    private var maxTextSizePx = 0

    override fun onMeasureLink(linkData: DecoratedLinkData, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val imageSize = linkData.withImage {
            it.imageWidth + imageMarginRight
        } ?: 0
        val width = min(maxWidth, getSpanWidth(padding * 2 + imageSize + maxTextSizePx))
        val height = linkData.imageHeight + padding * 2 + verticalMargin * 2
        val measuredWidth = if (mode != MeasureSpec.UNSPECIFIED) {
            min(availableWidth, width)
        } else {
            width
        }
        setMeasuredDimension(measuredWidth, height)
    }

    override fun onDrawLink(linkData: DecoratedLinkData, canvas: Canvas) {
        val maxWidth = min(getSpanWidth(width), width)
        var left = x
        var right = maxWidth
        var top = verticalMargin
        var bottom = height - verticalMargin

        // background
        backgroundDrawable.setBounds(left.toInt(), top, right, bottom)
        backgroundDrawable.draw(canvas)

        top += padding
        bottom -= padding
        left += padding
        right -= padding

        // image
        drawable?.let {
            canvas.translate(left, top.toFloat())
            it.draw(canvas)
            canvas.translate(-left, -top.toFloat())
            left += (it.bounds.width() + imageMarginRight).toFloat()
        }

        val availableWidth = (right - left).toInt()
        if (availableWidth <= 0) { // никогда не должно произойти, защита на всякий случай
            Timber.e("Does not fit the text into the DecoratedLinkSpan")
            canvas.restore()
            return
        }
        val lineHeight = (bottom + top) / 2 - top

        // region FIRST LINE
        val additionalLayout = linkData.takeAdditionalData { layout, additionalInfo ->
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
        linkData.takeSubtitleData { layout, subtitle ->
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
        // endregion
    }

    override fun onLinkDataUpdated() {
        super.onLinkDataUpdated()
        maxTextSizePx = detectMaxTextSize().toInt()
    }

    private fun getSpanWidth(calculated: Int): Int {
        return max(min(calculated, maxWidth), minWidth)
    }

    private fun detectMaxTextSize(): Float {
        val titleWidth = linkData?.let {
            titleLayout.measureText(it.title)
        } ?: 0f
        val additionalWidth = linkData.takeAdditionalData { layout, additionalInfo ->
            layout.measureText(additionalInfo) + titleMarginHorizontal
        } ?: 0f
        val subtitleWidth = linkData.takeSubtitleData { layout, subtitle ->
            layout.measureText(subtitle)
        } ?: 0f
        return max(titleWidth + additionalWidth, subtitleWidth)
    }

    private inline fun <R> DecoratedLinkData?.takeAdditionalData(block: (layout: StaticLayoutDelegate, additionalInfo: String) -> R): R? {
        return this?.takeIf {
            it.urlType.isInternal
        }?.additionalInfo?.let {
            block.invoke(additionalInfoLayout, it)
        }
    }

    private inline fun <R> DecoratedLinkData?.takeSubtitleData(block: (layout: StaticLayoutDelegate, subtitle: String) -> R): R? {
        return this?.takeIf {
            it.urlType.isInternal
        }?.subtitle?.let {
            block.invoke(subtitleLayout, it)
        } ?: run {
            (this?.details ?: this?.subtitle)?.let {
                block.invoke(detailsLayout, it)
            }
        }
    }
}