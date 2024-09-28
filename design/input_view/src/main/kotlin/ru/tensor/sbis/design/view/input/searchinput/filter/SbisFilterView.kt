package ru.tensor.sbis.design.view.input.searchinput.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.facebook.drawee.drawable.RoundedColorDrawable
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.view.input.R

/**
 * Представление для отображения фильтров, используется как вместе со строкой поиска так и без нее
 * стандарт http://axure.tensor.ru/MobileStandart8/#g=1&p=%D1%81%D1%82%D1%80%D0%BE%D0%BA%D0%B0_%D0%BF%D0%BE%D0%B8%D1%81%D0%BA%D0%B0
 *
 * @author ma.kolpakov
 */
class SbisFilterView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int = R.attr.filterViewTheme,
    @StyleRes defStyleRes: Int = R.style.FilterViewDefaultTheme,
    private val controller: FilterController
) : View(context, attrs, defStyleAttr, defStyleRes),
    FilterAPI by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.filterViewTheme,
        @StyleRes defStyleRes: Int = R.style.FilterViewDefaultTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, FilterController())

    private val bottomDividerRect = Rect()
    private val bottomDividerPaint = Paint()
    var dividerIsVisible: Boolean = false
    private val styleHolder = FilterViewStyleHolder()
    private var filterHeight = 0

    internal val selectedFilters: TextLayout = TextLayout {
        paint.typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    internal val filterIcon: TextLayout = TextLayout {
        paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
    }
    private val touchManager =
        TextLayoutTouchManager(this, true, selectedFilters, filterIcon)

    private val bounds: Rect = Rect(0, 0, 0, 0)

    private var roundedColorBackground = RoundedColorDrawable(0f, Color.MAGENTA)

    init {
        isClickable = true
        styleHolder.initStyle(context, attrs, defStyleAttr, defStyleRes)
        controller.attachView(this)
        filterIcon.apply {
            textPaint.textSize = styleHolder.iconHeight.toFloat()
            colorStateList = styleHolder.iconColors
            configure {
                text = styleHolder.filterIcon
            }
        }
        selectedFilters.apply {
            textPaint.textSize = styleHolder.filtersTextSize.toFloat()
            colorStateList = styleHolder.filterColors
        }
        bottomDividerPaint.color = styleHolder.dividerColor
        background = roundedColorBackground
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = measureFilterWidth().coerceAtMost(MeasureSpec.getSize(widthMeasureSpec))
        selectedFilters.configure {
            maxWidth =
                size - filterIcon.width - styleHolder.iconOffsetRight - styleHolder.filterOffsetLeft
        }
        setMeasuredDimension(
            MeasureSpecUtils.makeAtMostSpec(size),
            MeasureSpecUtils.makeExactlySpec(filterHeight)
        )
    }

    private fun measureFilterWidth(): Int {
        var width = filterIcon.width + styleHolder.iconOffsetRight
        width += if (hasFilters()) {
            selectedFilters.getDesiredWidth() + styleHolder.filterOffsetLeft
        } else {
            styleHolder.iconOffsetLeft
        }
        return width
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var iconOffset = 0
        if (hasFilters()) {
            selectedFilters.layout(
                styleHolder.filterOffsetLeft,
                (filterHeight - selectedFilters.height) / 2
            )
            iconOffset = selectedFilters.right
        } else {
            iconOffset += styleHolder.iconOffsetLeft
            selectedFilters.layout(0, 0)
        }
        filterIcon.layout(iconOffset, (filterHeight - filterIcon.height) / 2)
        bottomDividerRect.set(0, filterHeight - styleHolder.dividerHeight, right, bottom)
        bounds.right = right
        bounds.bottom = bottom
        selectedFilters.setStaticTouchRect(bounds)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        selectedFilters.draw(canvas)
        filterIcon.draw(canvas)
        if (dividerIsVisible) {
            canvas.drawRect(bottomDividerRect, bottomDividerPaint)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        filterIcon.isEnabled = enabled
        selectedFilters.isEnabled = enabled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) =
        touchManager.onTouch(this, event) || super.onTouchEvent(event)

    internal fun setFilled(isFilled: Boolean) {
        filterIcon.configure {
            text = if (isFilled) styleHolder.filterIconFiled else styleHolder.filterIcon
        }
    }

    internal fun setColor(filterColor: FilterColorType) {
        roundedColorBackground.color =
            if (filterColor == FilterColorType.BASE) {
                styleHolder.backgroundColor
            } else {
                styleHolder.backgroundColorAdditional
            }
    }

    internal fun setRadius(radius: Float) = roundedColorBackground.setRadius(radius)

    fun setSize(filterSize: FilterSize) {
        filterHeight = if (filterSize == FilterSize.MEDIUM) {
            styleHolder.filterHeight
        } else {
            styleHolder.filterHeightSmall
        }
    }
}