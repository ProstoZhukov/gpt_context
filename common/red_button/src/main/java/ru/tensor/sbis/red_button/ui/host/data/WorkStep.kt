package ru.tensor.sbis.red_button.ui.host.data

/**
 * Перечисление возможных шагов работы фрагмента красной кнопки [HostFragment]
 *
 * @author ra.stepanov
 */
internal enum class WorkStep {

    /** Значение отображающее, что фрагмент работает в режиме ввода пин-кода */
    STEP_PIN,

    /** Значение отображающее, что фрагмент работает в режиме ввода смс-кода */
    STEP_SMS
}