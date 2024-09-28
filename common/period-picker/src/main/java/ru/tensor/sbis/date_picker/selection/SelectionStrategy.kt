package ru.tensor.sbis.date_picker.selection

import ru.tensor.sbis.date_picker.*
import ru.tensor.sbis.date_picker.free.items.HistoryPeriod
import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.HalfYear
import ru.tensor.sbis.date_picker.items.NamedItemVM
import ru.tensor.sbis.date_picker.items.Quarter
import java.util.*

/**
 * @author mb.kruglova
 */
abstract class SelectionStrategy(
    period: Period,
    protected val storage: CalendarVmStorage,
    private val periodHelper: PeriodHelper,
    private val selectedPeriodChanged: (Period) -> Unit,
    private val selectedNamedItemChanged: (NamedItemVM?) -> Unit
) {

    protected var selectedPeriod = period
        set(value) {
            field = value
            selectedPeriodChanged(value)
        }

    private var selectedNamedItem: NamedItemVM? = null
        set(value) {
            field = value
            selectedNamedItemChanged(value)
        }

    abstract fun getItemClickedPeriod(key: PeriodsVMKey, selected: Int, itemPeriod: Period): Period
    open val itemClickedAction: (() -> Unit)? = null
    open val dateSelection = false

    /**
     * Обработчик нажатия на заголовок года
     */
    open val yearClicked: ((PeriodsVMKey, NamedItemVM) -> Unit)? = { key, namedItem ->
        deselectMonths()
        selectedPeriod = Period.fromYear(key.year)
        selectedNamedItem = namedItem
        selectMonths()
        itemClickedAction?.invoke()
    }

    /**
     * Обработчик нажатия на полугодие
     */
    open val halfYearClicked: ((PeriodsVMKey, Int, NamedItemVM) -> Unit)? = { key, halfYear, namedItem ->
        deselectMonths()
        selectedPeriod = Period.fromHalfYear(key.year, halfYear)
        selectedNamedItem = namedItem
        selectMonths()
        itemClickedAction?.invoke()
    }

    /**
     * Обработчик нажатия на квартал
     */
    open val quarterClicked: ((PeriodsVMKey, Int, NamedItemVM) -> Unit)? = { key, quarter, namedItem ->
        deselectMonths()
        selectedPeriod = Period.fromQuarter(key.year, quarter)
        selectedNamedItem = namedItem
        selectMonths()
        itemClickedAction?.invoke()
    }

    /**
     * Обработчик нажатия на месяц
     */
    open val monthClicked: ((PeriodsVMKey, Int) -> Unit)? = { key, month ->
        deselectMonths()
        selectedNamedItem = null
        selectedPeriod = getItemClickedPeriod(key, month, Period.fromMonth(key.year, month))
        selectMonths()
        itemClickedAction?.invoke()
    }

    /**
     * Отметка диапазона месяцев в календарной сетке режима "Год" согласно выбранного пользователем периода
     */
    private fun selectMonths() {
        storage.selectMonths(selectedPeriod)
        selectYearQuarter()
    }

    /**
     * Выделяет текущий квартал/полугодие если период входит в их рамки
     */
    private fun selectYearQuarter() {
        val isFullHalfYear = periodHelper.isFullHalfYear(selectedPeriod)
        if (isFullHalfYear.first) {
            storage.selectHalfYear(HalfYear(selectedPeriod.yearFrom, isFullHalfYear.second))
        } else {
            storage.deselectHalfYears()
            val isFullQuarter = periodHelper.isFullQuarter(selectedPeriod)
            if (isFullQuarter.first) {
                storage.selectQuarter(Quarter(selectedPeriod.yearFrom, isFullQuarter.second))
            } else {
                storage.deselectQuarters()
            }
        }
    }

    /**
     * Сброс диапазона месяцев в календарной сетке режима "Год"
     */
    private fun deselectMonths() {
        storage.deselectMonths(selectedPeriod)
    }

    /**
     * Обработчик нажатия на заголовок месяца
     */
    open val monthLabelClicked: ((PeriodsVMKey, NamedItemVM, Calendar, Calendar) -> Unit)? =
        { key, namedItem, min, max ->
            deselectDays()
            val dayFrom = getStartDayOfMonth(key.year, key.month, min)
            val dayTo = getEndDayOfMonth(key.year, key.month, max)
            selectedPeriod = Period.fromMonth(key.year, key.month, dayFrom, dayTo)
            selectedNamedItem = namedItem
            selectDays()
            itemClickedAction?.invoke()
        }

    /**
     * Обработчик нажатия на день месяца
     */
    open val dayClicked: ((PeriodsVMKey, Int) -> Unit)? = { key, day ->
        deselectDays()
        selectedNamedItem = null
        selectedPeriod = getItemClickedPeriod(key, day, Period.fromDay(key.year, key.month, day))
        selectDays()
        itemClickedAction?.invoke()
    }

    /**
     * Отметка диапазона дней в календарной сетке режима "Месяц" согласно выбранного пользователем периода
     */
    private fun selectDays() {
        storage.selectDays(selectedPeriod, dateSelection)
    }

    /**
     * Сброс диапазона дней в календарной сетке режима "Месяц"
     */
    private fun deselectDays() {
        storage.deselectDays(selectedPeriod)
    }

    /**
     * Выделение заголовков года, полугодия, квартала
     */
    fun selectYearPeriods() {
        selectedNamedItem = periodHelper.getLongPeriodVmFromStorage(storage, selectedPeriod)
    }

    /**
     * Выделение заголовка месяца
     */
    fun selectMonthPeriods() {
        selectedNamedItem = periodHelper.getMonthPeriodVmFromStorage(storage, selectedPeriod)
    }

    val recentPeriodClicked: (HistoryPeriod) -> Unit = { historyPeriod ->
        selectedPeriod = Period.fromHistoryPeriod(historyPeriod)
    }
}