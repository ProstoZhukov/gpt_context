package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.tensor.devices.settings.generated.TimeZone

/**
 * Модель временной зоны
 */
@Parcelize
data class TimeZoneInside(
    val code: String,
    val name: String,
    val nameRusFull: String,
    val nameRusSmall: String,
    val offset: Int
) : Parcelable

/** @SelfDocumented */
fun TimeZone.map() = TimeZoneInside(
    code,
    name,
    nameRusFull,
    nameRusSmall,
    offset
)

/** @SelfDocumented */
fun TimeZoneInside.map() =
    TimeZone(
        code,
        name,
        nameRusFull,
        nameRusSmall,
        offset
    )