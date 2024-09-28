package ru.tensor.sbis.calendar_decl.calendar.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Данные о типе отпуска для новой карточки отпуска
 * @property type тип отпуска
 * @property name название типа отпуска
 * @property planOnly создание только плановых отпусков
 * @property forceFact создание только факт отпуска отпусков
 */
@Parcelize
data class VacationTypeInfo(
    val type: VacationType,
    val name: String,
    val planOnly: Boolean? = null,
    val forceFact: Boolean = false,
    val extReason: Short? = null
) : Parcelable
