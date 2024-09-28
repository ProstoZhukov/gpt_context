package ru.tensor.sbis.calendar.date.view.day_legend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import org.json.JSONObject
import ru.tensor.sbis.calendar.date.EVENTS_COUNT_POSITION_BOTTOM
import ru.tensor.sbis.calendar.date.EVENTS_COUNT_POSITION_TOP
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.view.HatchWithOutlineDrawer
import ru.tensor.sbis.calendar.date.view.day.LegendDayViewStyleHolder
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.sp
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.R as RDesign

/**
 * Используется в приложении бизнес
 */
class LegendViewFirstDay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val styleHolder: LegendViewFirstDayStyleHolder
    private val dayStyleHolder: LegendDayViewStyleHolder

    init {
        if (isInEditMode) {
            context.theme.applyStyle(RDesign.style.DefaultLightTheme, true)
            context.theme.applyStyle(R.style.CalendarDateViewBaseStyle, true)
        }
        styleHolder = LegendViewFirstDayStyleHolder()
        dayStyleHolder = LegendDayViewStyleHolder(colorsProvider = ColorsProvider(context))
        styleHolder.load(context, attrs)
        dayStyleHolder.load(context, attrs)
    }

    private val viewHeightSpec: Int = MeasureSpec.makeMeasureSpec(dayStyleHolder.viewHeight, MeasureSpec.EXACTLY)

    private val baselinePaint = Paint().apply {
        color = Color.RED
    }

    //region arrow back
    /** Видимость стрелки */
    var arrowBackVisible: Boolean = true
        set(value) {
            field = value
            arrowBackTextLayout.configure {
                isVisible = value
            }
            safeRequestLayout()
        }
    private val arrowBackTextLayout = TextLayout().apply {
        configure {
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            paint.color = dayStyleHolder.colorsProvider.arrowBack
            paint.textSize = styleHolder.arrowBackTextSize
            text = resources.getString(RDesign.string.design_mobile_icon_arrow_back_android)
        }
    }
    private val arrowBackYOffset = resources.sp(1f)
    //endregion

    //region date
    /** Текст даты */
    var dateText = ""
        set(value) {
            field = value
            dateTextLayout.configure {
                this.text = value
            }
            safeRequestLayout()
        }
    private val dateTextLayout = TextLayout().apply {
        configure {
            this.includeFontPad = false
            this.paint.typeface = TypefaceManager.getRobotoMediumFont(context)
            this.paint.isSubpixelText = true
            this.paint.color = dayStyleHolder.colorsProvider.firstDayText
            paint.textSize = styleHolder.dateTextSize
            needHighWidthAccuracy = true
        }
    }

    private var dateTextMarginEnd = 0f
    //endregion

    //region month
    /** Текст месяца */
    var monthText = ""
        set(value) {
            field = value
            monthTextLayout.configure {
                text = value
            }
            safeRequestLayout()
        }
    private val monthTextLayout = TextLayout().apply {
        configure {
            paint.typeface = TypefaceManager.getRobotoMediumFont(context)
            paint.color = dayStyleHolder.colorsProvider.firstDayText
            paint.textSize = styleHolder.monthTextSize
            needHighWidthAccuracy = true
        }
    }
    //endregion

    //region day of week
    /** Текст дня недели */
    var dayOfWeekText = ""
        set(value) {
            field = value
            dayOfWeekTextLayout.configure {
                text = value
            }
            safeRequestLayout()
        }

    /** Цвет дня недели */
    var dayOfWeekTextColor: Int = TextColor.DEFAULT.getValue(context)
        set(value) {
            field = value
            dayOfWeekTextLayout.configure {
                paint.color = value
            }
            invalidate()
        }
    private val dayOfWeekTextLayout = TextLayout().apply {
        configure {
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            paint.textSize = styleHolder.dayOfWeekTextSize
            paint.color = dayOfWeekTextColor
            paint.isSubpixelText = true
            includeFontPad = false
        }
    }
    //endregion

    //region events count
    /** Видимость счетчика событий */
    var eventsCountIsVisible: Boolean = isInEditMode
        set(value) {
            field = value
            eventsCountTextLayout.configure {
                isVisible = value
            }
            safeRequestLayout()
        }

    /** Позиция рисования счетчика событий */
    var eventsCountPosition: Int = EVENTS_COUNT_POSITION_TOP
        set(value) {
            field = value
            safeRequestLayout()
        }

    /** Текст счетчика событий */
    var eventsCountText = ""
        set(value) {
            field = value
            eventsCountTextLayout.configure {
                text = value
            }
            safeRequestLayout()
        }
    private val eventsCountTextLayout = TextLayout().apply {
        configure {
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            paint.color = dayStyleHolder.colorsProvider.eventsCountText
            paint.textSize = dayStyleHolder.eventsCountTextSize
        }
    }
    //endregion

    //region divider
    /** Цвет разделителя */
    var dividerColor: Int = dayStyleHolder.colorsProvider.divider
        set(value) {
            field = value
            dividerPaint.color = value
            invalidate()
        }

    /** Видимость разделителя */
    var dividerVisible: Boolean = true
        set(value) {
            field = value
            invalidate()
        }
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = dayStyleHolder.dividerWidth.toFloat()
        color = dividerColor
    }

    //endregion
    internal var unwantedVacationDraw: UnwantedVacationDraw = UnwantedVacationDraw.NONE
        set(value) {
            val isInvalidate = field != value
            field = value
            if (isInvalidate) invalidate()
        }

    /** Штриховка нежелательного дня отпуска */
    internal var hatchDrawable: HatchWithOutlineDrawer? = null

    private val borderRect = Rect()
    private val borderRectF = RectF()
    private val borderPath = Path()

    init {
        dateTextMarginEnd = styleHolder.dateTextMarginEnd

        setBackgroundColor(
            if (isInEditMode) {
                ContextCompat.getColor(context, RDesign.color.palette_color_white1)
            } else {
                dayStyleHolder.colorsProvider.firstDayBackground
            }
        )
        if (isInEditMode) {
            dateText = "18"
            monthText = "мая'21"
            dayOfWeekText = "ЧТ"
            eventsCountText = "84"
        }
        accessibilityDelegate = object : AccessibilityDelegate() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info?.text = JSONObject(
                    mapOf(
                        context.resources.getResourceEntryName(R.id.day) to "$dateText $monthText",
                        context.resources.getResourceEntryName(R.id.day_week) to dayOfWeekText
                    )
                ).toString()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        var actualDateBaseline = dayStyleHolder.dateTextBaseline
        if (eventsCountIsVisible && eventsCountPosition == EVENTS_COUNT_POSITION_BOTTOM) {
            actualDateBaseline -= eventsCountTextLayout.textPaint.textSize / 2
        }
        if (arrowBackTextLayout.isVisible) {
            val arrowBackVerticalPosition = actualDateBaseline
            arrowBackTextLayout.layout(
                0,
                (arrowBackVerticalPosition - arrowBackTextLayout.baseline + arrowBackYOffset).toInt()
            )
        }
        dateTextLayout.layout(
            (arrowBackTextLayout.width + styleHolder.arrowBackEndMargin).toInt(),
            (actualDateBaseline - dateTextLayout.baseline).toInt()
        )

        monthTextLayout.layout(
            (dateTextLayout.right + dateTextMarginEnd).toInt(),
            (actualDateBaseline - monthTextLayout.baseline).toInt()
        )

        var actualDayOfWeekBaseline = dayStyleHolder.dayOfWeekTextBaseline
        if (eventsCountIsVisible && eventsCountPosition == EVENTS_COUNT_POSITION_BOTTOM) {
            actualDayOfWeekBaseline -= eventsCountTextLayout.textPaint.textSize / 2
        }
        val dayOfWeekX = dateTextLayout.right + dateTextMarginEnd
        dayOfWeekTextLayout.layout(
            dayOfWeekX.toInt(),
            (actualDayOfWeekBaseline - dayOfWeekTextLayout.baseline).toInt()
        )
        if (eventsCountTextLayout.isVisible) {
            var eventsCountHorizontalPosition = width - dayStyleHolder.eventsCountTextMarginEnd - eventsCountTextLayout.width
            if (dividerVisible) {
                eventsCountHorizontalPosition -= dividerPaint.strokeWidth
            }
            val eventsCountVerticalPosition = if (eventsCountPosition == EVENTS_COUNT_POSITION_TOP) {
                eventsCountTextLayout.height - eventsCountTextLayout.baseline - dayStyleHolder.eventsCountTextMarginTop
            } else {
                height - dayStyleHolder.eventsCountTextMarginTop - eventsCountTextLayout.baseline
            }
            eventsCountTextLayout.layout(eventsCountHorizontalPosition.toInt(), eventsCountVerticalPosition.toInt())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawUnwanted(canvas)
        dateTextLayout.draw(canvas)
        monthTextLayout.draw(canvas)

        if (isInspectMode) {
            canvas.drawLine(
                0f,
                dayStyleHolder.dateTextBaseline,
                width.toFloat(),
                dayStyleHolder.dateTextBaseline,
                baselinePaint
            )
            canvas.drawLine(
                0f,
                dayStyleHolder.dayOfWeekTextBaseline,
                width.toFloat(),
                dayStyleHolder.dayOfWeekTextBaseline,
                baselinePaint
            )
            canvas.drawLine(
                arrowBackTextLayout.right.toFloat(),
                0f,
                arrowBackTextLayout.right.toFloat(),
                height.toFloat(),
                baselinePaint
            )
            canvas.drawLine(
                (arrowBackTextLayout.right + styleHolder.arrowBackEndMargin).toFloat(),
                0f,
                (arrowBackTextLayout.right + styleHolder.arrowBackEndMargin).toFloat(),
                height.toFloat(),
                baselinePaint
            )
            canvas.drawLine(
                dateTextLayout.right.toFloat(),
                0f,
                dateTextLayout.right.toFloat(),
                height.toFloat(),
                baselinePaint
            )
            canvas.drawLine(
                dateTextLayout.right.toFloat() + dateTextMarginEnd,
                0f,
                dateTextLayout.right.toFloat() + dateTextMarginEnd,
                height.toFloat(),
                baselinePaint
            )
        }

        if (arrowBackTextLayout.isVisible) {
            arrowBackTextLayout.draw(canvas)
        }

        dayOfWeekTextLayout.draw(canvas)

        if (eventsCountTextLayout.isVisible) {
            eventsCountTextLayout.draw(canvas)
        }

        if (dividerVisible) {
            canvas.drawLine(
                width - dividerPaint.strokeWidth / 2f,
                0f,
                width - dividerPaint.strokeWidth / 2f,
                height.toFloat(),
                dividerPaint
            )
        }
    }

    fun getRequiredWidth(): Int {
        return (arrowBackTextLayout.width + styleHolder.arrowBackEndMargin + dateTextLayout.width + dateTextMarginEnd + monthTextLayout.width + styleHolder.monthTextMarginEnd).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            if (isInEditMode) {
                MeasureSpec.makeMeasureSpec(getRequiredWidth(), MeasureSpec.EXACTLY)
            } else {
                widthMeasureSpec
            },
            viewHeightSpec
        )
    }

    private fun drawUnwanted(canvas: Canvas) {
        if (unwantedVacationDraw !is UnwantedVacationDraw.NONE) {
            getDrawingRect(borderRect)
            borderRectF.set(borderRect)
            if (unwantedVacationDraw is UnwantedVacationDraw.SINGLE) {
                borderRectF.right -= (hatchDrawable?.outlineWidth ?: 0f) / 2
            }
            borderPath.reset()
            borderPath.addRect(borderRectF, Path.Direction.CW)
            borderPath.close()
            if (unwantedVacationDraw.drawFill) {
                hatchDrawable?.run {
                    drawOnlyHatch(canvas, hatchPath)
                    drawOnlyBorder(canvas, borderPath)
                }
            }
        }
    }
}

/**
 * Способ отрисовки нежелательных дней отпуска
 */
internal sealed class UnwantedVacationDraw(open val drawFill: Boolean) {
    /** не рисовать */
    object NONE : UnwantedVacationDraw(false)

    /** рисовать как одиночный день */
    data class SINGLE(override val drawFill: Boolean) : UnwantedVacationDraw(drawFill)

    /** рисовать как несколько дней без границы справа*/
    data class MULTI(override val drawFill: Boolean) : UnwantedVacationDraw(drawFill)
}

private const val isInspectMode = false