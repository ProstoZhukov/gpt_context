package ru.tensor.sbis.calendar.date.view.day

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import org.joda.time.LocalDate
import org.json.JSONObject
import ru.tensor.sbis.calendar.date.EVENTS_COUNT_POSITION_BOTTOM
import ru.tensor.sbis.calendar.date.EVENTS_COUNT_POSITION_TOP
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.SHOW_HINT_MODE_BUSY_LEVEL
import ru.tensor.sbis.calendar.date.SHOW_HINT_MODE_EVENTS_COUNT
import ru.tensor.sbis.calendar.date.data.ActivityForPainting
import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.view.HatchDrawable
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import ru.tensor.sbis.calendar.date.view.resolveBackgroundColorWithColorsProvider
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.sp
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.R as RDesign

/** Вью дня в пикере легенды */
class LegendDayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val styleHolder: LegendDayViewStyleHolder

    init {
        if (isInEditMode) {
            this.context.theme.applyStyle(R.style.CalendarDateViewBaseStyle, true)
            this.context.theme.applyStyle(RDesign.style.DefaultLightTheme, true)
        }
        styleHolder = LegendDayViewStyleHolder(colorsProvider = ColorsProvider(context))
        styleHolder.load(context, attrs)
    }

    private val baselinePaint = Paint().apply {
        color = Color.RED
    }
    private val colorsProvider = ColorsProvider(this.context)

    /** День ячейки */
    var date: LocalDate = LocalDate()

    /** Отображение счетчика событий в углу либо уровня занятости в дне */
    var showHintMode: Int = SHOW_HINT_MODE_BUSY_LEVEL
        set(value) {
            field = value
            eventsCountTextLayout.configure {
                isVisible = value == SHOW_HINT_MODE_EVENTS_COUNT
            }
            safeRequestLayout()
        }

    /** Позиция рисования счетчика событий */
    var eventsCountPosition: Int = EVENTS_COUNT_POSITION_TOP
        set(value) {
            field = value
            safeRequestLayout()
        }

    /**
     * Параметр отрисовка по правилу отпуск в праздничный день.
     * Рисуется штриховка на фоне, под углом 45 градусов, линии рисуются [colorBlockPaint] в функции [paintDay]
     */
    var isVacationOnHoliday: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    /** Нежелательный для отпуска день */
    var isUnwantedVacationDay: Boolean = false

    /** Схема раскрашивания фона цветными блоками */
    var coloringScheme: List<ActivityForPainting>? = null
        set(value) {
            field = value
            invalidate()
        }

    /** Тип дня */
    var dayType = EventType.WORKDAY
        set(value) {
            field = value
            invalidate()
        }

    /** Цвет сплошного фона дня */
    var dayBackgroundColor: Int? = null
        set(value) {
            field = value
            priorityBackground = value ?: Color.BLACK
        }

    /** Текст дня недели */
    var dayOfWeekText = ""
        set(value) {
            field = value
            dayOfWeekTextLayout.configure {
                text = value
            }
            safeRequestLayout()
        }

    /** Текст даты */
    var dateText = ""
        set(value) {
            field = value
            dateTextLayout.configure {
                text = value
            }
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

    /** Уровень занятости дня */
    var busyLevel: List<Boolean>? = null
        set(value) {
            field = value
            invalidate()
        }

    /** Цвет уровня занятости дня */
    var busyLevelColor: Int = colorsProvider.busyLevel
        set(value) {
            field = value
            paintBusyLevel.color = value
            invalidate()
        }

    /** Цвет даты */
    var dateTextColor: Int = colorsProvider.colorDateText
        set(value) {
            field = value
            dateTextLayout.configure {
                paint.color = value
            }
            invalidate()
        }

    /** Цвет дня недели */
    var dayOfWeekTextColor: Int = colorsProvider.workdayText
        set(value) {
            field = value
            dayOfWeekTextLayout.configure {
                paint.color = value
            }
            invalidate()
        }
    val isUsualDay: Boolean
        get() = dayType in listOf(EventType.DAY_OFF, EventType.WORKDAY)

    //region busy level
    private var busyLevelWidth = 0f
    private val canvasStep = resources.sp(3.5f)
    private val paintBusyLevel = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1 * context.resources.displayMetrics.density
        color = colorsProvider.busyLevel
    }
    //endregion

    //region date
    private val defaultDateTextBaseline = styleHolder.dateTextBaseline
    private val dateTextLayout = TextLayout().apply {
        configure {
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            paint.textSize = styleHolder.dateTextSize
            paint.color = dateTextColor
            this.paint.isSubpixelText = true
            this.includeFontPad = false
        }
    }
    //endregion

    //region day of week
    private val dayOfWeekTextLayout = TextLayout().apply {
        configure {
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            paint.textSize = styleHolder.dayOfWeekTextSize
            paint.color = TextColor.DEFAULT.getValue(this@LegendDayView.context)
            this.paint.isSubpixelText = true
            includeFontPad = false
        }
    }
    private val defaultDayOfWeekBaseline = styleHolder.dayOfWeekTextBaseline
    //endregion

    //region events count
    private val eventsCountTextLayout = TextLayout().apply {
        configure {
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            paint.textSize = styleHolder.eventsCountTextSize
            paint.color = colorsProvider.eventsCountText
        }
    }
    private val busyLevelDateMargin = resources.sp(2).toFloat()
    //endregion

    //region divider
    private val dividerPaint = Paint()
    private val dividerWidth: Int = styleHolder.dividerWidth
    //endregion

    //region measure
    private val viewWidthSpec: Int = MeasureSpec.makeMeasureSpec(styleHolder.viewWidth, MeasureSpec.EXACTLY)
    private val viewHeightSpec: Int = MeasureSpec.makeMeasureSpec(styleHolder.viewHeight, MeasureSpec.EXACTLY)
    //endregion

    private val colorBlockPaint = Paint()
    private val hatchDrawable = HatchDrawable(resources)

    @ColorInt
    private var priorityBackground: Int = 0
    private val textRect = Rect()

    private val vacationOnHolidayColor: Int

    private val maxDateTextWidth =
        Array(31, { i -> i }).map { dateTextLayout.textPaint.measureText(it.toString()) }.max()

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.calendar_date_view_color_cross_vacation_holiday, typedValue, true)
        vacationOnHolidayColor = typedValue.data
        hatchDrawable.color = vacationOnHolidayColor
        dividerPaint.color = colorsProvider.divider

        if (isInEditMode) {
            dateText = "24"
            dayOfWeekText = "ВТ"
            eventsCountText = "84"
            busyLevel = listOf(true, true, true)
        }

        id = R.id.calendar_date_view_id
        accessibilityDelegate = AutoTestHelper()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(viewWidthSpec, viewHeightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val eventsCountDrawnOnBottom =
            showHintMode == SHOW_HINT_MODE_EVENTS_COUNT && eventsCountPosition == EVENTS_COUNT_POSITION_BOTTOM
        var actualDateBaseline = defaultDateTextBaseline
        if (eventsCountDrawnOnBottom) {
            actualDateBaseline -= eventsCountTextLayout.textPaint.textSize / 2
        }
        dateTextLayout.layout(
            (width / 2f - dividerWidth / 2f - dateTextLayout.width / 2f).toInt(),
            (actualDateBaseline - dateTextLayout.baseline).toInt()
        )
        var actualDayOfWeekBaseline = defaultDayOfWeekBaseline
        if (eventsCountDrawnOnBottom) {
            actualDayOfWeekBaseline -= eventsCountTextLayout.textPaint.textSize / 2
        }
        dayOfWeekTextLayout.layout(
            (width / 2f - dividerWidth / 2f - dayOfWeekTextLayout.width / 2f).toInt(),
            (actualDayOfWeekBaseline - dayOfWeekTextLayout.baseline).toInt()
        )
        busyLevelWidth = width / 2f - maxDateTextWidth / 2f - busyLevelDateMargin * 2 - dividerWidth
        if (eventsCountTextLayout.isVisible) {
            val eventsCountVerticalPosition = if (eventsCountPosition == EVENTS_COUNT_POSITION_TOP) {
                eventsCountTextLayout.height - eventsCountTextLayout.baseline - styleHolder.eventsCountTextMarginTop
            } else {
                height - styleHolder.eventsCountTextMarginTop - eventsCountTextLayout.baseline
            }
            eventsCountTextLayout.layout(
                (width - dividerWidth - styleHolder.eventsCountTextMarginEnd - eventsCountTextLayout.width).toInt(),
                eventsCountVerticalPosition.toInt()
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // отрисовка фона
        val backgroundColor = if (isUsualDay && isUnwantedVacationDay) {
            Color.TRANSPARENT
        } else if (isUnwantedVacationDay && isVacationOnHoliday) {
            colorsProvider.colorDayOff
        } else if (dayBackgroundColor != null) {
            if (dayType != EventType.NOT_HIRED) priorityBackground
            else colorsProvider.notHired
        } else {
            dayType.resolveBackgroundColorWithColorsProvider(colorsProvider)
        }

        paintDay(canvas, backgroundColor)

        coloringScheme?.forEach {
            paintDay(canvas, it.sbisColor.getColor(context), it.percentStart, it.percentEnd)
        }
        if (isVacationOnHoliday) {
            hatchDrawable.color = colorsProvider.colorCrossedVacation
            drawVacationOnHoliday(canvas)
        }

        canvas.drawRect((width - dividerWidth).toFloat(), 0f, width.toFloat(), height.toFloat(), dividerPaint)

        if (isInspectMode) {
            canvas.drawLine(
                0f,
                defaultDateTextBaseline,
                width.toFloat(),
                defaultDateTextBaseline,
                baselinePaint
            )
            canvas.drawLine(
                0f,
                defaultDayOfWeekBaseline,
                width.toFloat(),
                defaultDayOfWeekBaseline,
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
                (dateTextLayout.right + busyLevelDateMargin).toFloat(),
                0f,
                (dateTextLayout.right + busyLevelDateMargin).toFloat(),
                height.toFloat(),
                baselinePaint
            )
        }

        // отрисовка данных
        dateTextLayout.draw(canvas)
        dayOfWeekTextLayout.draw(canvas)
        dateTextLayout.textPaint.getTextBounds(dateText, 0, dateText.length, textRect)
        val dateTextHeight = textRect.height()
        if (eventsCountTextLayout.isVisible) {
            eventsCountTextLayout.draw(canvas)
        }
        if (showHintMode != SHOW_HINT_MODE_EVENTS_COUNT) {
            drawBusyLevel(
                canvas,
                defaultDateTextBaseline - dateTextHeight + canvasStep / 1.5f,
                dateTextLayout.right + busyLevelDateMargin
            )
        }
    }

    private fun paintDay(canvas: Canvas, color: Int, percentStart: Int = 0, percentEnd: Int = 100) {
        colorBlockPaint.color = color
        canvas.drawRect(
            0f,
            height * (percentStart / 100f),
            width.toFloat(),
            height * (percentEnd / 100f),
            colorBlockPaint
        )
    }

    private fun drawVacationOnHoliday(canvas: Canvas) {
        canvas.save()
        colorBlockPaint.color = colorsProvider.colorHolidayCrossVac
        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            colorBlockPaint
        )
        hatchDrawable.setBounds(0, 0, (width - dividerWidth), height)
        hatchDrawable.draw(canvas)
        canvas.restore()
    }

    private fun drawBusyLevel(canvas: Canvas, topBorder: Float, startX: Float) {
        busyLevel?.forEachIndexed { index, level ->
            val top = topBorder + canvasStep * index
            if (level) {
                canvas.drawLine(
                    startX,
                    top,
                    startX + busyLevelWidth,
                    top,
                    paintBusyLevel
                )
            }
        }
    }

    private inner class AutoTestHelper : AccessibilityDelegate() {
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.text = JSONObject(
                mapOf(
                    context.resources.getResourceEntryName(R.id.day) to dateText,
                    context.resources.getResourceEntryName(R.id.day_week) to dayOfWeekText
                )
            ).toString()
        }
    }
}

private const val isInspectMode = false