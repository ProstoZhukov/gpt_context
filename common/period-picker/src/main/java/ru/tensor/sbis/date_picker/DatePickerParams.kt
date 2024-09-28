package ru.tensor.sbis.date_picker

import java.io.Serializable
import java.util.*

/**
 * Класс - агрегатор параметров компонента выбора периода
 * @property historyKey уникальный ключ, управляющий доступом к истории
 * @property type тип выбора - период или одичночная дата
 * @property minDate нижний предел календарной сетки
 * @property maxDate верхней предел календарной сетки
 * @property period выбранный изначально период
 * @property initialDate дата, начиная с которой будет изначально отображаться календарная сетка
 * @property resultReceiverId идентификатор получателя результата выбора (по умолчанию используется [historyKey])
 *
 * @author mb.kruglova
 */
data class DatePickerParams @JvmOverloads constructor(
    val historyKey: String = "",
    val type: PickerType = PickerType.PERIOD,
    val minDate: Calendar = MIN_DATE,
    val maxDate: Calendar = MAX_DATE,
    val period: Period = Period.create(),
    val initialDate: Calendar = getCurrentDay(),
    val resultReceiverId: String = historyKey,
    val visualParams: VisualParams? = null
) : Serializable

