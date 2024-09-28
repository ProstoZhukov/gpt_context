package ru.tensor.sbis.design.cylinder.picker.time

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.joda.time.LocalDateTime
import org.joda.time.ReadablePeriod
import ru.tensor.sbis.design.cylinder.picker.R
import ru.tensor.sbis.design.cylinder.picker.plusAssign
import ru.tensor.sbis.design.cylinder.picker.value.CENTER_OFFSET
import java.util.concurrent.TimeUnit

/**
 * @author Subbotenko Dmitry
 */

class CylinderDateTimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    companion object {
        private const val PICKER_VALUE_CHANGE_DEBOUNCE_INTERVAL = 150L
    }

    private val disposer = CompositeDisposable()
    private lateinit var liveData: LiveData

    private val linearLayoutManager = object : LinearLayoutManager(context) {
        override fun canScrollVertically(): Boolean {
            return super.canScrollVertically() && scrollingEnabled
        }
    }
        .also {
            layoutManager = it
        }

    private lateinit var viewType: CylinderViewType
    private var minutesStep: Int = 5
    private val timePickerAdapter by lazy { CylinderDateTimePickerAdapter(viewType, minutesStep).also { adapter = it } }
    private var stopScrollListener = false
    private var scrollingEnabled = true

    private var scrollingSpeedScale = ScrollingSpeed.DEFAULT.scale

    init {
        LinearSnapHelper().attachToRecyclerView(this)
        context.withStyledAttributes(attrs, R.styleable.CylinderDateTimePicker) {
            val scaleOrdinal = getInt(
                R.styleable.CylinderDateTimePicker_CylinderDateTimePicker_scrollSpeed,
                ScrollingSpeed.DEFAULT.ordinal,
            )
            scrollingSpeedScale = ScrollingSpeed.values()[scaleOrdinal].scale
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        return super.fling(velocityX, (velocityY * scrollingSpeedScale).toInt())
    }

    fun init(liveData: LiveData, viewType: CylinderViewType, minutesStep: Int = 5) {
        this.minutesStep = minutesStep
        this.viewType = viewType
        this.liveData = liveData

        liveData.cylinder = -1

        timePickerAdapter.initDate = liveData.date
        linearLayoutManager.scrollToPositionWithOffset(
            timePickerAdapter.getPositionForDate(liveData.date) - CENTER_OFFSET,
            0
        )
        addOnScrollListener(scrollListener)

        if (!isInEditMode) {
            disposer += liveData.scrollEnabled
                .throttleLast(PICKER_VALUE_CHANGE_DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    scrollingEnabled = it
                }

            disposer += liveData.showMidnightAs24
                .distinctUntilChanged()
                .throttleLast(PICKER_VALUE_CHANGE_DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    timePickerAdapter.showMidnightAs24 = it
                    timePickerAdapter.notifyDataSetChanged()
                }

            disposer += liveData.dateChangeObservable
                .throttleLast(PICKER_VALUE_CHANGE_DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .skip(1)
                /*
             При выставлении значения барабана при gервой загрузке с помощью метода scrollToPositionWithOffset
             ресайклер вносит значение скролла в pendingPosition, в результате чего  stopScrollListener = false происходит
             раньше, чем барабан начинает вращение.

             в результате onScrolled получает первое значение и сбрасывает счетчики барабанов на минимум.

             skip(1) симптоматически фиксит проблему, но это не значит, что она не повторится впредь.

             Для полного фикса нужен кастомный layout manager, который сможет правильно делать scrollToPositionWithOffset без вызова onScrolled()
             */
                .filter {
                    val position = currentPosition()
                    val date = getDateForPickerPosition(position)
                    position != NO_POSITION && when (viewType) {
                        CylinderViewType.DAY -> it.dayOfYear().roundFloorCopy() != date.dayOfYear().roundFloorCopy()
                        CylinderViewType.HOUR -> it.hourOfDay().roundFloorCopy() != date.hourOfDay().roundFloorCopy()
                        CylinderViewType.MINUTE -> it.minuteOfHour().roundFloorCopy() != date.minuteOfHour()
                            .roundFloorCopy()
                    }
                }
                .subscribe {
                    stopScrollListener = true
                    stopScroll()
                    linearLayoutManager
                        .scrollToPositionWithOffset(timePickerAdapter.getPositionForDate(it) - CENTER_OFFSET, 0)
                    stopScrollListener = false
                }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode) {
            disposer.clear()
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        private val changed = PublishSubject.create<Int>()

        init {
            if (!isInEditMode) {
                disposer += changed.throttleLast(PICKER_VALUE_CHANGE_DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it == SCROLL_STATE_IDLE) {
                            val position = currentPosition()
                            val newDate = liveData.getDateTime(this@CylinderDateTimePicker, position)
                            liveData.cylinder = id
                            liveData.date = newDate
                        }
                    }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            changed.onNext(newState)
        }
    }

    fun getDateForPosition(position: Int): ReadablePeriod = timePickerAdapter.getDateForPosition(position)

    private fun currentPosition(): Int {
        val first = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        return if (first == NO_POSITION) NO_POSITION else first + CENTER_OFFSET
    }

    private fun getDateForPickerPosition(position: Int): LocalDateTime {
        val dateFromAdapter = timePickerAdapter.getDateForPosition(position)
        return resolveAdapterDateWithLiveDataDate(dateFromAdapter, liveData.date)
    }

    fun resolveAdapterDateWithLiveDataDate(
        period: ReadablePeriod,
        currentDate: LocalDateTime,
        maxDate: LocalDateTime = LocalDateTime.now().plusYears(1) // в пределах одного дня
    ): LocalDateTime = when (viewType) {
        CylinderViewType.DAY ->
            timePickerAdapter.initDate
                .plus(period)
                .withHourOfDay(currentDate.hourOfDay)
                .withMinuteOfHour(currentDate.minuteOfHour)

        CylinderViewType.HOUR -> currentDate.withHourOfDay(0).plus(period)
        CylinderViewType.MINUTE -> currentDate.withMinuteOfHour(0).plus(period)
    }.run {
        if (this > maxDate) maxDate else this
    }

    interface LiveData {
        var cylinder: Int
        val dateChangeObservable: Observable<LocalDateTime>
        var date: LocalDateTime
        val timeBoundsObservable: Observable<Pair<LocalDateTime?, LocalDateTime?>>
        fun getDateTime(picker: CylinderDateTimePicker, position: Int): LocalDateTime
        val scrollEnabled: Observable<Boolean>
        val showMidnightAs24: Observable<Boolean>
    }

    private enum class ScrollingSpeed(val scale: Float) {
        DEFAULT(1f),
        HALF_SPEED(0.5f),
        QUARTER_SPEED(0.25f),
    }
}
