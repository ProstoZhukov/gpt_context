package ru.tensor.sbis.wheel_time_picker

import android.content.Context
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.LocalDateTime
import org.joda.time.Period
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.rx.livedata.dataValue
import ru.tensor.sbis.design.cylinder.picker.value.CylinderTypePicker
import ru.tensor.sbis.design.cylinder.picker.value.ValueLiveData
import ru.tensor.sbis.design.utils.delegateProperty
import ru.tensor.sbis.wheel_time_picker.data.DurationMode
import ru.tensor.sbis.wheel_time_picker.data.PeriodPickerMode
import ru.tensor.sbis.wheel_time_picker.picker_live_data.PeriodAndDatePicker
import ru.tensor.sbis.wheel_time_picker.picker_live_data.PeriodPickerLiveData
import ru.tensor.sbis.wheel_time_picker.picker_live_data.PeriodWithOneDayPicker

/**
 * Вьюмодель, определяющая состояние экрана выбора даты и времени.
 *
 * @author us.bessonov
 */
class PeriodPickerViewModel(
    context: Context,
    private val startTimeSubject: BehaviorSubject<RxContainer<LocalDateTime>>,
    private val endTimeSubject: BehaviorSubject<RxContainer<LocalDateTime>>,
    private val timeBoundsSubject: BehaviorSubject<Pair<LocalDateTime?, LocalDateTime?>>
) {

    // вход/выход
    /** Режим отображения пикера длительности */
    val durationMode = BehaviorSubject.create<DurationMode>()

    // вход/выход
    /** Адаптер лайфдаты барабанов */
    private var periodPickerLiveData: PeriodPickerLiveData = object : PeriodPickerLiveData(context, durationMode) {

        override val startDateObservable: Observable<LocalDateTime>
            get() = startTimeSubject.map {
                it.value ?: LocalDateTime(0)
            }
        override var startDate: LocalDateTime
            get() = startTimeSubject.dataValue ?: LocalDateTime.now()
            set(value) {
                startTimeSubject.dataValue = value
            }

        override val endDateObservable: Observable<LocalDateTime>
            get() = endTimeSubject.map {
                it.value ?: LocalDateTime(0)
            }

        override var endDate: LocalDateTime
            get() = endTimeSubject.dataValue ?: startDate
            set(it) {
                endTimeSubject.dataValue = it
            }

        override val timeBoundsObservable: Observable<Pair<LocalDateTime?, LocalDateTime?>>
            get() = timeBoundsSubject

        override val timeBounds: Pair<LocalDateTime?, LocalDateTime?>
            get() = timeBoundsSubject.value!!

        override val allDayLong = BehaviorSubject.createDefault(false)
    }

    // вход/выход
    /**
     * Режим работы - простой выбор даты и времени / выбор времени начала / выбор времени окончания.
     */
    val mode = BehaviorSubject.createDefault(PeriodPickerMode.DATE_AND_TIME)

    // выход
    /** Событие на весь день */
    val allDayLong: BehaviorSubject<Boolean>
        get() = periodPickerLiveData.allDayLong

    /**
     * @see [allDayLong]
     */
    var allDayLongValue: Boolean
        get() = allDayLong.value ?: false
        set(value) {
            allDayLong.onNext(value)
        }

    // вход
    /** Можно ли создать событие на весь день */
    var canCreateAllDayLongEvent: Boolean by delegateProperty(periodPickerLiveData::canCreateAllDayLongEvent)

    /** Барабаны выбирают время в пределах одного дня */
    var isOneDay: Boolean by delegateProperty(periodPickerLiveData::isOneDay)

    /** Можно ли создать событие с нулевой продолжительностью */
    var canCreateZeroLengthEvent: Boolean by delegateProperty(periodPickerLiveData::canCreateZeroLengthEvent)

    /**Максимальная продолжительность в часах в барабане для режима один день */
    var maxDurationTime: Long by delegateProperty(periodPickerLiveData::oneDayHoursBound)

    var pickerDataChanges: (() -> Unit)? by delegateProperty(periodPickerLiveData::pickerDataChanges)

    val periodMore1Day: Boolean
        get() = periodPickerLiveData.periodMore1Day

    internal val startTimeLiveData: PeriodWithOneDayPicker
        get() = periodPickerLiveData.startTimeLiveData

    internal val endTimeLiveData: PeriodAndDatePicker
        get() = periodPickerLiveData.endTimeLiveData

    internal val pickerData: Collection<Period>
        get() = periodPickerLiveData.pickerData

    internal fun configureEndLiveData(showMidnightAs24: Boolean, scrollEnabled: Boolean) {
        periodPickerLiveData.endTimeLiveData.apply {
            showMidnightAs24Subject.onNext(showMidnightAs24)
            scrollEnabledSubject.onNext(scrollEnabled)
        }
    }

    /** @SelfDocumented */
    internal val onlyDateDataDay: ValueLiveData<Int>
        get() = periodPickerLiveData.dateOnlyLiveDataDay

    /** @SelfDocumented */
    internal val onlyDateDataMonth: ValueLiveData<Int>
        get() = periodPickerLiveData.dateOnlyLiveDataMonth

    /** @SelfDocumented */
    internal val onlyDateDataYear: CylinderTypePicker.LiveData<Int>
        get() = periodPickerLiveData.dateOnlyLiveDataYear

}
