package ru.tensor.sbis.calendar.date.view.year

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.parcelize.Parcelize
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.view.DatePickerBaseProperties
import ru.tensor.sbis.calendar.date.view.EmptySelectedDayDrawable
import ru.tensor.sbis.calendar.date.view.selector.MultipleSelector
import ru.tensor.sbis.calendar.date.view.selector.NoSelector
import ru.tensor.sbis.calendar.date.view.selector.OneSelector
import ru.tensor.sbis.calendar.date.view.selector.Selector
import ru.tensor.sbis.design.utils.delegatePropertyMT
import java.util.Calendar
import kotlin.math.abs
import kotlin.properties.Delegates

/**
 * Бесконечно скроллящийся календарь
 */
internal class YearView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    /**
     * Рисовать фон у рядовых дней (рабочий, выходной)
     */
    var drawBackgroundUsualDay: Boolean = true
        set(value) {
            field = value
            yearAdapter.drawBackgroundUsualDay = value
        }

    /**
     * Листенер выбранных дат
     */
    var onSelectionChangedListener: (Pair<LocalDate?, LocalDate?>) -> Unit = { }

    /**
     * Показать на сетке календаря маркер
     */
    var marker: Marker? = null
        set(value) {
            field = value
            if (value == null && markerDecoration != null) {
                removeItemDecoration(markerDecoration!!)
                markerDecoration = null
            } else if (value != null) {
                if (markerDecoration == null) {
                    markerDecoration = MarkerDecoration(context)
                    addItemDecoration(markerDecoration!!)
                }
                markerDecoration!!.color = marker!!.color
                markerDecoration!!.date = marker!!.date
            }
        }

    private var markerDecoration: MarkerDecoration? = null

    /**
     * Адаптер
     */
    internal val yearAdapter = YearAdapter(drawBackgroundUsualDay).also {
        it.preCountSpan()
        it.stateRestorationPolicy = Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        adapter = it
    }

    /**
     * Выбранные даты
     */
    var selectedDates by delegatePropertyMT(yearAdapter::selectedDates)

    /**
     * Настройки (цвет заливки и прочее) для передачи в адаптер     *
     */
    var baseProperties = DatePickerBaseProperties(selectedDayDrawable = EmptySelectedDayDrawable())
        set(value) {
            field = value
            mode = value.mode
            yearAdapter.selectedDayDrawable = value.selectedDayDrawable
        }

    /**
     * Режим выбора дат
     */
    var mode by Delegates.observable(DatePickerBaseProperties.NO_SELECTOR) { _, _, newValue ->
        val oldSelector = selector

        selector = when (newValue) {
            DatePickerBaseProperties.ONE_SELECTOR      -> OneSelector(yearAdapter) { onSelectionChangedListener.invoke(it) }
            DatePickerBaseProperties.MULTIPLE_SELECTOR -> MultipleSelector(yearAdapter) { onSelectionChangedListener.invoke(it) }
            else                                       -> NoSelector()
        }
        selector.isEnabled = oldSelector.isEnabled
        selector.selectedDates = yearAdapter.selector.selectedDates
        yearAdapter.selector = selector
    }

    internal var selector: Selector = NoSelector()

    companion object {
        private const val DAYS_HOLDER_POOL_MAX = 100
    }

    private val layoutManager: GridLayoutManager = object : GridLayoutManager(context, Calendar.DAY_OF_WEEK) {
        init {
            spanSizeLookup = object : SpanSizeLookup() {
                /**
                 *  Календарь - это сетка, но название месяца тоже является ячейкой.
                 *  На одной строке, очевидно, рисуется 7 элементов.
                 *  При этом, если мы рисуем сетку календаря, элемент занимает одну ячейку, а если
                 *  название месяца - то элемент занимает все семь позиций (всю ширину).
                 */
                override fun getSpanSize(position: Int): Int {
                    return when (yearAdapter.getItemViewType(position) == YearAdapter.VIEW_TYPE_MONTH_NAME) {
                        true -> Calendar.DAY_OF_WEEK
                        else -> 1
                    }
                }
            }
        }
    }

    init {
        super.setLayoutManager(layoutManager)
        (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        // Что бы повторно не тратить время на создание холдеров.
        recycledViewPool.setMaxRecycledViews(YearAdapter.VIEW_TYPE_DAY, DAYS_HOLDER_POOL_MAX)
        setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate(this))
    }

    /**
     * Добавить декорацию нежелательных дней
     */
    fun addUnwantedDecoration() {
        addItemDecoration(UnwantedVacation(context), 0)
    }

    /**
     * Позиция адаптера которая сверху видна
     */
    fun getTopVisibleMonth(): Int = layoutManager.findFirstVisibleItemPosition()

    /**
     * Месяц по позиции [position]
     */
    fun monthByPosition(position: Int) = yearAdapter.monthByPosition(position).toMonthLocaleDate()

    /**
     * Отлистывание к дате [date], если установлен флаг [toMonth] то к началу месяца
     */
    fun scrollTo(date: LocalDate, toMonth: Boolean) {
        val position = if (!toMonth) yearAdapter.indexOfDay(date) else yearAdapter.indexOfMonth(date)
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()
        val isAnimation = abs(position - first) < 60
        when {
            first == NO_POSITION && last == NO_POSITION -> {
                layoutManager.scrollToPositionWithOffset(
                    yearAdapter.indexOfDay(date),
                    resources.getDimensionPixelSize(R.dimen.calendar_year_month_month_height)
                )
            }
            position < first -> {
                val positionScroll = if (date.isFirstWeekOfMonth()) yearAdapter.indexOfMonth(date) else position - 8
                if (isAnimation) smoothScrollToPosition(positionScroll) else scrollToPosition(positionScroll)
            }
            position > last  -> {
                if (isAnimation)
                    smoothScrollToPosition(position + (last - first) / 2)
                else
                    scrollToPosition(position + (last - first) / 2)
            }
            else             -> {
                // отлистывание так что бы ячейка была видна с отступом от верха равным высоте ячейки
                layoutManager.findViewByPosition(position)?.let {
                    val heightTop = if (date.isFirstWeekOfMonth()) layoutManager.findViewByPosition(yearAdapter.indexOfMonth(date))?.height else null
                    smoothScrollBy(0, it.top - (heightTop ?: it.height))
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()!!, marker)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.state)
            marker = state.marker
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun LocalDate.isFirstWeekOfMonth() = dayOfMonth - dayOfWeek <= 0

    /**
     * Хранение состояния адаптера
     * @param state - состояние адаптера
     * @param marker - данные маркера
     */
    @Parcelize
    internal data class SavedState(
        val state: Parcelable,
        val marker: Marker? = null
    ): Parcelable
}
