package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

import ru.tensor.devices.settings.generated.AlcoholSaleAllowedTime as ControllerAlcoholSaleAllowedTime

/** Настройки временного периода, в который можно реализовывать алкоголь. */
@Parcelize
data class AlcoholSaleAllowedTime(
    val startTime: Date,
    val endTime: Date
) : Parcelable

/** @SelfDocumented */
fun ControllerAlcoholSaleAllowedTime.toAndroid() = AlcoholSaleAllowedTime(
    startTime = from,
    endTime = till
)

/** @SelfDocumented */
fun AlcoholSaleAllowedTime.toController() = ControllerAlcoholSaleAllowedTime(startTime, endTime)