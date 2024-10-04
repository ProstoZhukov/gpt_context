package ru.tensor.sbis.design.period_picker.view.models

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.SbisPeriodPickerPlugin
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumPosition
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumSelection
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType.END
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType.STANDARD
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumType.START
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayItemModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.EmptyModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.MonthLabelModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.HalfYearModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.MonthModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.QuarterModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.YearLabelModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.YearModePeriodPickerModel
import ru.tensor.sbis.design.period_picker.view.utils.checkRangeBelonging
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.dayOfWeek
import ru.tensor.sbis.design.period_picker.view.utils.endQuarterMonths
import ru.tensor.sbis.design.period_picker.view.utils.firstDayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.firstMonthOfYear
import ru.tensor.sbis.design.period_picker.view.utils.getDayKey
import ru.tensor.sbis.design.period_picker.view.utils.getEndDayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.getEndMonthOfYear
import ru.tensor.sbis.design.period_picker.view.utils.getFormattedMonthLabel
import ru.tensor.sbis.design.period_picker.view.utils.getMonthKey
import ru.tensor.sbis.design.period_picker.view.utils.getPlacement
import ru.tensor.sbis.design.period_picker.view.utils.getStartDayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.getStartMonthOfYear
import ru.tensor.sbis.design.period_picker.view.utils.getType
import ru.tensor.sbis.design.period_picker.view.utils.getYearKey
import ru.tensor.sbis.design.period_picker.view.utils.halfYearMultiplicity
import ru.tensor.sbis.design.period_picker.view.utils.halfYearRange
import ru.tensor.sbis.design.period_picker.view.utils.halfYearStep
import ru.tensor.sbis.design.period_picker.view.utils.isCurrentYear
import ru.tensor.sbis.design.period_picker.view.utils.isMonday
import ru.tensor.sbis.design.period_picker.view.utils.isSunday
import ru.tensor.sbis.design.period_picker.view.utils.lastDayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.lastMonthOfYear
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.monthRange
import ru.tensor.sbis.design.period_picker.view.utils.monthStep
import ru.tensor.sbis.design.period_picker.view.utils.quarter1Range
import ru.tensor.sbis.design.period_picker.view.utils.quarter4Range
import ru.tensor.sbis.design.period_picker.view.utils.quarterMultiplicity
import ru.tensor.sbis.design.period_picker.view.utils.quarterRange
import ru.tensor.sbis.design.period_picker.view.utils.quarterStep
import ru.tensor.sbis.design.period_picker.view.utils.rangeTo
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.period_picker.view.utils.setHalfYearVerticalPlacement
import ru.tensor.sbis.design.period_picker.view.utils.startHalfYearMonths
import ru.tensor.sbis.design.period_picker.view.utils.startQuarterMonths
import ru.tensor.sbis.design.period_picker.view.utils.weekdays
import ru.tensor.sbis.design.period_picker.view.utils.year
import ru.tensor.sbis.design.period_picker.view.utils.yearStep
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.LinkedList
import ru.tensor.sbis.design.R as RDesign

/**
 * Структура - агрегатор элементов календарной сетки.
 * Упрощает доступ к конкретным элементам.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class CalendarStorage : Parcelable {

    /** Есть ли у календаря режим Год. */
    internal var hasYearMode = false

    /** Поле для доступа к дням по ключу. */
    private var days = HashMap<Calendar, DayModel>()

    /** Поле для доступа к меткам месяцев по ключу. */
    private var monthLabels = HashMap<CalendarStorageKey, MonthLabelModel>()

    /** Поле для выравнивания дней в месяцей. */
    private var monthDaysAligned = LinkedHashMap<CalendarStorageKey, Int>()

    /** Поле для доступа к месяцу по ключу. */
    private val months = HashMap<Calendar, MonthModel>()

    /** Поле для доступа к кварталу по ключу. */
    private val quarters = HashMap<Calendar, QuarterModel>()

    /** Поле для доступа к полугодию по ключу. */
    private val halfYears = HashMap<Calendar, HalfYearModel>()

    /** Поле для доступа к году по ключу. */
    private val years = HashMap<Calendar, YearLabelModel>()

    /** Поле для доступа к году по ключу. */
    private val yearLabels = HashMap<Calendar, YearLabelModel>()

    /** Сетка календаря в режиме Месяц. */
    var dayGrid = LinkedList<DayItemModel>()
        private set

    /** Сетка календаря в режиме Год, отвечающая за месяцы. */
    var monthGrid = LinkedList<MonthModel>()
        private set

    /** Сетка календаря в режиме Год, отвечающая за кварталы. */
    var quarterGrid = LinkedList<QuarterModel>()
        private set

    /** Сетка календаря в режиме Год, отвечающая за полугодия. */
    var halfYearGrid = LinkedList<HalfYearModel>()
        private set

    /** Сетка календаря в режиме Год, отвечающая за года. */
    var yearGrid = LinkedList<YearLabelModel>()
        private set

    /** Сетка календаря в режиме Год, отвечающая за года. */
    var yearLabelsGrid = LinkedList<YearLabelModel>()
        private set

    /**
     * Добавить новые данные для календаря в хранилище.
     */
    internal fun addDataToStorage(newData: CalendarStorage, addToEnd: Boolean): CalendarStorage {
        days.putAll(newData.days)
        monthLabels.putAll(newData.monthLabels)
        months.putAll(newData.months)
        quarters.putAll(newData.quarters)
        halfYears.putAll(newData.halfYears)
        years.putAll(newData.years)

        if (addToEnd) {
            dayGrid.addAll(newData.dayGrid)
            monthGrid.addAll(newData.monthGrid)
            quarterGrid.addAll(newData.quarterGrid)
            halfYearGrid.addAll(newData.halfYearGrid)
            yearGrid.addAll(newData.yearGrid)
            monthDaysAligned.putAll(newData.monthDaysAligned)
        } else {
            dayGrid.addAll(0, newData.dayGrid)
            monthGrid.addAll(0, newData.monthGrid)
            quarterGrid.addAll(0, newData.quarterGrid)
            halfYearGrid.addAll(0, newData.halfYearGrid)
            yearGrid.addAll(0, newData.yearGrid)
            val newMonthDaysAligned = newData.monthDaysAligned
            newMonthDaysAligned.putAll(monthDaysAligned)
            monthDaysAligned = newMonthDaysAligned
        }
        return this
    }

    /**
     * Отметить диапазон в календарной сетке согласно периоду, выбранным пользователем.
     */
    internal fun selectPeriod(dateFrom: Calendar?, dateTo: Calendar?) {
        if (dateFrom == null) return

        selectDays(dateFrom, dateTo)
        if (hasYearMode) selectQuantum(dateFrom, dateTo)

    }

    /**
     * Отметить диапазон в днях в календарной сетке согласно периоду, выбранным пользователем.
     */
    internal fun selectDays(dateFrom: Calendar, dateTo: Calendar?) {
        when {
            // Начальная дата меньше конечной.
            dateTo != null && dateFrom < dateTo -> selectRange(dateFrom, dateTo)
            // Начальная дата совпадает с конечной.
            dateFrom == dateTo -> selectDay(dateFrom)
            // Конечная дата еще не выбрана.
            dateTo == null -> selectDay(dateFrom, START)
            else -> Unit
        }
    }

    /**
     * Отметить диапазон в квантах в календарной сетке согласно периоду, выбранным пользователем.
     */
    internal fun selectQuantum(dateFrom: Calendar, dateTo: Calendar?) {
        if (dateTo == null) {
            selectMonth(dateFrom)
            return
        }

        if (!checkFirstLastDays(dateFrom, dateTo)) return

        val dateToKey = getMonthKey(dateTo.year, dateTo.month)

        when {
            dateFrom.year == dateTo.year && checkQuarter(dateFrom, dateTo) -> selectQuarter(dateFrom, dateToKey)
            dateFrom.year == dateTo.year && checkHalfYear(dateFrom, dateTo) -> selectHalfYear(dateFrom, dateToKey)
            checkYear(dateFrom, dateTo) -> selectYear(dateFrom, getYearKey(dateTo.year))
            else -> selectMonths(dateFrom, dateToKey)
        }
    }

    /**
     * Отметить диапазон в годах в календарной сетке согласно периоду, выбранным пользователем.
     */
    internal fun selectYear(dateFrom: Calendar, dateTo: Calendar?) {
        if (dateTo == null) {
            val yearVM = yearLabels[dateFrom]
            yearVM?.setSelection(
                type = START,
                position = QuantumPosition()
            )
        } else {
            val rangeYear = dateFrom.year..dateTo.year
            rangeYear.forEach { year ->
                val yearKey = getYearKey(year)
                val dateToKey = getYearKey(dateTo.year)
                val yearVM = yearLabels[yearKey]
                yearVM?.setSelection(
                    type = yearKey.getType(dateFrom, dateToKey, dateFrom.year == dateTo.year),
                    position = QuantumPosition(
                        left = yearKey != dateFrom,
                        top = false,
                        right = yearKey != dateToKey,
                        bottom = false
                    )
                )

                val rangeMonth = monthRange
                rangeMonth.forEach { month ->
                    val calendar = getMonthKey(year, month)
                    val monthsVM = months[calendar]
                    monthsVM?.setSelection(
                        type = STANDARD,
                        position = QuantumPosition(
                            left = !startQuarterMonths.contains(calendar.month),
                            top = calendar.month !in quarter1Range,
                            right = !endQuarterMonths.contains(calendar.month),
                            bottom = calendar.month !in quarter4Range
                        )
                    )
                }
            }
        }
    }

    /**
     * Сброс выбранного диапазона в календарной сетке.
     */
    internal fun deselectPeriod(dateFrom: Calendar?, dateTo: Calendar?): Boolean {
        return if (dateFrom != null && dateTo != null) {
            dateFrom.rangeTo(dateTo).forEach { calendar ->
                val dayVm = days[calendar.removeTime()]
                dayVm?.resetSelection()
            }
            if (hasYearMode) deselectQuantumPeriod(dateFrom, dateTo)
            true
        } else {
            false
        }
    }

    /**
     * Обновить пометку года.
     */
    internal fun updateMarkedYears(year: Int) {
        val prevMarkedYears = yearLabels.filterValues { it.isMarked }
        for ((k, _) in prevMarkedYears) {
            yearLabels[k]?.isMarked = false
        }

        val date = getYearKey(year)
        val yearVm = yearLabels[date.removeTime()]
        yearVm?.isMarked = true
    }

    /**
     * Отметить счётчики в календарной сетке.
     */
    internal fun setCounters(map: Map<Calendar, Int>) {
        map.forEach {
            val dayVm = days[it.key.removeTime()]
            dayVm?.counter = it.value.toString()
        }
    }

    /**
     * Сбросить счётчики в календарной сетке.
     */
    internal fun resetCounters(dates: Set<Calendar>) {
        dates.forEach {
            val dayVm = days[it.removeTime()]
            dayVm?.counter = ""
        }
    }

    /** Получить списки данных для календаря в режиме Год. */
    internal fun getYearModeCalendar(): List<Any> {
        val list = mutableListOf<Any>()
        yearGrid.forEach {
            val year = it.year
            list.add(year)
            val calendar = YearModePeriodPickerModel(
                year,
                getMonths(year),
                getQuarters(year),
                getHalfYear(year)
            )
            list.add(calendar)
        }

        return list
    }

    /** Отметить диапазон. */
    @VisibleForTesting
    internal fun selectRange(dateFrom: Calendar, dateTo: Calendar) {
        dateFrom.rangeTo(dateTo).forEach { date ->
            val calendar = getDayKey(date.year, date.month, date.dayOfMonth)
            val dayVm = days[calendar]
            dayVm?.setSelection(
                calendar.getDayType(dateFrom, dateTo),
                calendar.getDayPlacement(dateFrom, dateTo)
            )
        }
    }

    /** Отметить день. */
    @VisibleForTesting
    internal fun selectDay(dateFrom: Calendar, dayType: QuantumType = STANDARD) {
        val calendar = getDayKey(dateFrom.year, dateFrom.month, dateFrom.dayOfMonth)
        val dayVm = days[calendar]
        dayVm?.setSelection(dayType)
    }

    /** Отметить месяцы. */
    @VisibleForTesting
    internal fun selectMonths(dateFrom: Calendar, dateTo: Calendar) {
        dateFrom.rangeTo(dateTo).forEach { calendar ->
            val monthsVM = months[calendar]
            monthsVM?.setSelection(
                type = calendar.getType(dateFrom, dateTo),
                position = calendar.getPlacement(dateFrom, dateTo)
            )
        }
    }

    /** Отметить квартал. */
    @VisibleForTesting
    internal fun selectQuarter(dateFrom: Calendar, dateTo: Calendar) {
        val year = dateFrom.year
        val monthRange = dateFrom.month..dateTo.month
        monthRange.forEach { month ->
            val calendar = getMonthKey(year, month)
            val quartersVM = quarters[calendar]
            quartersVM?.setSelection(
                type = START,
                position = QuantumPosition(
                    left = false,
                    top = false,
                    right = true,
                    bottom = false
                )
            )

            val monthsVM = months[calendar]
            monthsVM?.setSelection(
                type = if (month == dateTo.month) END else STANDARD,
                position = QuantumPosition(
                    left = true,
                    top = false,
                    right = calendar != dateTo,
                    bottom = false
                )
            )
        }
    }

    /** Отметить полугодие. */
    @VisibleForTesting
    internal fun selectHalfYear(dateFrom: Calendar, dateTo: Calendar) {
        val year = dateFrom.year
        val monthRange = dateFrom.month..dateTo.month
        monthRange.forEach { month ->
            val calendar = getMonthKey(year, month)
            val halfYearVM = halfYears[calendar]
            halfYearVM?.setSelection(
                type = END,
                position = QuantumPosition(
                    left = true,
                    top = false,
                    right = false,
                    bottom = false
                )
            )

            val monthsVM = months[calendar]
            monthsVM?.setSelection(
                type = if (calendar == dateFrom) START else STANDARD,
                position = QuantumPosition(
                    left = !listOf(dateFrom.month, dateFrom.month + quarterMultiplicity).contains(calendar.month),
                    top = calendar.setHalfYearVerticalPlacement(),
                    right = true,
                    bottom = !calendar.setHalfYearVerticalPlacement()
                )
            )
        }
    }

    /** Сгенерировать календарь для сетки по дням. */
    internal fun generateDays(
        min: Calendar,
        max: Calendar,
        limitRange: SbisPeriodPickerRange,
        markerType: MarkerType,
        isDayAvailable: ((Calendar) -> Boolean)?,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme)
    ) {
        val monthNames =
            SbisPeriodPickerPlugin.resourceProvider?.get()?.getStringArray(RDesign.array.design_months)?.toList()
                ?: monthRange.getStringList()
        for (year in min.year..max.year) {
            generateMonths(year, min, max, limitRange, monthNames, markerType, isDayAvailable, dayCustomTheme)
        }
    }

    /** Сгенерировать календарь для сетки по квантам. */
    internal fun generateQuanta(
        min: Calendar,
        max: Calendar,
        limitRange: SbisPeriodPickerRange
    ) {
        val resourceProvider = SbisPeriodPickerPlugin.resourceProvider?.get()
        val halfYearList =
            resourceProvider?.getStringArray(R.array.short_quantum_list) ?: halfYearRange.getStringArray()
        val quarterList = resourceProvider?.getStringArray(R.array.quantum_list) ?: quarterRange.getStringArray()
        val monthList =
            resourceProvider?.getStringArray(RDesign.array.design_months_short) ?: monthRange.getStringArray()

        generateYearLabel(limitRange)

        for (year in min.year..max.year) {
            generateYear(year)
            generateHalfYears(halfYearList, year, limitRange)
            generateQuarters(quarterList, year, limitRange)
            generateMonths(monthList, year, limitRange)
        }

        monthGrid.sortAddAll(months.values)
        quarterGrid.sortAddAll(quarters.values)
        halfYearGrid.sortAddAll(halfYears.values)
        yearGrid.sortAddAll(years.values)
        yearLabelsGrid.sortAddAll(yearLabels.values)
    }

    /** Получить месяца для года. */
    private fun getMonths(year: Int): List<MonthModel> {
        return monthGrid.filter { it.year == year }
    }

    /** Получить кварталы для года. */
    private fun getQuarters(year: Int): List<QuarterModel> {
        return quarterGrid.filter { it.year == year }
    }

    /** Получить полугодия для года. */
    private fun getHalfYear(year: Int): List<HalfYearModel> {
        return halfYearGrid.filter { it.year == year }
    }

    /** Получить тип дня. */
    private fun Calendar.getDayType(dateFrom: Calendar, dateTo: Calendar): QuantumType {
        return when (this) {
            dateFrom -> START
            dateTo -> END
            else -> STANDARD
        }
    }

    /** Получить позицию дня в календаре относительно выбранного периода. */
    private fun Calendar.getDayPlacement(dateFrom: Calendar, dateTo: Calendar): QuantumPosition {
        return QuantumPosition(
            left = setDayLeftPlacement(dateFrom),
            top = setDayTopPlacement(dateFrom),
            right = setDayRightPlacement(dateTo),
            bottom = setDayBottomPlacement(dateTo)
        )
    }

    /** Настроить нахождение выбранных дней слева относительно текущего дня. */
    private fun Calendar.setDayLeftPlacement(dateFrom: Calendar): Boolean {
        // Слева от понедельника нет дней.
        return if (isMonday) {
            false
        } // Слева от текущего дня есть другие дни, это не начало периода и не первый день месяца.
        else {
            this != dateFrom && dayOfMonth != firstDayOfMonth
        }
    }

    /** Настроить нахождение выбранных дней сверху относительно текущего дня. */
    private fun Calendar.setDayTopPlacement(dateFrom: Calendar): Boolean {
        val isStartPeriodMonth = year == dateFrom.year && month == dateFrom.month
        return (isStartPeriodMonth && dayOfMonth - weekdays >= dateFrom.dayOfMonth) ||
            (!isStartPeriodMonth && dayOfMonth > weekdays)
    }

    /** Настроить нахождение выбранных дней справа относительно текущего дня. */
    private fun Calendar.setDayRightPlacement(dateTo: Calendar): Boolean {
        // Справа от воскресенья нет дней.
        return if (isSunday) {
            false
        } // Справа от текущего дня есть другие дни, если это не конец периода и не последний день месяца.
        else {
            this != dateTo && dayOfMonth != lastDayOfMonth
        }
    }

    /** Настроить нахождение выбранных дней снизу относительно текущего дня. */
    private fun Calendar.setDayBottomPlacement(dateTo: Calendar): Boolean {
        val isEndPeriodMonth = year == dateTo.year && month == dateTo.month
        return (isEndPeriodMonth && dayOfMonth + weekdays <= dateTo.dayOfMonth) ||
            (!isEndPeriodMonth && dayOfMonth <= lastDayOfMonth - weekdays)
    }

    /** @SelfDocumented */
    private fun checkFirstLastDays(dateFrom: Calendar, dateTo: Calendar): Boolean {
        return dateFrom.dayOfMonth == dateFrom.firstDayOfMonth && dateTo.dayOfMonth == dateTo.lastDayOfMonth
    }

    /** @SelfDocumented */
    private fun checkQuarter(dateFrom: Calendar, dateTo: Calendar): Boolean {
        return startQuarterMonths.contains(dateFrom.month) && dateFrom.month + 2 == dateTo.month
    }

    /** @SelfDocumented */
    private fun checkHalfYear(dateFrom: Calendar, dateTo: Calendar): Boolean {
        return startHalfYearMonths.contains(dateFrom.month) && dateFrom.month + 5 == dateTo.month
    }

    /** @SelfDocumented */
    private fun checkYear(dateFrom: Calendar, dateTo: Calendar): Boolean {
        return dateFrom.month == firstMonthOfYear && dateTo.month == lastMonthOfYear
    }

    /** Отметить месяц. */
    private fun selectMonth(dateFrom: Calendar) {
        if (dateFrom.dayOfMonth == dateFrom.firstDayOfMonth) {
            val monthsVM = months[dateFrom]
            monthsVM?.setSelection(
                type = START,
                position = QuantumPosition()
            )
        }
    }

    /**
     * Сброс диапазона квантов в календарной сетке.
     */
    private fun deselectQuantumPeriod(dateFrom: Calendar, dateTo: Calendar) {
        val rangeYear = dateFrom.year..dateTo.year
        rangeYear.forEach { year ->
            val monthRange = when {
                dateFrom.year == dateTo.year -> dateFrom.month..dateTo.month
                dateFrom.year == year -> dateFrom.month..lastMonthOfYear
                dateTo.year == year -> firstMonthOfYear..dateTo.month
                else -> firstMonthOfYear..lastMonthOfYear
            }
            monthRange.forEach { month ->
                val calendar = getMonthKey(year, month)
                val monthVm = months[calendar.removeTime()]
                monthVm?.resetSelection()
                val quarterVm = quarters[calendar.removeTime()]
                quarterVm?.resetSelection()
                val halfYearVm = halfYears[calendar.removeTime()]
                halfYearVm?.resetSelection()
                val yearVm = yearLabels[calendar.removeTime()]
                yearVm?.resetSelection()
            }
        }
    }

    /** Сгенерировать месяцы. */
    private fun generateMonths(
        year: Int,
        min: Calendar,
        max: Calendar,
        limit: SbisPeriodPickerRange,
        monthNames: List<String>,
        markerType: MarkerType,
        isDayAvailable: ((Calendar) -> Boolean)?,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme)
    ) {
        val monthFrom = getStartMonthOfYear(year, min)
        val monthTo = getEndMonthOfYear(year, max)
        for (month in monthFrom..monthTo) {
            val dayFrom = getStartDayOfMonth(year, month, min)
            val dayTo = getEndDayOfMonth(year, month, max)
            val title = if (isCurrentYear(year)) {
                String.format("%s", monthNames[month])
            } else {
                getFormattedMonthLabel(monthNames[month], year)
            }
            val key = CalendarStorageKey.createMonthKey(year, month)
            val monthLabel = prepareMonthLabels(year, month, limit, title)
            val monthDays = prepareDays(
                year,
                month,
                limit,
                dayFrom,
                dayTo,
                markerType,
                isDayAvailable,
                dayCustomTheme
            )
            val monthDaysAligned = (
                0 until getDayOfWeek(
                    year,
                    month,
                    dayFrom
                )
                ).map { EmptyModel(GregorianCalendar(year, month, dayFrom)) } + monthDays.values.toList()

            monthLabels[key] = monthLabel
            this.monthDaysAligned[key] = monthDaysAligned.size
            dayGrid.add(monthLabel)

            days.putAll(monthDays)
            dayGrid.addAll(monthDaysAligned)
        }
    }

    /** Подготовить дни. */
    private fun prepareDays(
        year: Int,
        month: Int,
        limit: SbisPeriodPickerRange,
        dayFrom: Int,
        dayTo: Int,
        markerType: MarkerType,
        isDayAvailable: ((Calendar) -> Boolean)?,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme)
    ): Map<Calendar, DayModel> {
        val today = Calendar.getInstance().removeTime()
        return (dayFrom..dayTo).associate { day ->
            val calendar = GregorianCalendar(year, month, day)
            val isCurrent = calendar == today
            val dayVm = DayModel(
                dayOfMonth = calendar.dayOfMonth,
                dayOfWeek = calendar.dayOfWeek,
                date = calendar,
                daySelection = QuantumSelection(),
                counter = "",
                markerType = markerType,
                isCurrent = isCurrent,
                isRangePart = calendar.timeInMillis in limit.start.timeInMillis..limit.end.timeInMillis,
                isAvailable = isDayAvailable?.let { it(calendar) } ?: true,
                customTheme = dayCustomTheme(calendar)
            )
            calendar to dayVm
        }
    }

    /** Подготовить заголовки месяцев. */
    private fun prepareMonthLabels(
        year: Int,
        month: Int,
        limit: SbisPeriodPickerRange,
        title: String
    ): MonthLabelModel {
        val isRangePart = checkRangeBelonging(limit, month, year, monthStep)
        return MonthLabelModel(getMonthKey(year, month), title, isRangePart)
    }

    /** @SelfDocumented */
    private fun getDayOfWeek(year: Int, month: Int, day: Int) = GregorianCalendar(year, month, day).dayOfWeek

    /** @SelfDocumented */
    private fun <E : QuantumItemModel> LinkedList<E>.sortAddAll(collection: Collection<E>) =
        this.addAll(collection.sortedWith(compareBy({ it.year }, { it.month })))

    /** Сгенерировать год. */
    private fun generateYearLabel(limit: SbisPeriodPickerRange) {
        for (year in limit.startYear..limit.endYear) {
            val isRangePart = checkRangeBelonging(limit, firstMonthOfYear, year, yearStep)
            val item = YearLabelModel(year.toString(), year, QuantumSelection(), isRangePart = isRangePart)
            val yearKey = getYearKey(year)
            yearLabels[yearKey] = item
        }
    }

    /** Сгенерировать год. */
    private fun generateYear(year: Int) {
        val item = YearLabelModel(year.toString(), year, QuantumSelection(), true)
        val yearKey = getYearKey(year)
        years[yearKey] = item
    }

    /** Сгенерировать полугодия. */
    private fun generateHalfYears(halfYearList: Array<out String>, year: Int, limit: SbisPeriodPickerRange) {
        halfYearList.forEachIndexed { i, s ->
            val month = i * halfYearMultiplicity
            val isRangePart = checkRangeBelonging(limit, month, year, halfYearStep)
            val item = HalfYearModel(s, year, month, QuantumSelection(), isRangePart)
            val halfYearKey = getMonthKey(year, month)
            halfYears[halfYearKey] = item
        }
    }

    /** Сгенерировать кварталы. */
    private fun generateQuarters(quarterList: Array<out String>, year: Int, limit: SbisPeriodPickerRange) {
        quarterList.forEachIndexed { i, s ->
            val month = i * quarterMultiplicity
            val isRangePart = checkRangeBelonging(limit, month, year, quarterStep)
            val item = QuarterModel(s, year, month, QuantumSelection(), isRangePart)
            val quarterKey = getMonthKey(year, month)
            quarters[quarterKey] = item
        }
    }

    /** Сгенерировать месяцы. */
    private fun generateMonths(monthList: Array<out String>, year: Int, limit: SbisPeriodPickerRange) {
        monthList.forEachIndexed { month, s ->
            val isRangePart = checkRangeBelonging(limit, month, year, monthStep)
            val item = MonthModel(s, year, month, QuantumSelection(), isRangePart)
            val monthKey = getMonthKey(year, month)
            months[monthKey] = item
        }
    }

    /** @SelfDocumented */
    private fun Iterable<Int>.getStringList() = this.map { it.toString() }.toList()

    /** @SelfDocumented */
    private fun Iterable<Int>.getStringArray() = this.getStringList().toTypedArray()
}