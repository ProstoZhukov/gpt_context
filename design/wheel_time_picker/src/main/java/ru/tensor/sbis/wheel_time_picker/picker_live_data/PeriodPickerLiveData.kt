package ru.tensor.sbis.wheel_time_picker.picker_live_data

import android.content.Context
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.IllegalFieldValueException
import org.joda.time.LocalDateTime
import org.joda.time.Period
import org.joda.time.PeriodType
import ru.tensor.sbis.design.cylinder.picker.cylinder.IBindCylinder
import ru.tensor.sbis.design.cylinder.picker.time.CylinderDateTimePicker
import ru.tensor.sbis.design.cylinder.picker.value.CylinderTypePicker
import ru.tensor.sbis.design.cylinder.picker.value.ValueLiveData
import ru.tensor.sbis.design.utils.delegatePropertyMT
import ru.tensor.sbis.wheel_time_picker.DEFAULT_MAX_TIME_OFF_VALUE
import ru.tensor.sbis.wheel_time_picker.MAX_POSSIBLE_DAY_IN_MONTH
import ru.tensor.sbis.wheel_time_picker.MAX_YEARS_COUNT
import ru.tensor.sbis.wheel_time_picker.MIN_YEARS_COUNT
import ru.tensor.sbis.wheel_time_picker.R
import ru.tensor.sbis.wheel_time_picker.data.DurationMode
import ru.tensor.sbis.wheel_time_picker.getDaysCountInMonth
import ru.tensor.sbis.wheel_time_picker.getMidnight
import ru.tensor.sbis.wheel_time_picker.observable
import java.util.concurrent.TimeUnit

/**
 * Предназначен для управления состоянием пикеров даты/периода.
 *
 * @author us.bessonov
 */
internal abstract class PeriodPickerLiveData(
    /** @SelfDocumented */
    val context: Context,
    private val durationMode: BehaviorSubject<DurationMode>
) : PeriodPickerLiveDataDependency {

    private var cylinder = 0

    /** Период времени в пределах 1 дня */
    val period get() = Period(startDate, endDate, PeriodType.dayTime())

    /** Период времени больше 1 дня */
    val periodMore1Day get() = period.toDurationFrom(DateTime()).toStandardDays().days > 0

    /** Время начала */
    val startTimeLiveData: PeriodWithOneDayPicker = StartTimeLiveData()

    /** Observable количества дней в выбранном месяце. */
    internal val daysCollection = BehaviorSubject.createDefault((1..MAX_POSSIBLE_DAY_IN_MONTH).toList())

    /** Лайвдата барабана выбора года в пикере дней. */
    val dateOnlyLiveDataYear: DateOnlyLiveDataYear = DateOnlyLiveDataYear()

    /** Лайвдата барабана выбора месяца в пикере дней. */
    val dateOnlyLiveDataMonth: DateOnlyLiveDataMonth = DateOnlyLiveDataMonth()

    /** Лайвдата барабана выбора дня в пикере дней. */
    val dateOnlyLiveDataDay: DateOnlyLiveDataDay = DateOnlyLiveDataDay()

    /** Время окончания */
    val endTimeLiveData: PeriodAndDatePicker = EndTimeLiveData()

    /** Стандартные временные отрезки для пикера периода дат */
    var defaultPickerData: MutableCollection<Period> = HashSet()

    /** Данные в пикере */
    var pickerData: Collection<Period> = defaultPickerData

    /** Барабаны выбирают время в пределах одного дня */
    var isOneDay: Boolean
        get() = endTimeLiveData.isOneDay
        set(value) {
            endTimeLiveData.isOneDay = value
            startTimeLiveData.isOneDay = value
        }

    var canCreateZeroLengthEvent: Boolean
        get() = endTimeLiveData.canCreateZeroLengthEvent
        set(value) {
            endTimeLiveData.canCreateZeroLengthEvent = value
            updateDefaultPickerData(oneDayHoursBound)
        }

    /** Изменение дат в пикере */
    var pickerDataChanges: (() -> Unit)? = null

    /** Границы времени в пределах одного дня */
    var oneDayHoursBound by observable(TimeUnit.HOURS.toMillis(8)) {
        updateDefaultPickerData(it)
    }

    /** Можно ли создать событие на весь день */
    var canCreateAllDayLongEvent: Boolean = false
        set(value) {
            field = value
            updateDefaultPickerData(oneDayHoursBound)
        }

    private fun updateDefaultPickerData(mills: Long) {
        with(defaultPickerData) {
            clear()
            if (canCreateZeroLengthEvent) {
                add(Period.minutes(0))
            }
            add(Period.minutes(5))
            add(Period.minutes(10))
            add(Period.minutes(15))
            add(Period.minutes(30))
            add(Period.minutes(45))
            add(Period.hours(1))
            add(Period.hours(1).plusMinutes(30))
            (2..TimeUnit.MILLISECONDS.toHours(DEFAULT_MAX_TIME_OFF_VALUE).toInt())
                .forEach { add(Period.hours(it)) }
            if (canCreateAllDayLongEvent)
                add(Period.hours(24))
            val valuesForPickerData = defaultPickerData.filter { period ->
                val periodMillis = period.toStandardDuration().millis
                periodMillis <= mills || period.hours == Period.hours(24).hours
            }.toMutableList()
            if (pickerData.size < defaultPickerData.size) { // отбросили некоторые значения больше полуночи - добиваем еще одним значением до полуночи
                val periodUntilMidnight = Duration(mills).toPeriod()
                if (!valuesForPickerData.contains(periodUntilMidnight)) {
                    valuesForPickerData.add(periodUntilMidnight)
                }
            }

            pickerData = valuesForPickerData
        }
    }

    private inner class StartTimeLiveData : PeriodWithOneDayPicker {

        override var isOneDay: Boolean = false

        override val showMidnightAs24: Observable<Boolean> = Observable.just(false)
        override val scrollEnabled: Observable<Boolean> = Observable.just(true)
        override var cylinder by delegatePropertyMT(this@PeriodPickerLiveData::cylinder)
        override val dateChangeObservable get() = startDateObservable
        override var date: LocalDateTime
            get() = startDate
            set(value) {
                var newEndDate = value.plus(period)
                if (isOneDay) {
                    val newEndDateMidnight = getMidnight(value)
                    if (newEndDate > newEndDateMidnight)
                        newEndDate = newEndDateMidnight
                } else {
                    if (newEndDate > timeBounds.second)
                        newEndDate = timeBounds.second
                }

                startDate = value
                endDate = newEndDate
            }
        override val timeBoundsObservable get() = this@PeriodPickerLiveData.timeBoundsObservable

        override fun getDateTime(picker: CylinderDateTimePicker, position: Int): LocalDateTime {
            val period = picker.getDateForPosition(position)
            return picker.resolveAdapterDateWithLiveDataDate(period, date)
        }
    }

    private inner class EndTimeLiveData : PeriodAndDatePicker, PeriodWithZeroLengthPicker {

        override var isOneDay: Boolean = false

        override val scrollEnabledSubject = BehaviorSubject.createDefault(true)

        override val scrollEnabled: Observable<Boolean> = scrollEnabledSubject

        override var canCreateZeroLengthEvent: Boolean = false

        override var cylinder by delegatePropertyMT(this@PeriodPickerLiveData::cylinder)
        override val comparator: Comparator<Period> =
            Comparator { o1, o2 ->
                if (o1 == o2)
                    return@Comparator 0

                if (o1 == null)
                    return@Comparator -1

                if (o2 == null)
                    return@Comparator 1

                o1.toDurationFrom(DateTime()).toStandardSeconds().seconds - o2.toDurationFrom(
                    DateTime()
                ).toStandardSeconds().seconds
            }
        override val stringConverter: (Period) -> String = {

            val string = StringBuilder()
            if (canCreateAllDayLongEvent && it.hours == 24 && it.minutes == 0) {
                string.append(context.resources.getString(R.string.wheel_time_picker_time_selection_all_day))
            } else {
                if (it.days > 0 && it.hours > 0 && it.minutes > 0) {
                    //Сокращенная форма
                    string
                        .append(
                            context.resources.getString(R.string.wheel_time_picker_picker_days).format(it.days)
                        )
                        .append(" ")
                        .append(
                            context.resources.getString(R.string.wheel_time_picker_picker_hours).format(it.hours)
                        )
                        .append(" ")
                        .append(
                            context.resources.getString(R.string.wheel_time_picker_picker_minutes).format(it.minutes)
                        )
                } else {
                    // Полная форма
                    if (it.days > 0) string.append(
                        context.resources.getQuantityString(
                            R.plurals.wheel_time_picker_plurals_days,
                            it.days,
                            it.days
                        )
                    ).append(" ")
                    if (it.hours > 0) string.append(
                        context.resources.getQuantityString(
                            R.plurals.wheel_time_picker_plurals_hours,
                            it.hours,
                            it.hours
                        )
                    ).append(" ")
                    if (it.minutes > 0 || it.hours == 0 && it.days == 0) string.append(
                        context.resources.getQuantityString(
                            R.plurals.wheel_time_picker_plurals_minutes,
                            it.minutes,
                            it.minutes
                        )
                    )
                }
            }
            string.toString()
        }

        override val values: Collection<Period> get() = pickerData

        override val dateChangeObservable get() = endDateObservable

        override val showMidnightAs24Subject = BehaviorSubject.createDefault(false)
        override val showMidnightAs24 = showMidnightAs24Subject

        override var date by delegatePropertyMT(this@PeriodPickerLiveData::endDate)
        override val timeBoundsObservable: Observable<Pair<LocalDateTime?, LocalDateTime?>>
            get() = startDateObservable.map {
                Pair(
                    it,
                    if (isOneDay) earlyDate(getMidnight(it), it.plusMillis(oneDayHoursBound.toInt())) else it.plusYears(
                        1
                    )
                ).apply {
                    if (isOneDay) {
                        val midnight = getMidnight(it)
                        pickerData =
                            defaultPickerData.filter { period ->
                                it.plus(period)
                                    .isBefore(midnight) && period.hours <= oneDayHoursBound
                            }
                        pickerDataChanges?.invoke()
                    }
                }
            }

        override fun getDateTime(picker: CylinderDateTimePicker, position: Int): LocalDateTime {
            val period = picker.getDateForPosition(position)
            return if (durationMode.value == DurationMode.END) {
                val maxDate = if (isOneDay) {
                    startDate.withHourOfDay(0).withMinuteOfHour(0).plusDays(1)
                } else {
                    startDate.plusDays(30)
                }
                val date = picker.resolveAdapterDateWithLiveDataDate(period, date, maxDate)
                if (isOneDay) {
                    if (date <= startDate) startDate.plusMinutes(5) else date
                } else {
                    if (date < startDate) startDate else date
                }
            } else {
                when (period.periodType) {
                    PeriodType.minutes() -> {
                        val res = endDate.withMinuteOfHour(0).plus(period)
                        when {
                            canCreateZeroLengthEvent && (res.toLocalDate() != startDate.toLocalDate() || res <= startDate) -> {
                                startDate
                            }
                            isOneDay && (res.toLocalDate() != startDate.toLocalDate() || res <= startDate) -> {
                                startDate.plusMinutes(5)
                            }
                            res < startDate -> {
                                res.plusDays(1)
                            }
                            else -> res
                        }
                    }
                    PeriodType.hours() -> {
                        val res = startDate.withHourOfDay(0).withMinuteOfHour(endDate.minuteOfHour).plus(period)
                        when {
                            canCreateZeroLengthEvent && (res.toLocalDate() != startDate.toLocalDate() || res <= startDate) -> {
                                startDate
                            }
                            isOneDay && (res.toLocalDate() != startDate.toLocalDate() || res <= startDate) -> {
                                startDate.plusMinutes(5)
                            }
                            res < startDate -> {
                                res.plusDays(1)
                            }
                            else -> res
                        }
                    }
                    else -> startDate
                }
            }
        }

        override val valueChangeObservable: Observable<Period>
            get() = Observable.merge(startDateObservable, endDateObservable)
                .map { if (allDayLong.value == true) Period(24, 0, 0, 0) else period }

        override var valueSetter by delegatePropertyMT({
            if (allDayLong.value == true) Period(
                24,
                0,
                0,
                0
            ) else period
        }) {
            val period = if (canCreateAllDayLongEvent) {
                val setToMidnight = it.hours == 24 && it.minutes == 0
                showMidnightAs24Subject.onNext(setToMidnight)
                allDayLong.onNext(setToMidnight)
                scrollEnabledSubject.onNext(!setToMidnight)
                if (setToMidnight) Period(startDate, getMidnight(startDate)) else it
            } else {
                scrollEnabledSubject.onNext(true)
                it
            }
            endDate = startDate.plus(period)
        }

        @Suppress("unused")
        private fun earlyDate(firstDate: LocalDateTime, secondDate: LocalDateTime): LocalDateTime {
            return if (firstDate.isBefore(secondDate)) {
                firstDate
            } else {
                secondDate
            }
        }
    }

    /**
     * Лайвдата для цилиндра ГОДА при выборе одной только даты.
     */
    inner class DateOnlyLiveDataYear : CylinderTypePicker.LiveData<Int> {

        override var cylinder by delegatePropertyMT(this@PeriodPickerLiveData::cylinder)

        /**
         * Такая усложнённая конструкция необходима для обхода оператора skip(1) в коде [CylinderTypePicker], а так же
         * для корректного изменения количества дней в месяце в зависимости от года.
         * */
        override val valueChangeObservable: Observable<Int>
            get() = Observable.merge(startDateObservable, startDateObservable)
                .concatMap { Observable.just(it.year).delay(100, TimeUnit.MILLISECONDS) }
                .take(3)
                .doOnNext { valueSetter = it }

        override var valueSetter: Int = startDate.year
            set(value) {
                if (field == value) return
                field = value
                daysCollection.onNext((1..getDaysCountInMonth(dateOnlyLiveDataMonth.valueSetter, field)).toList())
                updateDates()
            }

        override val values: Collection<Int>
            get() = (MIN_YEARS_COUNT..MAX_YEARS_COUNT).toList()

        override val comparator: Comparator<Int> =
            Comparator { o1, o2 ->
                return@Comparator when {
                    o1 == o2 -> 0
                    o1 == null || o2 == null -> -1
                    o1 > o2 -> 1
                    o1 < o2 -> -1
                    else -> -1

                }
            }
        override val stringConverter: (Int) -> String
            get() = { intYear -> "$intYear" }

    }


    /**
     * Лайвдата для цилиндра МЕСЯЦЕВ при выборе одной только даты.
     */
    inner class DateOnlyLiveDataMonth : ValueLiveData<Int> {

        override var cylinder by delegatePropertyMT(this@PeriodPickerLiveData::cylinder)

        /** Оператор: take(1) нужен, чтобы брать только стартовое значение барабна, иначе барабан зациклится. */
        override val valueChangeObservable: Observable<Int>
            get() = startDateObservable.map { it.monthOfYear - 1 }.take(1)


        override var valueSetter: Int = startDate.monthOfYear - 1
            set(value) {
                if (field == value) return
                field = value
                daysCollection.onNext((1..getDaysCountInMonth(value, dateOnlyLiveDataYear.valueSetter)).toList())
                updateDates()
            }

        override val values: Collection<Int>
            get() = (0..11).toList()

        override val comparator: Comparator<Int> =
            Comparator { o1, o2 ->
                return@Comparator when {
                    o1 == o2 -> 0
                    o1 == null || o2 == null -> -1
                    o1 > o2 -> 1
                    o1 < o2 -> -1
                    else -> -1
                }
            }
        override val collectionChangeObservable: Observable<List<Int>>
            get() = BehaviorSubject.create()

        override val bind: (IBindCylinder, Int) -> Unit
            get() = { binder, data ->
                binder.bind(context.resources.getStringArray(ru.tensor.sbis.design.R.array.design_months_names)[data])
            }
    }


    /**
     * Лайвдата для цилиндра ДНЕЙ при выборе одной только даты.
     */
    inner class DateOnlyLiveDataDay : ValueLiveData<Int> {

        override var cylinder by delegatePropertyMT(this@PeriodPickerLiveData::cylinder)

        override var valueSetter: Int = startDate.dayOfMonth
            set(value) {
                if (field == value) return
                field = value
                updateDates()
            }

        /** Оператор: take(1) нужен, чтобы брать только стартовое значение барабна, иначе барабан зациклится. */
        override val valueChangeObservable: Observable<Int>
            get() = startDateObservable.map { it.dayOfMonth }.take(1)

        override val values: Collection<Int>
            get() = daysCollection.value ?: (1..MAX_POSSIBLE_DAY_IN_MONTH).toList()

        override val comparator: Comparator<Int> =
            Comparator { o1, o2 ->
                return@Comparator when {
                    o1 == o2 -> 0
                    o1 == null || o2 == null -> -1
                    o1 > o2 -> 1
                    o1 < o2 -> -1
                    else -> -1
                }
            }
        override val collectionChangeObservable: Observable<List<Int>>
            get() = daysCollection

        override val bind: (IBindCylinder, Int) -> Unit
            get() = { binder, day ->
                binder.bind("$day")
            }

    }

    /** Формирование даты и обновление данных. */
    private fun updateDates() {
        try {
            LocalDateTime(
                dateOnlyLiveDataYear.valueSetter,
                dateOnlyLiveDataMonth.valueSetter + 1,
                dateOnlyLiveDataDay.valueSetter,
                0,
                0
            ).let { date ->
                startDate = date
                endDate = date
            }
        } catch (e: IllegalFieldValueException) {
            // Для перехвата ситуаций, когда барабан месяца или года уже провернулся и пытается вставить своё значение,
            // однако значение барабана дней не соответствует выбранному месяцу\году, он ещё не подстроился.
            return
        }
    }
}