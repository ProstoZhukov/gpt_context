package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.crud.payment_settings.model.AlcoholSaleSettings
import ru.tensor.sbis.crud.payment_settings.model.MarkingSettings
import ru.tensor.sbis.crud.payment_settings.model.map
import ru.tensor.devices.settings.generated.AlcoMarkingSettings as ControllerAlcoholMarkingSettings

/** Модель настроек продажи алкоголя. */
@Parcelize
data class AlcoholMarkingSettings(
    val alco: AlcoholSaleSettings,
    val marking: MarkingSettings,
    val utms: List<UtmSettings>,
    val jewelryUtmSettings: JewelryUtmSettings?,
    val timeZoneName : String?
) : Parcelable {

    companion object {
        fun stub() = AlcoholMarkingSettings(
            AlcoholSaleSettings.stub(),
            MarkingSettings.stub(),
            listOf(),
            JewelryUtmSettings.stub(),
            null
        )
    }
}

/** @SelfDocumented */
fun ControllerAlcoholMarkingSettings.toAndroidType(): AlcoholMarkingSettings = AlcoholMarkingSettings(
    alco = alco.map(),
    marking = marking.map(),
    utms = utms.map { it.toAndroidType() },
    jewelryUtmSettings = jewelryUtm?.toAndroidType(),
    timeZoneName = timeZoneName
)

/** @SelfDocumented */
fun AlcoholMarkingSettings.toControllerType(): ControllerAlcoholMarkingSettings = ControllerAlcoholMarkingSettings(
    alco = alco.map(),
    marking = marking.map(),
    utms = ArrayList(utms.map { it.toControllerType() }),
    jewelryUtm = jewelryUtmSettings?.toControllerType(),
    timeZoneName = timeZoneName
)