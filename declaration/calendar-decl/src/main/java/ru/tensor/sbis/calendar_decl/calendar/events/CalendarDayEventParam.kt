package ru.tensor.sbis.calendar_decl.calendar.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Класс дополнительных параметров создания событий календаря
 */
sealed class CalendarDayEventParam: Parcelable {
    /**
     * Параметры создания отпуска
     * @param forceFact задает условие как создавать отпуск, если установлен то создавать только фактический.
     * если сброшен - то в зависимости от настроек и выбранных дат отпуска будет создаваться плановый или фактический
     */
    @Parcelize
    data class Vacation(val forceFact: Boolean, val desc: String? = null, val error: String? = null): CalendarDayEventParam()

    /**
     * Пустой набор параметров
     */
    @Parcelize
    object Empty: CalendarDayEventParam()
}
