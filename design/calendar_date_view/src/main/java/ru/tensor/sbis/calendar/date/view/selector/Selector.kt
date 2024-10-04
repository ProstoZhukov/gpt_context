package ru.tensor.sbis.calendar.date.view.selector

import androidx.annotation.MainThread
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Period
import ru.tensor.sbis.calendar.date.data.*
import ru.tensor.sbis.calendar.date.data.BOTTOM
import ru.tensor.sbis.calendar.date.data.LEFT
import ru.tensor.sbis.calendar.date.data.RIGHT
import ru.tensor.sbis.calendar.date.data.SelectedType.*
import ru.tensor.sbis.calendar.date.data.TOP

abstract class Selector {
    @MainThread
    abstract fun onClick(date: LocalDate)
    abstract var selectedDates: Pair<LocalDate?, LocalDate?>
    @MainThread
    abstract fun getSelectedType(date: LocalDate): SelectedType
    var isEnabled: Boolean = true
}

internal interface AdapterNotifier {
    fun notifyDataSetChanged()
}

internal class NoSelector : Selector() {
    override fun getSelectedType(date: LocalDate): SelectedType = NONE()
    override fun onClick(date: LocalDate) = Unit
    override var selectedDates: Pair<LocalDate?, LocalDate?> = Pair(null, null)
}

internal class OneSelector(
    private val adapter: AdapterNotifier,
    private inline val onSelectionChangedListener: (Pair<LocalDate?, LocalDate?>) -> Unit
) : Selector() {
    private var selectedDay: LocalDate? = null
    override fun getSelectedType(date: LocalDate): SelectedType =
        if (selectedDay == date) SINGLE() else NONE()

    override fun onClick(date: LocalDate) {
        if (!isEnabled) return

        selectedDay = date
        adapter.notifyDataSetChanged()
        onSelectionChangedListener.invoke(selectedDates)
    }

    override var selectedDates
        get() = Pair(selectedDay, null as LocalDate?)
        set(value) {
            selectedDay = value.first
            adapter.notifyDataSetChanged()
        }
}

/** Селектор для случая, когда можно выбирать только дату начала периода, а дата окончания подставляется извне */
internal class OneSelectorWithSeveralDays(
    adapter: AdapterNotifier,
    onSelectionChangedListener: (Pair<LocalDate?, LocalDate?>) -> Unit
): MultipleSelectorBase(adapter, onSelectionChangedListener) {

    private var dates = Pair<LocalDate?, LocalDate?>(null, null)

    override val startDate: LocalDate?
        get() = dates.first

    override val endDate: LocalDate?
        get() = dates.second

    override fun initSelectedDates(value: Pair<LocalDate?, LocalDate?>) {
        dates = value
        adapter.notifyDataSetChanged()
    }

    override fun onClick(date: LocalDate) {
        if (!isEnabled) return

        val timeBetween = if (dates.first != null && dates.second != null) Period(dates.first, dates.second) else null
        dates = date to timeBetween?.let { date.plus(timeBetween) }

        adapter.notifyDataSetChanged()
        onSelectionChangedListener.invoke(selectedDates)
    }
}

internal class MultipleSelector(
    adapter: AdapterNotifier,
    onSelectionChangedListener: (Pair<LocalDate?, LocalDate?>) -> Unit
) : MultipleSelectorBase(adapter, onSelectionChangedListener) {

    private val dates = mutableListOf<LocalDate>()

    override val startDate: LocalDate?
        get() = if (dates.isNotEmpty()) {
            dates.minOrNull()
        } else {
            null
        }

    override val endDate: LocalDate?
        get() = if (dates.size > 1) {
            dates.maxOrNull()
        } else {
            null
        }

    override fun initSelectedDates(value: Pair<LocalDate?, LocalDate?>) {
        dates.clear()
        value.first?.also { dates.add(it) }
        value.second?.also { dates.add(it) }
        isDurationMoreWeek = if (startDate != null) Days.daysBetween(startDate, endDate ?: startDate).days > 6 else false
        adapter.notifyDataSetChanged()
    }

    override fun onClick(date: LocalDate) {
        if (!isEnabled) return

        if (dates.size > 1) {
            dates.clear()
        }
        dates.add(date)
        adapter.notifyDataSetChanged()
        onSelectionChangedListener.invoke(selectedDates)
    }
}

internal abstract class MultipleSelectorBase(
    protected val adapter: AdapterNotifier,
    protected inline val onSelectionChangedListener: (Pair<LocalDate?, LocalDate?>) -> Unit
) : Selector() {

    private val dates = mutableListOf<LocalDate>()

    protected abstract val startDate: LocalDate?

    protected abstract val endDate: LocalDate?

    protected var isDurationMoreWeek: Boolean = false

    override var selectedDates
        get() = Pair(startDate, endDate)
        set(value) {
            initSelectedDates(value)
        }

    protected abstract fun initSelectedDates(value: Pair<LocalDate?, LocalDate?>)

    private fun isSelected(date: LocalDate): Boolean {
        val startDate = startDate
        val endDate = endDate
        return when {
            startDate == null                 -> false
            date < startDate                  -> false
            endDate != null && date > endDate -> false
            else                              -> true
        }
    }

    override fun getSelectedType(date: LocalDate): SelectedType {
        return if (isSelected(date)) {
            fillBorderFlags(
                when {
                    startDate == endDate -> SINGLE()
                    date == startDate    -> FIRST()
                    endDate == null      -> NONE()
                    date == endDate      -> LAST()
                    else                 -> MIDDLE()
                }, date
            )
        } else NONE()
    }

    override fun onClick(date: LocalDate) {
        if (!isEnabled) return

        if (dates.size > 1) {
            dates.clear()
        }
        dates.add(date)
        adapter.notifyDataSetChanged()
        onSelectionChangedListener.invoke(selectedDates)
    }

    @Suppress("KotlinConstantConditions")
    private fun fillBorderFlags(res: SelectedType, date: LocalDate): SelectedType {
        var flags = 0
        // добавить нижний бордюр для всех дат у которых это последняя неделя месяца
        if (date.monthOfYear != date.plusDays(7).monthOfYear) {
            flags = flags.or(BOTTOM)
        }
        // добавить нижний бордюр для всех дат у которых это последняя неделя отпуска
        if (endDate == null || Days.daysBetween(date, endDate!!).days < 7) {
            flags = flags.or(BOTTOM)
        }
        // добавить верхний бордюр для всех дат у которых это первая неделя выбраного периода
        if (startDate != null && Days.daysBetween(startDate, date).days < 7) {
            flags = flags.or(TOP)
        }
        // добавить верхний бордюр для всех дат у которых это первая неделя месяца
        if (startDate != null && date.dayOfMonth <= 7) {
            flags = flags.or(TOP)
        }
        // добавить левый бордюр для всех дат у которых это первая неделя выбраного периода и длительность больше недели
        if (date.dayOfWeek == 1 && isDurationMoreWeek) {
            flags = flags.or(LEFT)
        }
        // добавить правый бордюр для всех дат у которых это первая неделя выбраного периода и длительность больше недели
        if (date.dayOfWeek == 7 && isDurationMoreWeek) {
            flags = flags.or(RIGHT)
        }
        // добавить левый бордюр для первого дня периода
        if (date == startDate) {
            flags = flags.or(LEFT)
        }
        // добавить правый бордюр для последнего дня выбраного периода
        if (date == endDate) {
            flags = flags.or(RIGHT)
        }
        // Добавить пиксель, что бы не было разрыва линии
        if (date.plusDays(7) == endDate) {
            flags = flags.or(PIXEL_BR)
        }
        if (date.minusDays(7) == startDate) {
            flags = flags.or(PIXEL_TL)
        }
        res.flags = flags
        return res
    }
}