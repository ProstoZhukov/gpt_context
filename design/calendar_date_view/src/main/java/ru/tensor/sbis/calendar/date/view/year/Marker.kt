package ru.tensor.sbis.calendar.date.view.year

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.LocalDate

/**
 * Позиция [date] и [color] цвет маркера
 */
@Parcelize
data class Marker(
    val date: LocalDate,
    val color: Int
): Parcelable
