package ru.tensor.sbis.calendar.date.view.day

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import org.joda.time.LocalDate
import org.json.JSONObject
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.data.ActivityForPainting
import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.data.SelectedType
import ru.tensor.sbis.calendar.date.view.HatchDrawable
import ru.tensor.sbis.calendar.date.view.SelectedDayDrawable
import ru.tensor.sbis.calendar.date.view.day.subviews.DateView
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.R as RDesign

private const val PHONE_MEASURE_COEFFICIENT = 1.16f

internal class DayView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : DateView(context, attrs, defStyle) {

    var drawBackgroundUsualDay: Boolean = true
    var isVacationOnHoliday: Boolean = false
    var isUnwantedVacationDay: Boolean = false
    var coloringScheme: List<ActivityForPainting>? = null @MainThread set
    var dayType = EventType.WORKDAY
    var dayBackgroundColor: Int? = null
        set(value) {
            field = value
            priorityBackground = value ?: Color.TRANSPARENT
        }
    var selectedType: SelectedType = SelectedType.NONE()

    var dateTextSize: Float
        get() = textSize
        set(value) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }
    var dateText: CharSequence?
        get() = text
        set(value) {
            text = value
        }
    var dateFullText: LocalDate? = null
        set(value) {
            field = value
            accessibilityDelegate = object : AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info?.text = JSONObject(
                        mapOf(
                            context.resources.getResourceEntryName(R.id.text_date) to "$dateFullText",
                        )
                    ).toString()
                }
            }
        }
    /**
    "Отрисовщик" выбранного дня
    Достаточно много времени уходит на создание, поэтому lazy
     */
    lateinit var selectedDayDrawable: SelectedDayDrawable
    @ColorInt
    var priorityBackground: Int = 0
    private val hatchDrawable = HatchDrawable(resources)
    val margin: Float
        get() = context.resources.getDimension(RDesign.dimen.small_delimiter_height) / 2
    private val dayPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.calendar_date_view_color_cross_vacation_holiday, typedValue, true)
        hatchDrawable.color = typedValue.data
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DayView, 0, 0)
        try {
            createDateView()
            if (typedArray.hasValue(R.styleable.DayView_dateTextSize))
                setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimension(R.styleable.DayView_dateTextSize, 0f))

            text = typedArray.getInteger(R.styleable.DayView_date_text, 0).toString()
            isCurrent = typedArray.getBoolean(R.styleable.DayView_is_current, false)
        } finally {
            typedArray.recycle()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height: Float = newWidth.toFloat()
        height /= PHONE_MEASURE_COEFFICIENT
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        if (selectedType !is SelectedType.NONE) {
            selectedDayDrawable.onDraw(selectedType, canvas, colorsProvider)
            super.onDraw(canvas)
            return
        }

        val backgroundColor = if (isUsualDay() && !drawBackgroundUsualDay) {
            Color.TRANSPARENT
        } else if (isVacationOnHoliday) {
            colorsProvider.colorHolidayCrossVac
        } else if (dayBackgroundColor != null) {
            if (dayType != EventType.NOT_HIRED) priorityBackground
            else colorsProvider.notHired
        } else {
            when (dayType) {
                EventType.WORKDAY -> colorsProvider.colorWorkday
                EventType.DAY_OFF -> colorsProvider.colorDayOff
                EventType.TRUANCY -> colorsProvider.colorTruancy
                EventType.BUSINESS_TRIP -> colorsProvider.colorBusinessTrip
                EventType.PLAN_VACATION -> colorsProvider.colorPlanVacation
                EventType.PLAN_VACATION_ON_AGREEMENT -> colorsProvider.colorPlanVacationOnAgreement
                EventType.PLAN_VACATION_ON_DELETION -> colorsProvider.colorPlanVacationOnDeletion
                EventType.FACT_VACATION -> colorsProvider.colorFactVacation
                EventType.FACT_VACATION_WITHOUT_PAY -> colorsProvider.colorFactVacation
                EventType.FACT_VACATION_MOBILIZATION -> colorsProvider.colorFactVacation
                EventType.SICK_LEAVE -> colorsProvider.colorSickLeave
                EventType.DOWNTIME -> colorsProvider.colorDowntime
                EventType.BIRTHDAY -> colorsProvider.colorBirthday
                EventType.BABY_CARE -> colorsProvider.colorSickLeave
                EventType.NOT_HIRED -> colorsProvider.notHired
                EventType.REPORT -> colorsProvider.colorReport
                EventType.TIME_OFF -> colorsProvider.colorTimeOff
            }
        }

        paintDay(canvas, backgroundColor, paint = paint)

        coloringScheme?.forEach {
            paintDay(canvas, it.sbisColor.getColor(context), it.percentStart, it.percentEnd, dayPaint)
        }
        drawVacationOnHoliday(canvas)
        super.onDraw(canvas)
    }

    fun isUsualDay(): Boolean {
        return dayType in listOf(EventType.DAY_OFF, EventType.WORKDAY) && !dateText.isNullOrEmpty() && dateText != "0"
    }

    private fun createDateView() {
        id = R.id.text_date

        typeface = TypefaceManager.getRobotoRegularFont(context)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.calendar_day_text_size))
    }

    private fun paintDay(canvas: Canvas, color: Int, percentStart: Int = 0, percentEnd: Int = 100, paint: Paint) {
        paint.color = color
        canvas.save()
        canvas.translate(margin, margin)
        canvas.clipRect(0f, 0f, width - margin * 2, height - margin * 2)
        canvas.drawRect(
            0f,
            height * (percentStart / 100f),
            width.toFloat(),
            height * (percentEnd / 100f),
            paint
        )
        canvas.restore()
    }

    private fun drawVacationOnHoliday(canvas: Canvas) {
        if (isVacationOnHoliday) {
            canvas.save()
            canvas.clipRect(0 + margin,
                            0 + margin,
                            width.toFloat() - margin,
                            height.toFloat() - margin)
            hatchDrawable.setBounds(0, 0, width, height)
            hatchDrawable.draw(canvas)
            canvas.restore()
        }
    }

}


