package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.Date
import ru.tensor.devices.settings.generated.AlcoholSaleTemporaryRestriction as ControllerAlcoholSaleTemporaryRestriction

/** @SelfDocumented */
@Parcelize
data class AlcoholSaleTemporaryRestriction(
    val time: AlcoholRestrictionTime?,
    val dates: AlcoholRestrictionDates?,
    val volumeFrom: BigDecimal?
) : Parcelable

/** @SelfDocumented */
fun ControllerAlcoholSaleTemporaryRestriction.toAndroid() = AlcoholSaleTemporaryRestriction(
    time = time?.toAndroid(),
    dates = dates?.toAndroid(),
    volumeFrom = volumeFrom
)

/** @SelfDocumented */
fun AlcoholSaleTemporaryRestriction.toController() = ControllerAlcoholSaleTemporaryRestriction(
    time = time?.toController(),
    dates = dates?.toController(),
    volumeFrom = volumeFrom
)