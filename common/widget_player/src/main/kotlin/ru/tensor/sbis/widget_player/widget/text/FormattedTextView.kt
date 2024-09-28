package ru.tensor.sbis.widget_player.widget.text

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.*
import android.util.TypedValue
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isGone
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.richtext.span.background.LayoutBackgroundSpan
import ru.tensor.sbis.widget_player.layout.MultiLineView
import ru.tensor.sbis.widget_player.layout.TextHolderView
import ru.tensor.sbis.widget_player.widget.text.touch.ClickableSpanTouchHandler
import ru.tensor.sbis.widget_player.widget.text.touch.LongClickSpanTouchHandler

/**
 * @author am.boldinov
 */
internal class FormattedTextView(context: Context) : View(context), MultiLineView, MultiLineView.AfterMeasureUpdater,
    MultiLineView.BeforeMeasureUpdater, TextHolderView {

    private val gestureDetector = GestureDetectorCompat(getContext(), object : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            if (linksClickable) {
                longClickHandled = LongClickSpanTouchHandler.handleTouch(this@FormattedTextView, layout, e)
            }
        }
    }, Handler(Looper.getMainLooper()))

    private val textLayout = TextLayout {
        ellipsize = null
        maxLines = Int.MAX_VALUE
        includeFontPad = false
    }.apply {
        layoutFactory = FormattedTextLayoutFactory
    }
    private val layout get() = textLayout.layout as FormattedLayout

    private val spannable get() = textLayout.text as? Spannable

    private var longClickHandled = false

    var linksClickable = true

    override val text get() = textLayout.text

    override val beforeMeasureUpdater = this

    override val afterMeasureUpdater = this

    fun configure(config: TextLayoutConfig) {
        textLayout.configure(config).also { changed ->
            if (!isGone && changed) {
                safeRequestLayout()
            }
        }
    }

    fun setTextSizePx(size: Float) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun setTextSize(unit: Int, size: Float) {
        val textSize = TypedValue.applyDimension(unit, size, resources.displayMetrics)
        configure {
            paint.textSize = textSize
        }
    }

    fun setTypeface(typeface: Typeface?) {
        configure {
            paint.typeface = typeface
        }
    }

    fun setLinkTextColor(@ColorInt color: Int) {
        textLayout.textPaint.apply {
            if (linkColor != color) {
                linkColor = color
                invalidate()
            }
        }
    }

    fun setTextColor(@ColorInt color: Int) {
        textLayout.textPaint.apply {
            if (this.color != color) {
                this.color = color
                invalidate()
            }
        }
    }

    fun setStrikeText(strike: Boolean) {
        textLayout.textPaint.apply {
            if (this.isStrikeThruText != strike) {
                this.isStrikeThruText = strike
                invalidate()
            }
        }
    }

    fun setUnderlineText(underline: Boolean) {
        textLayout.textPaint.apply {
            if (this.isUnderlineText != underline) {
                this.isUnderlineText = underline
                invalidate()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = gestureDetector.onTouchEvent(event) || longClickHandled
        if (event.action == MotionEvent.ACTION_UP) {
            longClickHandled = false
        }
        return handled || linksClickable && ClickableSpanTouchHandler.handleTouch(
            this,
            layout,
            event
        ) || super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val horizontalPadding = paddingStart + paddingEnd
        val verticalPadding = paddingTop + paddingBottom
        val width = MeasureSpecUtils.measureDirection(widthMeasureSpec) { availableWidth ->
            val availableTextWidth = availableWidth?.minus(horizontalPadding)
            val textWidth = textLayout.getPrecomputedWidth(availableTextWidth)
            maxOf(horizontalPadding + textWidth, suggestedMinimumWidth)
        }
        textLayout.buildLayout(width - horizontalPadding)
        val height = MeasureSpecUtils.measureDirection(heightMeasureSpec) {
            maxOf(verticalPadding + textLayout.height, suggestedMinimumHeight)
        }
        setMeasuredDimension(width, height)
        layout.clearTopOffsets()
    }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        layout.draw(canvas)
    }

    override fun getLineCount(): Int {
        return textLayout.lineCount
    }

    override fun getLineWidth(line: Int): Int {
        return layout.getLineWidth(line).toInt()
    }

    override fun getLineHeight(line: Int): Int {
        return layout.getLineHeight(line)
    }

    override fun getLineTop(line: Int): Int {
        return layout.getLineTop(line)
    }

    override fun getLineBottom(line: Int): Int {
        return layout.getLineBottom(line)
    }

    override fun getLeftLineIndent(lineNumber: Int): Int {
        return textLayout.leftIndents?.getOrElse(lineNumber) {
            0
        } ?: 0
    }

    override fun updateLeftLineIndent(lineNumber: Int, indent: Int): Boolean {
        val current = getLeftLineIndent(lineNumber)
        return if (current != indent) {
            configure {
                indents = (indents ?: TextLayout.TextLineIndents()).apply {
                    left = left.setIndent(lineNumber, indent)
                }
            }
            true
        } else {
            false
        }
    }

    override fun updateLineHeight(lineNumber: Int, height: Int): Boolean {
        val offset = height - getLineHeight(lineNumber)
        return if (offset > 0) {
            layout.updateLineTopOffset(lineNumber, offset)
            setMeasuredDimension(measuredWidth, measuredHeight + offset)
            true
        } else {
            false
        }
    }

    override fun getBaseline(): Int {
        val layoutBaseLine = textLayout.safeLayoutBaseLine
        return if (layoutBaseLine != -1) {
            paddingTop + layoutBaseLine
        } else {
            layoutBaseLine
        }
    }

    private fun drawBackground(canvas: Canvas) {
        spannable?.apply {
            getSpans(0, length, LayoutBackgroundSpan::class.java).forEach { span ->
                val start = getSpanStart(span)
                val end = getSpanEnd(span)
                span.draw(canvas, layout, start, end)
            }
        }
    }

    private fun IntArray?.setIndent(lineNumber: Int, indent: Int): IntArray {
        val current = this
        if (current != null && lineNumber < current.size) {
            current[lineNumber] = indent
            return current
        } else {
            return IntArray(lineNumber + 2) {
                when (it) {
                    lineNumber     -> {
                        indent
                    }
                    lineNumber + 1 -> { // force last element 0
                        0
                    }
                    else           -> {
                        current?.let { arr ->
                            arr[it]
                        } ?: 0
                    }
                }
            }
        }
    }
}