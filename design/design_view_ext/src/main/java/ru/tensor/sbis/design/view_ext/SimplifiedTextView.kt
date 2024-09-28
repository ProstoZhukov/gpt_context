package ru.tensor.sbis.design.view_ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout
import android.text.Layout.Alignment.ALIGN_CENTER
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.Layout.Alignment.ALIGN_OPPOSITE
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParamsProvider
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import kotlin.math.roundToInt

/**
 * Упрощённая реализация [TextView], использующая [TextLayout].
 *
 * @author us.bessonov
 */
@Suppress("LeakingThis")
open class SimplifiedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = ResourcesCompat.ID_NULL,
    @StyleRes defStyleRes: Int = ResourcesCompat.ID_NULL,
    styleProvider: StyleParamsProvider<StyleParams.TextStyle>? = null
) : View(context) {

    private val textLayout =
        TextLayout.createTextLayoutByStyle(context, getTextStyle(defStyleAttr, defStyleRes), styleProvider)
            .apply { makeClickable(this@SimplifiedTextView) }

    /**
     * @see [TextView.setGravity]
     */
    var gravity: Int = Gravity.NO_GRAVITY
        set(value) {
            if (field != value) {
                field = value
                updateGravity()
                internalLayout()
                invalidate()
            }
        }

    /**
     * @see [TextView.setText]
     */
    var text: CharSequence?
        get() = textLayout.text
        set(value) {
            if (text != value) {
                updateConfig(value)
                safeRequestLayout()
            }
        }

    /**
     * @see [TextView.setMaxLines]
     */
    var maxLines: Int
        get() = textLayout.maxLines
        set(value) {
            if (maxLines != value) {
                updateConfig(newMaxLines = value)
                safeRequestLayout()
            }
        }

    /** @SelfDocumented */
    var textColors: ColorStateList = ColorStateList.valueOf(Color.WHITE)
        private set

    /**
     * @see [Layout.getEllipsizedWidth]
     */
    val ellipsizedWidth: Int
        get() = textLayout.ellipsizedWidth

    /**
     * @see [TextView.getPaint]
     */
    val paint: TextPaint
        get() = textLayout.textPaint

    init {
        setWillNotDraw(false)
        context.withStyledAttributes(attrs, intArrayOf(android.R.attr.gravity), defStyleAttr, defStyleRes) {
            gravity = getInt(0, Gravity.NO_GRAVITY)
        }
    }

    /**
     * @see [TextView.setTextSize]
     */
    fun setTextSize(unit: Int, size: Float) {
        val newTextSize = TypedValue.applyDimension(unit, size, resources.displayMetrics)
        if (newTextSize != textLayout.textPaint.textSize) {
            updateConfig(newTextSize = newTextSize)
            safeRequestLayout()
        }
    }

    /**
     * @see [TextView.setTextColor]
     */
    fun setTextColor(@ColorInt color: Int) {
        textColors = ColorStateList.valueOf(color)
        textLayout.colorStateList = null
        textLayout.textPaint.color = color
        invalidate()
    }

    /**
     * @see [TextView.setTextColor]
     */
    fun setTextColor(colors: ColorStateList) {
        textColors = colors
        textLayout.colorStateList = colors
        invalidate()
    }

    /**
     * @see [TextView.setText]
     */
    fun setText(@StringRes resource: Int) {
        text = resources.getString(resource)
    }

    /**
     * @see [TextView.setTextAppearance]
     */
    fun setTextAppearance(context: Context, @StyleRes style: Int) {
        val styleParams = SimplifiedTextViewCanvasStylesProvider.textStyleProvider.getStyleParams(context, style)
        var shouldInvalidate = false
        var shouldLayout = false
        textLayout.configure {
            paint.apply {
                styleParams.textColor?.let {
                    if (color != it) {
                        color = it
                        shouldInvalidate = true
                    }
                }
                styleParams.textSize?.let {
                    if (textSize != it) {
                        textSize = it
                        shouldLayout = true
                    }
                }
                styleParams.typeface?.let {
                    if (typeface != it) {
                        typeface = it
                        shouldLayout = true
                    }
                }
                styleParams.includeFontPad?.let {
                    if (includeFontPad != it) {
                        includeFontPad = it
                        shouldLayout = true
                    }
                }
            }
        }
        if (shouldLayout) {
            safeRequestLayout()
        } else if (shouldInvalidate) {
            invalidate()
        }
    }

    /** @SelfDocumented */
    fun setFontStyle(style: Int) {
        textLayout.configure {
            paint.typeface = Typeface.create(paint.typeface, style)
        }
        safeRequestLayout()
    }

    /** @SelfDocumented */
    fun setEllipsize(ellipsize: TextUtils.TruncateAt?) {
        textLayout.configure {
            this.ellipsize = ellipsize
        }
        safeRequestLayout()
    }

    /**
     * @see [Layout.getEllipsisCount]
     */
    fun getEllipsisCount(line: Int) = textLayout.getEllipsisCount(line)

    override fun isEnabled() = textLayout.isEnabled

    override fun setEnabled(enabled: Boolean) {
        textLayout.isEnabled = enabled
    }

    override fun setSelected(selected: Boolean) {
        textLayout.isSelected = true
    }

    override fun isSelected() = textLayout.isSelected

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (visibility == GONE) {
            setMeasuredDimension(0, 0)
            return
        }
        val width: Int
        val height: Int
        if (maxLines > 1) {
            textLayout.configure {
                layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
                val availableHeight = MeasureSpec.getSize(heightMeasureSpec)
                val maxFitLines = calculateFitLines(availableHeight)
                maxHeight = if (maxFitLines < maxLines) {
                    availableHeight
                } else {
                    calculateHeight(maxLines)
                }
            }
            width = textLayout.width
            height = textLayout.height
        } else {
            val desiredWidth = textLayout.getDesiredWidth(text)
            val desiredHeight = textLayout.getDesiredHeight()
            width = resolveSize(desiredWidth, widthMeasureSpec)
            height = resolveSize(desiredHeight, heightMeasureSpec)
            textLayout.configure { layoutWidth = width }
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        internalLayout()
    }

    override fun onDraw(canvas: Canvas) {
        textLayout.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) =
        (if (isClickable) textLayout.onTouch(this, event) else false) || super.onTouchEvent(event)

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        textLayout.setOnClickListener(
            l?.let {
                TextLayout.OnClickListener { _, _ ->
                    it.onClick(this)
                }
            }
        )
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        textLayout.updatePadding(left, top, right, bottom)
    }

    override fun getPaddingTop() = textLayout.paddingTop

    override fun getPaddingBottom() = textLayout.paddingBottom

    override fun getPaddingLeft() = textLayout.paddingStart

    override fun getPaddingStart() = textLayout.paddingStart

    override fun getPaddingRight() = textLayout.paddingEnd

    override fun getPaddingEnd() = textLayout.paddingEnd

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.text = text
    }

    private fun internalLayout() {
        val actualTop = when (gravity) {
            Gravity.BOTTOM -> measuredHeight - textLayout.height
            Gravity.CENTER, Gravity.CENTER_VERTICAL,
            Gravity.CENTER_VERTICAL or Gravity.RIGHT, Gravity.CENTER_VERTICAL or Gravity.LEFT ->
                (measuredHeight - textLayout.height) / 2
            else -> 0
        }
        textLayout.layout(0, actualTop)
    }

    private fun updateConfig(
        newText: CharSequence? = text,
        newMaxLines: Int = maxLines,
        @Px
        newTextSize: Float = textLayout.textPaint.textSize
    ) {
        textLayout.configure {
            text = newText ?: ""
            paint.textSize = newTextSize
            maxLines = newMaxLines
            padding = TextLayout.TextLayoutPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }

    private fun updateGravity() {
        textLayout.configure {
            alignment = when (gravity) {
                Gravity.CENTER, Gravity.CENTER_HORIZONTAL -> ALIGN_CENTER
                Gravity.RIGHT, Gravity.END,
                (Gravity.CENTER_VERTICAL or Gravity.RIGHT), (Gravity.CENTER_VERTICAL or Gravity.LEFT)
                -> ALIGN_OPPOSITE
                else -> ALIGN_NORMAL
            }
        }
    }

    @StyleRes
    private fun getTextStyle(@AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int): Int {
        return context.getDataFromAttrOrNull(defStyleAttr, true) ?: defStyleRes
    }

    private fun calculateFitLines(@Px availableHeight: Int): Int = when {
        availableHeight >= getFirstLineHeight() + getLastLineHeight() -> {
            2 + (availableHeight - getFirstLineHeight() - getLastLineHeight()) / getInnerLineHeight()
        }
        availableHeight >= getFirstLineHeight() -> 1
        else -> 0
    }

    private fun calculateHeight(lineCount: Int): Int {
        val fm = getFontMetrics()
        return when {
            lineCount <= 0 -> 0
            lineCount == 1 -> (fm.bottom - fm.top).roundToInt()
            else -> getFirstLineHeight() + getLastLineHeight() + (lineCount - 2) * getInnerLineHeight()
        }
    }

    private fun getFontMetrics() = textLayout.textPaint.fontMetrics

    private fun getFirstLineHeight() = getFontMetrics().run { (descent - top).roundToInt() }

    private fun getLastLineHeight() = getFontMetrics().run { (bottom - ascent).roundToInt() }

    private fun getInnerLineHeight() = getFontMetrics().run { (descent - ascent).roundToInt() }
}

private object SimplifiedTextViewCanvasStylesProvider : CanvasStylesProvider()