package ru.tensor.sbis.wheel_time_picker.picker_live_data

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.LocalDateTime

/**
 * Служебный контракт для выбора даты/периода.
 *
 * @author us.bessonov
 */
internal interface PeriodPickerLiveDataDependency {
    /** Observable даты начала периода */
    val startDateObservable: Observable<LocalDateTime>

    /** Дата начала периода */
    var startDate: LocalDateTime

    /** Observable даты окончания периода */
    val endDateObservable: Observable<LocalDateTime>

    /** Дата окончания периода */
    var endDate: LocalDateTime

    /** Observable границ интервала доступных дат */
    val timeBoundsObservable: Observable<Pair<LocalDateTime?, LocalDateTime?>>

    /** Интервал доступных дат */
    val timeBounds: Pair<LocalDateTime?, LocalDateTime?>

    /** Событие на весь день */
    val allDayLong: BehaviorSubject<Boolean>
}