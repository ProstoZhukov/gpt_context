package ru.tensor.sbis.wheel_time_picker.picker_live_data

/**
 * Провайдер конфигурации для пикера даты/периода с поддержкой нулевого значения.
 *
 * @author us.bessonov
 */
internal interface PeriodWithZeroLengthPicker {

    /** @SelfDocumented */
    var canCreateZeroLengthEvent: Boolean
}