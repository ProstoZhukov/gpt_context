package ru.tensor.sbis.calendar.date.view.year

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.parcelize.Parcelize
import org.joda.time.Days
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.calendar.date.utils.getCalendarDateViewStyleId
import ru.tensor.sbis.calendar.date.utils.plusAssign
import ru.tensor.sbis.calendar.date.view.DatePickerBaseProperties
import ru.tensor.sbis.calendar.date.view.DatePickerLifeData
import ru.tensor.sbis.calendar.date.view.EmptySelectedDayDrawable
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Скролящейся календарь на +/-10 лет от текущей даты. Задается константой в адаптере [YearAdapter.YEARS_THRESHOLD]
 */
class YearPicker
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(
    context,
    attrs,
    defStyle
) {
    /**
     * Показать маркер
     */
    var marker: Marker?
        get() = yearView.marker
        set(value) {
            yearView.marker = value
        }

    /**
     * Выбранные даты
     */
    var selectedDates: Pair<LocalDate?, LocalDate?>
        get() = yearView.selectedDates
        set(value) {
            yearView.selectedDates = value
        }

    /**
     * Способ выбора даты или дат
     */
    var baseProperties: DatePickerBaseProperties
        get() = yearView.baseProperties
        set(value) {
            yearView.baseProperties = value
        }

    private var isTabletMode: Boolean = false
        @MainThread
        set(value) {
            field = value
            invalidateView()
        }

    private val shadow: View
    private val topBar: View
    internal val yearView: YearView
    private var needAddUnwanted: Boolean = false
    var scrollFeatureEnable = true

    var pickerLifeData: DatePickerLifeData? = null
        set(value) {
            value?.apply {
                disposer.clear()
                scrollSubscribe()

                // Выбранные даты
                val dates = selectedDates.observable
                    .debounce(25, TimeUnit.MILLISECONDS)
                    .filter { it.first != null }
                    .cache()

                // Первый подскрол при создании или открытии документа
                disposer += Observable.merge(
                    dates
                        .map { Pair(it.first!!, false) }
                        .take(1),
                    month.observable.map { Pair(it, true) }
                        .take(1)
                ).scan { t1, t2 ->
                    if (!t1.second) t1 else t2
                }.debounce(50, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .take(1)
                    .subscribe { (it, showMonth) ->
                        if (scrollFeatureEnable) {
                            val weekYear = it.weekOfWeekyear
                            val firstMonthWeek = it.withDayOfMonth(1).weekOfWeekyear
                            yearView.stopScroll()
                            if (weekYear != firstMonthWeek && !showMonth) {
                                yearView.scrollToPosition(
                                    yearView.yearAdapter.indexOfDay(
                                        it.withDayOfMonth(max(it.dayOfMonth - 7, 1))
                                    )
                                )
                            } else {
                                yearView.scrollToPosition(yearView.yearAdapter.indexOfMonth(it))
                            }
                        }
                        yearView.isVisible = true
                    }

                // Обновить раскраску ячеек
                disposer += dates
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it?.let {
                            this@YearPicker.selectedDates = it
                            val count = it.second?.let { end -> Days.daysBetween(it.first!!, end).days } ?: 1
                            yearView.yearAdapter
                                .notifyItemRangeChanged(yearView.yearAdapter.indexOfDay(it.first!!), count)
                        }
                    }

                // Подскрол к дате когда пользователь выбирает две даты
                disposer += dates
                    .skip(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        ifRequireScrollToDates(it!!)
                    }

                yearView.onSelectionChangedListener = {
                    if (selectedDates.value != it) {
                        selectedDates.onNext(it)
                        selectedDatesFunc?.invoke(it)
                        selectedDatesWithNoTimePickerSubscribe.onNext(it)
                    }
                }

                disposer += data
                    .filter { it.isNotEmpty() }
                    .map {
                        val firstVisible = (yearView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                        val lastVisible = (yearView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                        yearView.yearAdapter.updateData(it, firstVisible, lastVisible)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it.notifyRecycler) {
                            it.list?.forEach { value ->
                                if (value.second) {
                                    yearView.yearAdapter.notifyItemChanged(value.first)
                                }
                            }
                        }
                    }
            }
            field = value
        }

    private val disposer = CompositeDisposable()

    private val scrolled = PublishSubject.create<Int>()

    init {
        context.theme.applyStyle(context.getCalendarDateViewStyleId(), true)
        inflate(context, R.layout.year_picker, this)
        shadow = findViewById(R.id.shadow)
        topBar = findViewById(R.id.top_bar)
        yearView = findViewById(R.id.year_view)
        yearView.overScrollMode = View.OVER_SCROLL_NEVER
        yearView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val topVisible = yearView.getTopVisibleMonth()
                scrolled.onNext(topVisible)
            }
        })
        context.withStyledAttributes(attrs, R.styleable.YearPicker) {
            isTabletMode = getBoolean(R.styleable.YearPicker_isTabletMode, false)
        }
        baseProperties = DatePickerBaseProperties(selectedDayDrawable = EmptySelectedDayDrawable())
    }

    fun setFirstDate(firstDate: LocalDate, lastDate: LocalDate) {
        yearView.yearAdapter.setFirstDate(firstDate, lastDate)
    }

    private fun invalidateView() {
        topBar.layoutParams.also {
            it as ConstraintLayout.LayoutParams
            it.height =
                context.resources.getDimensionPixelSize(if (isTabletMode) R.dimen.top_bar_tablet_height else R.dimen.top_bar_phone_height)
            it.marginStart = if (isTabletMode) context.resources.getDimensionPixelSize(R.dimen.tablet_margin) else 0
            it.marginEnd = if (isTabletMode) context.resources.getDimensionPixelSize(R.dimen.tablet_margin) else 0
            topBar.layoutParams = it
        }
        yearView.layoutParams.also {
            it as ConstraintLayout.LayoutParams
            it.marginStart = if (isTabletMode) context.resources.getDimensionPixelSize(R.dimen.tablet_margin) else 0
            it.marginEnd = if (isTabletMode) context.resources.getDimensionPixelSize(R.dimen.tablet_margin) else 0
            yearView.layoutParams = it
        }
        shadow.isVisible = isTabletMode
    }

    /**
     * Добавить декорацию нежелательных дней
     */
    fun addUnwantedDecoration() {
        yearView.addUnwantedDecoration()
        yearView.drawBackgroundUsualDay = false
        needAddUnwanted = true
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        yearView.selector.isEnabled = enabled
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            scrollSubscribe()
        }
    }

    private fun scrollSubscribe() {
        disposer += scrolled.observeOn(Schedulers.newThread()).subscribe { top ->
            val topVisibleDate = yearView.monthByPosition(top)
            pickerLifeData?.month?.onNext(topVisibleDate)
        }
    }

//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        disposer.clear()
//    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState(), needAddUnwanted)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.state)
            if (state.addUnwanted) {
                addUnwantedDecoration()
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    @MainThread
    fun scrollToDate(date: LocalDate) {
        yearView.scrollTo(date, !yearView.isVisible)
    }

    private fun ifRequireScrollToDates(dates: Pair<LocalDate?, LocalDate?>) {
        if (dates.first != null && dates.second != null) {
            yearView.scrollTo(dates.first!!, !yearView.isVisible)
            yearView.isVisible = true
        }
    }

    /**
     * Хранение состояния адаптера
     * @param state - состояние адаптера
     * @param data - данные адаптера
     */
    @Parcelize
    internal data class SavedState(
        val state: Parcelable?,
        val addUnwanted: Boolean
    ): Parcelable
}