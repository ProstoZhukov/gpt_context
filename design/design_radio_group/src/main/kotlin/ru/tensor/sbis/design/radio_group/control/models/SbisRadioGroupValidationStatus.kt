package ru.tensor.sbis.design.radio_group.control.models

/**
 * Состояние валидации радиогруппы.
 * В состоянии [INVALID] будет нарисована обводка вокруг компонента для сигнализации об ошибке.
 */
enum class SbisRadioGroupValidationStatus {

    /** Валидное состояние. Обводка валидации не отображается. */
    VALID,

    /** Невалидное состояние. Отображается обводка валидации вокруг компонента. */
    INVALID
}
