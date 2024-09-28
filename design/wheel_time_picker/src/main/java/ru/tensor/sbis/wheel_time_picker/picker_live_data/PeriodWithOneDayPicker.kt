package ru.tensor.sbis.wheel_time_picker.picker_live_data

import ru.tensor.sbis.design.cylinder.picker.time.CylinderDateTimePicker

/**
 * Провайдер конфигурации для пикера даты/периода в пределах одного дня.
 *
 * @author us.bessonov
 */
internal interface PeriodWithOneDayPicker : CylinderDateTimePicker.LiveData {

    /** Барабаны выбирают время в пределах одного дня */
    var isOneDay: Boolean
}