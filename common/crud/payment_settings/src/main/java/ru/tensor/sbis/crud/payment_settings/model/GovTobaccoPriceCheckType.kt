package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.GovTobaccoPriceCheckType as ControllerGovTobaccoPriceCheckType

/** Тип проверки государственного ограничения цены на табак. */
enum class GovTobaccoPriceCheckType {

    /** Предупреждать, если цена выше МРЦ или ниже ЕМЦ. */
    WARNING,

    /**
     * Эта настройка говорит, что цену для табака нужно брать из маркировки, ничего у пользователя не спрашивая.
     * Однако, если цена ниже ЕМЦ, предупредить пользователя об этом всё же требуется.
     * */
    AUTO_ACCEPT_IF_MORE_EMP
}

/** @SelfDocumented */
fun ControllerGovTobaccoPriceCheckType.map() = when (this) {
    ControllerGovTobaccoPriceCheckType.CONFIRM -> GovTobaccoPriceCheckType.WARNING
    ControllerGovTobaccoPriceCheckType.RECTIFY -> GovTobaccoPriceCheckType.AUTO_ACCEPT_IF_MORE_EMP
}

/** @SelfDocumented */
fun GovTobaccoPriceCheckType.map() = when (this) {
    GovTobaccoPriceCheckType.WARNING -> ControllerGovTobaccoPriceCheckType.CONFIRM
    GovTobaccoPriceCheckType.AUTO_ACCEPT_IF_MORE_EMP -> ControllerGovTobaccoPriceCheckType.RECTIFY
}