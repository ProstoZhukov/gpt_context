package ru.tensor.sbis.crud.sbis.retail_settings.model

import ru.tensor.sbis.retail_settings.generated.ShiftDateSource as ControllerShiftDateSource

/**
 * Перечисление типов времени открытия смены: START, END
 */
enum class ShiftDateSource {

    START,
    END
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerShiftDateSource.map(): ShiftDateSource =
        when (this) {
            ControllerShiftDateSource.START -> ShiftDateSource.START
            ControllerShiftDateSource.END -> ShiftDateSource.END
        }

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun ShiftDateSource.map(): ControllerShiftDateSource =
        when (this) {
            ShiftDateSource.START -> ControllerShiftDateSource.START
            ShiftDateSource.END -> ControllerShiftDateSource.END
        }