package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.MarkingSettings as ControllerMarkingSettings

/**
 * Настройки маркировки
 *
 * @property defaultAlcoholPrice цена, которая выставляется у алкоголя, если флаг skipPriceChecking выставлен в true
 * @property defaultTobaccoPrice цена, которая выставляется у табака, если флаг skipPriceChecking выставлен в true
 * @property skipPriceChecking флаг, сигнализирующий, нужно ли проверять МРЦ алкоголя и табака при добавлении в продажу (если true, то проверять не надо)
 * @property isPersonalSettings признак индивидуальности настроек.
 */
@Parcelize
data class MarkingSettings(
    val defaultAlcoholPrice: DefaultAlcoholPrice,
    val defaultTobaccoPrice: DefaultTobaccoPrice,
    val skipPriceChecking: Boolean,
    val showProblematicCodeWarning: Boolean,
    val isPersonalSettings: Boolean,
    val govAlcoPriceCheckType: GovAlcoPriceCheckType?,
    val govTobaccoPriceCheckType: GovTobaccoPriceCheckType?,
    val problematicCodeProcessType: ProblematicCodeProcessType
) : Parcelable {

    companion object {
        fun stub() = MarkingSettings(
            defaultAlcoholPrice = DefaultAlcoholPrice.MINIMUM,
            defaultTobaccoPrice = DefaultTobaccoPrice.MAXIMUM,
            skipPriceChecking = false,
            showProblematicCodeWarning = false,
            isPersonalSettings = false,
            govAlcoPriceCheckType = null,
            govTobaccoPriceCheckType = null,
            problematicCodeProcessType = ProblematicCodeProcessType.DENY
        )
    }
}

/** @SelfDocumented */
fun ControllerMarkingSettings.map(): MarkingSettings = MarkingSettings(
    defaultAlcoholPrice = defaultAlcoholPrice.map(),
    defaultTobaccoPrice = defaultTobaccoPrice.map(),
    skipPriceChecking = skipPriceChecking,
    showProblematicCodeWarning = showProblematicCodeWarning,
    isPersonalSettings = isPersonalSettings,
    govAlcoPriceCheckType = govAlcoholPriceCheckType?.map(),
    govTobaccoPriceCheckType = govTobaccoPriceCheckType?.map(),
    problematicCodeProcessType = problematicCode.map()
)

/** @SelfDocumented */
fun MarkingSettings.map(): ControllerMarkingSettings = ControllerMarkingSettings(
    defaultAlcoholPrice.map(),
    defaultTobaccoPrice.map(),
    skipPriceChecking,
    showProblematicCodeWarning,
    problematicCodeProcessType.map(),
    isPersonalSettings,
    govAlcoPriceCheckType?.map(),
    govTobaccoPriceCheckType?.map()
)