package ru.tensor.sbis.crud.devices.settings.model

import java.util.*
import ru.tensor.devices.settings.generated.ShiftInfo as ControllerShiftInfo

/**
 * Модель с информацией о текущей смене на рабочем месте
 * @property shiftIsOpen true - если на рабочем месте есть окрытая смена
 * @property shiftNumber - номер открытой смены
 * @property shiftOpenedDateTime - время открытия смены
 */
data class ShiftInfo(
    val shiftIsOpen: Boolean,
    val shiftNumber: Int?,
    val shiftOpenedDateTime: Date?
)

fun ControllerShiftInfo.map() = ShiftInfo(
    shiftIsOpen,
    shiftNumber,
    shiftOpenedDateTime
)