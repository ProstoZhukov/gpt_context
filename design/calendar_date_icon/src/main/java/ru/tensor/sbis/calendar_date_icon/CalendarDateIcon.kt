package ru.tensor.sbis.calendar_date_icon

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.theme.global_variables.IconSize

/**
 * Drawable иконки календаря, с кастомных числом
 *
 * @author da.zolotarev
 */
class CalendarDateIcon internal constructor(
    context: Context,
    private var configuration: CalendarDateIconConfiguration,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    controller: CalendarDateIconController
) : Drawable(), CalendarDateIconApi by controller {
    private var selectedNumberColor = Color.MAGENTA
    private var numberColor = Color.MAGENTA

    private var selectedIconColor = Color.MAGENTA
    private var iconColor = Color.MAGENTA

    @JvmOverloads
    constructor(
        context: Context,
        configuration: CalendarDateIconConfiguration = CalendarDateIconConfiguration.BORDER_ONLY,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.calendarDateIconStyle,
        @StyleRes defStyleRes: Int = R.style.CalendarDateIconStyle_TabNavView,
    ) : this(
        context,
        configuration,
        attrs,
        defStyleAttr,
        defStyleRes,
        CalendarDateIconController(IconSize.X3L.getDimen(context))
    )

    private val numberTopPadding =
        context.resources.getDimensionPixelSize(R.dimen.calendar_date_icon_number_top_padding)

    // Размер соответсвует иконке ННП
    override var size: Float = IconSize.X3L.getDimen(context)
        set(value) {
            if (field != value) {
                field = value
                iconPaint.apply {
                    textSize = value
                }
                numberPaint.apply {
                    textSize = value * NUMBER_SCALING
                }
                this.invalidateSelf()
            }
        }

    private val iconPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getSbisNavigationIconTypeface(context)
        textSize = IconSize.X3L.getDimen(context)
    }

    private val numberPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = IconSize.X3L.getDimen(context) * NUMBER_SCALING
        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    private val iconLayout = TextLayout {
        paint = iconPaint
        text = configuration.icon
    }

    private val numberLayout = TextLayout {
        paint = numberPaint
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.CalendarDateIcon, defStyleAttr, defStyleRes) {
            selectedNumberColor =
                getColor(R.styleable.CalendarDateIcon_CalendarDateIcon_selectedTextColor, Color.MAGENTA)
            numberColor = getColor(R.styleable.CalendarDateIcon_CalendarDateIcon_textColor, Color.MAGENTA)
            iconColor = getColor(R.styleable.CalendarDateIcon_CalendarDateIcon_iconColor, Color.MAGENTA)
            selectedIconColor = getColor(R.styleable.CalendarDateIcon_CalendarDateIcon_selectedIconColor, Color.MAGENTA)
            numberPaint.color = numberColor
            iconPaint.color = iconColor
        }
        controller.attach(this, numberLayout)
    }

    /**
     * Выставляет [ColorStateList] для иконок, для работы при смене состояния требуется дергать [setIsSelected]
     */
    fun setColorList(colorStateList: ColorStateList) {
        iconLayout.colorStateList = colorStateList
        numberLayout.colorStateList = colorStateList
    }

    /**
     * Текущее состояние "выделенности" иконки
     */
    fun setIsSelected(isSelected: Boolean) {
        iconLayout.isSelected = isSelected
        numberLayout.isSelected = isSelected
        iconLayout.configure {
            text = if (isSelected) configuration.selectedIcon else configuration.icon
            paint.color = if (isSelected) selectedIconColor else iconColor
        }
        numberLayout.configure {
            paint.color = if (isSelected) selectedNumberColor else numberColor
        }
        iconLayout.layout(iconLayout.left, iconLayout.top)
        numberLayout.layout(numberLayout.left, numberLayout.top)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        iconLayout.draw(canvas)
        numberLayout.draw(canvas)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        iconLayout.layout((bounds.width() - iconLayout.width) / 2, bounds.top)
        numberLayout.layout(
            (bounds.width() - numberLayout.width) / 2,
            bounds.top + numberTopPadding
        )
    }

    override fun setTint(tintColor: Int) {
        iconLayout.colorStateList = ColorStateList.valueOf(tintColor)
        numberLayout.colorStateList = ColorStateList.valueOf(tintColor)
    }

    override fun getIntrinsicHeight() = size.toInt()
    override fun getIntrinsicWidth() = size.toInt()

    override fun setAlpha(alpha: Int) {
        iconPaint.alpha = alpha
        numberPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        iconPaint.colorFilter = colorFilter
        numberPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    companion object {
        const val MIN_DATE = 1
        const val MAX_DATE = 31
        const val NUMBER_SCALING = 0.7f
    }
}