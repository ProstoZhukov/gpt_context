package ru.tensor.sbis.calendar.date.view.day_legend

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Region
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.cardview.widget.CardView
import androidx.core.graphics.toRectF
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.EVENTS_COUNT_POSITION_TOP
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.SHOW_HINT_MODE_BUSY_LEVEL
import ru.tensor.sbis.calendar.date.SHOW_HINT_MODE_EVENTS_COUNT
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.data.ModeSelectedDay
import ru.tensor.sbis.calendar.date.view.HatchDrawable
import ru.tensor.sbis.calendar.date.view.HatchWithOutlineDrawer
import ru.tensor.sbis.calendar.date.view.day.LegendDayView
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import ru.tensor.sbis.calendar.date.view.day_legend.adapter.DayLegendAdapter
import ru.tensor.sbis.calendar.date.view.day_legend.adapter.positionOfDate
import ru.tensor.sbis.calendar.date.view.day_legend.adapter.startDateToCountFrom
import ru.tensor.sbis.calendar.date.view.resolveBackgroundColorWithColorsProvider
import ru.tensor.sbis.design.custom_view_tools.utils.sp
import ru.tensor.sbis.design.utils.GravitySnapHelper
import ru.tensor.sbis.design.utils.LocaleUtils
import ru.tensor.sbis.design.utils.SnapGravity
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.delegatePropertyMT
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.min
import kotlin.properties.Delegates
import ru.tensor.sbis.design.R as RDesign

class LegendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.CalendarDateViewStyle,
    @StyleRes defStyleRes: Int = R.style.CalendarDateViewBaseStyle
) : FrameLayout(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val SCROLL_LEGEND_DEBOUNCE_TIME = 200L
        private const val MAX_ALPHA = 0xFF
        private const val UPDATE_HOLDER_THRESHOLD = 4
    }

    init {
        if (isInEditMode) {
            this.context.theme.applyStyle(RDesign.style.DefaultLightTheme, true)
        }
    }

    /** Видимость стрелки "назад" */
    @Suppress("unused")
    var arrowBackVisible by delegatePropertyMT({ firstDay.arrowBackVisible }) { firstDay.arrowBackVisible = it }

    /** Дата выбрана в результате клика или скролла */
    var onTypeSelectDay: ((ModeSelectedDay) -> Unit)?
        get() = dayLegendAdapter.onModeSelectDay
        set(value) {
            dayLegendAdapter.onModeSelectDay = value
        }

    /** Если true, то будем рисовать полоску отчета при отсутствии событий */
    @Suppress("unused")
    var showReportMarkForced: Boolean
        get() = dayLegendAdapter.showReportMarkForced
        set(value) {
            dayLegendAdapter.showReportMarkForced = value
        }

    /** Нажатие на первый день (закрепленный в начале строки) */
    @Suppress("unused")
    var onFirstDayClicked by Delegates.observable<OnClickListener?>(null) { _, _, newValue ->
        firstDay.setOnClickListener(newValue)
    }
    /** Нажатие на день в скроллирующейся области */
    var onDayClicked: ((LocalDate) -> Unit)?
        get() = dayLegendAdapter.onDayClicked
        set(value) {
            dayLegendAdapter.onDayClicked = value
        }

    //region view
    private val firstDay = LegendViewFirstDay(this.context, attrs).apply {
        id = R.id.legend_first_day_view_id
    }
    private val recycler = RecyclerView(this.context, attrs, defStyleAttr).apply {
        id = R.id.legend_first_recycler_view_id
    }
    private val cardView = CardView(this.context)
    private val legendContainer = LinearLayout(this.context).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutTransition = LayoutTransition()
    }
    //endregion

    init {
        cardView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        val cornerRadius = this.context.getDimen(R.attr.calendar_date_view_corner_radius)
        cardView.radius = cornerRadius
        addView(cardView)
        legendContainer.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        cardView.addView(legendContainer)
        firstDay.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        legendContainer.addView(firstDay)
        recycler.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        legendContainer.addView(recycler)
        val legendPadding = this.context.getDimenPx(R.attr.calendar_date_view_legend_offset)
        updatePadding(left = legendPadding, top = legendPadding, right = legendPadding, bottom = legendPadding)
    }

    private val colorsProvider = ColorsProvider(this.context)

    private val showHintMode: Int
    private val eventsCountPosition: Int
    private val currentDateScrollLimit: Boolean

    private val colorDrawable = ColorDrawable()
    private val hatchDrawable = HatchDrawable(resources)

    init {
        val typedArray = this.context.theme.obtainStyledAttributes(attrs, R.styleable.LegendView, 0, 0)
        try {
            showHintMode = typedArray.getInt(R.styleable.LegendView_showHintMode, SHOW_HINT_MODE_BUSY_LEVEL)
            eventsCountPosition = typedArray.getInt(R.styleable.LegendView_eventsCountPosition, EVENTS_COUNT_POSITION_TOP)
            firstDay.arrowBackVisible = typedArray.getBoolean(R.styleable.LegendView_backIconVisibility, true)
            currentDateScrollLimit = typedArray.getBoolean(R.styleable.LegendView_currentDateScrollLimit, false)
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.calendar_date_view_color_cross_vacation_holiday, typedValue, true)
            hatchDrawable.color = typedValue.data
        } finally {
            typedArray.recycle()
        }
    }

    private val isTablet = resources.getBoolean(RDesign.bool.is_tablet)
    private val dayOfMonthFormat = SimpleDateFormat("d", LocaleUtils.getDefaultLocale(context))
    private val monthFormat = SimpleDateFormat(if (isTablet) "MMMM''yy" else "MMM''yy", LocaleUtils.getDefaultLocale(context))
    private val monthFormatWithoutYear = SimpleDateFormat(if (isTablet) "MMMM" else "MMM", LocaleUtils.getDefaultLocale(context))
    private val dayFormat = SimpleDateFormat(if (isTablet) "EEEE" else "EE", LocaleUtils.getDefaultLocale(context))

    private val colorWorkday = colorsProvider.firstDayWorkdayText
    private val colorHoliday = colorsProvider.firstDayHolidayText

    private val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    private val dayLegendAdapter = DayLegendAdapter(currentDateScrollLimit).also {
        recycler.adapter = it
        it.showHintMode = showHintMode
        it.eventsCountPosition = eventsCountPosition
    }
    private var currentScrollingState = SCROLL_STATE_IDLE
        set(value) {
            field = value
            scrollStateSubject.onNext(value)
        }
    private var itemHeight = resources.sp(LegendViewDimensions.HEIGHT)
    private var sizeAnimation = WidthResizeAnimation(firstDay)

    private var currentLegendDate = LocalDate.now()
        set(value) {
            field = value
            _currentDateChangeSubject.onNext(value)
        }
    private val currentYear = LocalDate.now().year
    private val scrollLegend = PublishSubject.create<LocalDate>()
    val hatchWithOutlineDrawer = HatchWithOutlineDrawer(
        context,
        context.resources.getDimension(R.dimen.legend_divider_height) * 2
    )

    private val scrollStateSubject = PublishSubject.create<Int>()
    private val _currentDateChangeSubject = PublishSubject.create<LocalDate>()
    /** Выбранная дата при остановке скролла */
    @Suppress("unused")
    val currentDateChangeSubject: Observable<LocalDate> = Observable.combineLatest(
        _currentDateChangeSubject,
        scrollStateSubject.startWith(SCROLL_STATE_IDLE),
    ) { date, scrollState ->
        date to scrollState
    }
        .filter { it.second == SCROLL_STATE_IDLE }
        .map { it.first }

    init {
        initScrollLegend()

        LayerDrawable(arrayOf(colorDrawable, hatchDrawable)).apply {
            firstDay.background = this
        }

        firstDay.hatchDrawable = hatchWithOutlineDrawer
        firstDay.dividerVisible = true

        recycler.layoutManager = layoutManager
        GravitySnapHelper(SnapGravity.START).attachToRecyclerView(recycler)
        recycler.itemAnimator = null
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                currentScrollingState = newState
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
                val firstVisibleView = layoutManager.findViewByPosition(firstVisiblePos)
                firstVisibleView?.let {
                    val scrolledDate = startDateToCountFrom(currentDateScrollLimit).plusDays(firstVisiblePos - if (dx < 0) 1 else 0)
                    if (scrolledDate != currentLegendDate &&
                        (dx > 0 && abs(it.left) > it.width / 2 || dx == 0 || dx < 0 && abs(it.left) < it.width / 2)) {
                        if (currentScrollingState == RecyclerView.SCROLL_STATE_DRAGGING
                            || currentScrollingState == RecyclerView.SCROLL_STATE_SETTLING) {
                            currentLegendDate = scrolledDate
                            scrollLegend.onNext(scrolledDate)
                        }
                    }
                    val dayInfo = dayLegendAdapter.data[currentLegendDate]
                    firstDay.unwantedVacationDraw = getUnwantedCurrentDay(dayInfo)
                }
            }
        })
        recycler.setBackgroundColor(Color.WHITE)
        recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {

            private val unwantedVacationDaysRegion = Region()
            private val unwantedVacationDaysPath = Path()
            private val childBounds = Rect()
            private val daysBackgroundPaint = Paint().apply {
                style = Paint.Style.FILL_AND_STROKE
            }

            override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(canvas, parent, state)
                unwantedVacationDaysPath.reset()
                unwantedVacationDaysRegion.setEmpty()
                val horizontalScroll = parent.computeHorizontalScrollOffset()
                val diagonalOffset = horizontalScroll.toFloat() % (hatchWithOutlineDrawer.gap * 2f)
                hatchWithOutlineDrawer.startLinesOffset = -diagonalOffset
                for (i in 0 until parent.childCount) {
                    val child: View = parent.getChildAt(i)
                    if (child is LegendDayView && child.isUnwantedVacationDay) {
                        parent.getDecoratedBoundsWithMargins(child, childBounds)
                        unwantedVacationDaysRegion.op(
                            Rect(
                                child.left - if (isCurrentDayAndFirstDayUnwanted(child.date)) hatchWithOutlineDrawer.outlineWidth.toInt() else 0,
                                child.top,
                                child.right,
                                child.bottom
                            ), Region.Op.UNION
                        )

                        daysBackgroundPaint.color = when {
                            child.isUsualDay && child.dayType == EventType.WORKDAY -> colorsProvider.colorWorkday
                            child.isUsualDay && child.dayType == EventType.DAY_OFF -> colorsProvider.colorDayOff
                            else -> Color.TRANSPARENT
                        }
                        if (daysBackgroundPaint.color != Color.TRANSPARENT) {
                            canvas.drawRect(
                                childBounds.toRectF(),
                                daysBackgroundPaint
                            )
                        }
                    }
                }

                unwantedVacationDaysRegion.getBoundaryPath(unwantedVacationDaysPath)
                canvas.save()
                if (hatchWithOutlineDrawer.hatchPath.isEmpty) {
                    hatchWithOutlineDrawer.fillPath(0, canvas.width, 0, canvas.height, hatchWithOutlineDrawer.hatchPath)
                }
                canvas.clipPath(unwantedVacationDaysPath)
                hatchWithOutlineDrawer.drawOnlyHatch(canvas = canvas, hatchWithOutlineDrawer.hatchPath)
                canvas.restore()
            }

            override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDrawOver(canvas, parent, state)
                canvas.save()
                canvas.clipPath(unwantedVacationDaysPath)
                hatchWithOutlineDrawer.drawOnlyBorder(canvas = canvas, pathBorder = unwantedVacationDaysPath)
                canvas.restore()
            }
        })

        if (isInEditMode) {
            setCurrentDate(LocalDate.now())
        }
    }

    private fun isCurrentDayAndFirstDayUnwanted(date: LocalDate): Boolean =
        dayLegendAdapter.data[currentLegendDate]?.isUnwantedVacDay == true
                && dayLegendAdapter.data[date]?.isUnwantedVacDay == true

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(itemHeight + paddingTop + paddingBottom, MeasureSpec.EXACTLY)
        )
    }

    /**
     * Установить коллекцию данных [days]. Если [cleanOldData] = true, то перетрутся старые данные,
     * в противном случае новые данные устанавливаются без удаления старых
     */
    fun setData(days: Collection<Day>, cleanOldData: Boolean = false) {
        dayLegendAdapter.setData(
            days,
            layoutManager.findFirstVisibleItemPosition() - UPDATE_HOLDER_THRESHOLD,
            layoutManager.findLastVisibleItemPosition() + UPDATE_HOLDER_THRESHOLD,
            cleanOldData,
        )
        fillCurrentDayLegend()
    }

    /** Установить текущую дату [date] */
    fun setCurrentDate(date: LocalDate) {
        currentLegendDate = date
        firstItemSetText(date, false)
        firstDay.eventsCountIsVisible = showHintMode == SHOW_HINT_MODE_EVENTS_COUNT
        firstDay.eventsCountPosition = eventsCountPosition
        setEventsCount(date)
        if (currentScrollingState == SCROLL_STATE_IDLE) {
            val newPosition = positionOfDate(date, currentDateScrollLimit) + 1
            layoutManager.scrollToPositionWithOffset(newPosition, 0)
        }
        val dayInfo = dayLegendAdapter.data[currentLegendDate]
        firstDay.unwantedVacationDraw = getUnwantedCurrentDay(dayInfo)
    }

    private fun getUnwantedCurrentDay(dayInfo: Day?): UnwantedVacationDraw {
        val isUsualDay = dayInfo?.type in listOf(EventType.DAY_OFF, EventType.WORKDAY)
        val isUnwantedVacation = dayInfo?.isUnwantedVacDay

        val drawFill = isUnwantedVacation == true && isUsualDay

        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisible = layoutManager.findViewByPosition(firstVisiblePosition) as? LegendDayView
        return if (isCurrentDayAndFirstDayUnwanted(firstVisible?.date ?: currentLegendDate.plusDays(1))) {
            UnwantedVacationDraw.MULTI(drawFill)
        } else if (dayLegendAdapter.data[currentLegendDate]?.isUnwantedVacDay == true) {
            UnwantedVacationDraw.SINGLE(drawFill)
        } else {
            UnwantedVacationDraw.NONE
        }
    }

    private fun setEventsCount(date: LocalDate) {
        val info = dayLegendAdapter.data[date]
        firstDay.eventsCountText = if (info != null && info.payloadEventsCount > 0) info.payloadEventsCount.toString() else ""
    }

    @SuppressLint("DefaultLocale")
    private fun firstItemSetText(date: LocalDate, afterScroll: Boolean = false) {
        val oldWidth = firstDay.getRequiredWidth()

        firstDay.dateText = dayOfMonthFormat.format(date.toDate())
        firstDay.monthText = (if (date.year == currentYear) {
            monthFormatWithoutYear.format(date.toDate()).run {
                if (length > 2 && !isTablet) substring(0, 3) else this
            }
        } else {
            monthFormat.format(date.toDate()).run {
                var dateString = this
                if (!isTablet) {
                    // некоторые месяцы сокращаются до 4 символов
                    dateString = if (contains(".")) {
                        val month = substringBefore(".")
                        month.substring(0, min(month.length, 3)) + substringAfter(".")
                    } else {
                        val month = substringBefore("'")
                        month.substring(0, min(month.length, 3)) + "'" + substringAfter("'")
                    }
                }
                dateString
            }
        }).decapitalize()

        firstDay.dayOfWeekText = dayFormat.format(date.toDate()).uppercase()

        fillCurrentDayLegend()

        //если изменится ширина first_day - анимируем это изменение
        val newWidth = firstDay.getRequiredWidth()
        if (oldWidth != newWidth && oldWidth > 0) {
            if (currentScrollingState != SCROLL_STATE_IDLE || afterScroll) {
                sizeAnimation.apply {
                    cancel()
                    reset()
                    setParams(oldWidth, newWidth)
                    firstDay.startAnimation(this)
                }
            } else {
                firstDay.layoutParams.width = newWidth
                firstDay.requestLayout()
            }
        } else {
            firstDay.requestLayout()
        }
    }

    private fun fillCurrentDayLegend(){
        val dayInfo = dayLegendAdapter.data[currentLegendDate]
        val defaultBackgroundColor = if (currentLegendDate.dayOfWeek > DateTimeConstants.FRIDAY) {
            colorsProvider.colorDayOff
        } else {
            colorsProvider.colorWorkday
        }
        val backgroundColor = if (dayInfo == null) {
            defaultBackgroundColor
        } else {
            val isVacationOnHoliday = dayInfo.isVacationOnHoliday
            when {
                dayInfo.type == EventType.NOT_HIRED -> colorsProvider.notHired
                isVacationOnHoliday -> {
                    hatchDrawable.color = colorsProvider.colorCrossedVacation
                    colorsProvider.colorHolidayCrossVac
                }

                dayInfo.backgroundColor.isNotEmpty() -> dayInfo.parseBackgroundColor
                else -> dayInfo.type.resolveBackgroundColorWithColorsProvider(colorsProvider)
            }
        }
        colorDrawable.color = backgroundColor
        hatchDrawable.alpha = if (dayInfo?.isVacationOnHoliday == true) MAX_ALPHA else Color.TRANSPARENT
        firstDay.unwantedVacationDraw = getUnwantedCurrentDay(dayInfo)
        firstDay.dayOfWeekTextColor =
            when {
                dayInfo?.isHoliday == true -> colorHoliday
                currentLegendDate.dayOfWeek > 5 -> colorHoliday
                else -> colorWorkday
            }
        setEventsCount(currentLegendDate)
    }

    @SuppressLint("CheckResult")
    private fun initScrollLegend() {
        if (!isInEditMode) {
            scrollLegend.debounce(SCROLL_LEGEND_DEBOUNCE_TIME, TimeUnit.MILLISECONDS, Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { day ->
                    onTypeSelectDay?.invoke(ModeSelectedDay.SCROLL)
                    firstItemSetText(day, true)
                    onDayClicked?.invoke(day)
                    fillCurrentDayLegend()
                }
        }
    }

    private class WidthResizeAnimation(private val view: View) : Animation() {

        // distance between start and end height
        private var deltaWidth = 0
        private var startWidth = 0

        init {
            duration = 300
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            view.layoutParams.width = (startWidth + deltaWidth * interpolatedTime).toInt()
            view.requestLayout()
        }

        fun setParams(startWidth: Int, endWidth: Int) {
            this.startWidth = startWidth
            this.deltaWidth = endWidth - startWidth
        }

        override fun willChangeBounds() = true
    }
}

