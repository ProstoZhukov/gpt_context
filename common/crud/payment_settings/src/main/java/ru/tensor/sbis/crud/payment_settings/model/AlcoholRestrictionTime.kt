package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

import ru.tensor.devices.settings.generated.AlcoholRestrictionTime as ControllerAlcoholRestrictionTime

/** Настройки времени, когда запрещено реализовывать алкоголь. */
@Parcelize
data class AlcoholRestrictionTime(
    val from: Date,
    val till: Date
) : Parcelable

/** @SelfDocumented */
fun ControllerAlcoholRestrictionTime.toAndroid() = AlcoholRestrictionTime(
    from = from,
    till = till
)

/** @SelfDocumented */
fun AlcoholRestrictionTime.toController() = ControllerAlcoholRestrictionTime(from, till)