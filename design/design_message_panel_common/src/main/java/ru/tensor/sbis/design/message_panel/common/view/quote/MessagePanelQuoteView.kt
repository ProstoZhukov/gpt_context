package ru.tensor.sbis.design.message_panel.common.view.quote

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.StyleKey
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.common.R
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import ru.tensor.sbis.design.message_panel.decl.quote.QuoteView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimenPx
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.message_panel.common.R as RMPCommon

/**
 * Реализация панели цитирования/редактирования сообщений.
 *
 * @author vv.chekurda
 */
class MessagePanelQuoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = RMPCommon.attr.MessagePanel_quoteViewStyle,
    @StyleRes defStyleRes: Int = R.style.QuiteViewDefaultStyle
) : QuoteView(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build()) {

    private val titleLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.MessagePanelQuoteView_titleStyle, styleRes = R.style.QuoteText_Title)
    ).apply { id = R.id.design_message_panel_common_quote_title }

    private val subtitleLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.MessagePanelQuoteView_subtitleStyle, styleRes = R.style.QuoteText_Subtitle)
    ).apply { id = R.id.design_message_panel_common_quote_subtitle }

    private val closeButtonLayout = TextLayout.createTextLayoutByStyle(
        this.context,
        StyleKey(styleAttr = R.attr.MessagePanelQuoteView_closeButtonStyle, styleRes = R.style.QuoteCloseButton)
    ).apply { id = R.id.design_message_panel_common_quote_close_button }

    private val layouts = listOf(titleLayout, subtitleLayout, closeButtonLayout)
    private val closeButtonRect = Rect()

    private val dividerWidth = context.getDimenPx(RDesign.attr.borderThickness_s)
    private val dividerLeftSpace = context.getDimenPx(RDesign.attr.offset_s)
    private val dividerVerticalSpace = context.getDimenPx(RDesign.attr.offset_2xs)

    private val dividerPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = dividerWidth.toFloat()
    }

    private val touchManager = TextLayoutTouchManager(this, closeButtonLayout)

    override var data: MessagePanelQuote? = null
        set(value) {
            val isChanged = field != value
            field = value
            titleLayout.configure { text = value?.title ?: EMPTY }
            subtitleLayout.configure {
                text = value?.text ?: EMPTY
                val isAudioMessageData = value?.text == context.getString(R.string.design_message_panel_audio_message_text)
                val isVideoMessageData = value?.text == context.getString(R.string.design_message_panel_video_message_text)
                paint.color = context.getThemeColorInt(RDesign.attr.textColor)
                paint.alpha = if (isAudioMessageData || isVideoMessageData) {
                    MEDIA_SUBTITLE_COLOR_ALPHA.toInt()
                } else {
                    PAINT_MAX_ALPHA
                }
            }
            if (isChanged) safeRequestLayout()
        }

    init {
        val attributeList = intArrayOf(android.R.attr.id, R.attr.MessagePanelQuoteView_dividerColor)
        this.context.withStyledAttributes(attrs, attributeList) {
            id = getResourceId(attributeList.indexOf(android.R.attr.id), NO_ID)
            dividerPaint.color = getColor(attributeList.indexOf(R.attr.MessagePanelQuoteView_dividerColor), Color.TRANSPARENT)
                .takeUnless { it == Color.TRANSPARENT }
                ?: context.getColorFromAttr(RDesign.attr.secondaryIconColor)
        }
        minimumHeight = context.getDimenPx(RDesign.attr.inlineHeight_s)
        accessibilityDelegate = TextLayoutAutoTestsHelper(this, titleLayout, subtitleLayout, closeButtonLayout)
        if (isInEditMode) showPreview()
    }

    override fun setCloseListener(listener: (() -> Unit)?) {
        isClickable = true
        closeButtonLayout.setOnClickListener(
            listener?.let { TextLayout.OnClickListener { _, _ -> listener() } }
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )

        val availableWidth = measuredWidth - paddingEnd - paddingEnd
        val titleWidth = availableWidth - dividerLeftSpace - dividerWidth - closeButtonLayout.width
        titleLayout.configure { layoutWidth = titleWidth }
        subtitleLayout.configure { layoutWidth = titleWidth }
    }

    override fun getSuggestedMinimumWidth(): Int {
        val paddings = paddingStart + paddingEnd
        val dividerSumWidth = dividerLeftSpace + dividerWidth
        val contentWidth = maxOf(
            titleLayout.getDesiredWidth(titleLayout.text),
            subtitleLayout.getDesiredWidth(subtitleLayout.text)
        )
        val wrappedWidth = paddings + dividerSumWidth + contentWidth + closeButtonLayout.width
        return maxOf(super.getSuggestedMinimumWidth(), wrappedWidth)
    }

    override fun getSuggestedMinimumHeight(): Int {
        val paddings = paddingTop + paddingBottom
        val contentHeight = titleLayout.getDesiredHeight() + subtitleLayout.getDesiredHeight()
        val wrappedHeight = paddings + contentHeight
        return maxOf(super.getSuggestedMinimumHeight(), wrappedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val titleLeft = paddingStart + dividerLeftSpace + dividerWidth
        val availableHeight = measuredHeight - paddingTop - paddingBottom
        val verticalTextSpacing = (availableHeight - titleLayout.height - subtitleLayout.height) / 3
        titleLayout.layout(titleLeft, verticalTextSpacing)
        subtitleLayout.layout(titleLeft, titleLayout.bottom + verticalTextSpacing)
        closeButtonLayout.layout(
            titleLayout.right,
            (paddingTop + (availableHeight - closeButtonLayout.height) / 2f).roundToInt()
        )
        updateCloseButtonClickableArea()
    }

    private fun updateCloseButtonClickableArea() {
        closeButtonRect.set(
            closeButtonLayout.left,
            paddingTop,
            closeButtonLayout.right,
            measuredHeight - paddingBottom
        )
        closeButtonLayout.setStaticTouchRect(closeButtonRect)
    }

    override fun onDraw(canvas: Canvas) {
        drawVerticalDivider(canvas)
        layouts.forEach { it.draw(canvas) }
    }

    private fun drawVerticalDivider(canvas: Canvas) {
        canvas.drawLine(
            (paddingStart + dividerLeftSpace).toFloat(),
            (paddingTop + dividerVerticalSpace).toFloat(),
            (paddingStart + dividerLeftSpace).toFloat(),
            (height - paddingBottom - dividerVerticalSpace).toFloat(),
            dividerPaint
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        touchManager.onTouch(this, event) || super.onTouchEvent(event)

    private fun showPreview() {
        data = MessagePanelQuote(
            title = "Голубец Е.",
            text = "Позвони Марине, узнай как дела с навигационной панелью"
        )
    }
}

private const val MEDIA_SUBTITLE_COLOR_ALPHA = PAINT_MAX_ALPHA * 0.6