package ru.tensor.sbis.calendar_decl.calendar.events

import androidx.fragment.app.Fragment
import java.util.*

interface CalendarEventsFragmentProvider {

    /**
     * Метод, предостовляющий фрагмент событий на месяц c кастомными интерактором и роутером
     *
     * @param year год
     * @param month месяц
     * @param key ключ по которому извлекаются роутер и интерактор
     */
    fun getMonthEventsFragment(year: Int, month: Int, key: String): Fragment

    /**
     * Метод, предостовляющий фрагмент событий на год c кастомными интерактором и роутером
     *
     * @param year год
     * @param key ключ по которому извлекаются роутер и интерактор
     */
    fun getYearEventsFragment(year: Int, key: String): Fragment
}