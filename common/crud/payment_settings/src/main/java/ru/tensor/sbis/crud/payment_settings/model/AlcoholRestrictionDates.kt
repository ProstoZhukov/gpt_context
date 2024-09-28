package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import ru.tensor.devices.settings.generated.AlcoholRestrictionDates as ControllerAlcoholRestrictionDates

/** Настройки дат (дней), в которые нельзя реализовывать алкоголь. */
@Parcelize
data class AlcoholRestrictionDates(
    val from: Date,
    val till: Date
) : Parcelable

/** @SelfDocumented */
fun ControllerAlcoholRestrictionDates.toAndroid() = AlcoholRestrictionDates(
    from = from,
    till = till
)

/** @SelfDocumented */
fun AlcoholRestrictionDates.toController() =
    ControllerAlcoholRestrictionDates(
        from = from,
        till = till
    )