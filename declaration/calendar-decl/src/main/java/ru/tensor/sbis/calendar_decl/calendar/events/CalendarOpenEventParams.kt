package ru.tensor.sbis.calendar_decl.calendar.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Параметры открытия карточки
 */
sealed class CalendarOpenEventParams : Parcelable {
    /**
     * Открытие карточки события без доппараметров
     */
    @Parcelize
    object Default : CalendarOpenEventParams()

    /**
     * Открытие карточки планового отпуска с целью перенести его на другие даты
     */
    @Parcelize
    object ChangeVacation : CalendarOpenEventParams()

    /**
     * Открытие карточки планового отпуска с целью оформить по ссылке
     */
    @Parcelize
    object TakeVacation : CalendarOpenEventParams()
}