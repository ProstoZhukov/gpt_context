package ru.tensor.sbis.wheel_time_picker.picker_live_data

import io.reactivex.subjects.Subject
import org.joda.time.Period
import ru.tensor.sbis.design.cylinder.picker.value.CylinderTypePicker

/**
 * Провайдер конфигурации для пикера даты/периода.
 *
 * @author us.bessonov
 */
internal interface PeriodAndDatePicker : PeriodWithOneDayPicker, CylinderTypePicker.LiveData<Period>,
    PeriodWithZeroLengthPicker {

    /** Отображать полночь как 24 часа */
    val showMidnightAs24Subject: Subject<Boolean>

    /** Состояние активности скролла пикера окончания */
    val scrollEnabledSubject: Subject<Boolean>
}