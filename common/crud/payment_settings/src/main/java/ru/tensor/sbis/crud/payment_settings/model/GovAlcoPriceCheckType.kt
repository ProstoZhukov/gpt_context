package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.GovAlcoholPriceCheckType as ControllerGovAlcoPriceCheckType

/** Тип проверки государственного ограничения цены на алкоголь. */
enum class GovAlcoPriceCheckType {

    /** Предупреждать. */
    CONFIRM,

    /** Корректировать. */
    RECTIFY
}

/** @SelfDocumented */
fun ControllerGovAlcoPriceCheckType.map() = when (this) {
    ControllerGovAlcoPriceCheckType.CONFIRM -> GovAlcoPriceCheckType.CONFIRM
    ControllerGovAlcoPriceCheckType.RECTIFY -> GovAlcoPriceCheckType.RECTIFY
}

/** @SelfDocumented */
fun GovAlcoPriceCheckType.map() = when (this) {
    GovAlcoPriceCheckType.CONFIRM -> ControllerGovAlcoPriceCheckType.CONFIRM
    GovAlcoPriceCheckType.RECTIFY -> ControllerGovAlcoPriceCheckType.RECTIFY
}