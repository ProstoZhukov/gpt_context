package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import ru.tensor.sbis.retail_settings.generated.PaymentSettings as ControllerPaymentSettings

/** Дефолтное значение ЕМЦ(единая минимальная цена) табака на случай, если контроллер почему - то не отдал настройку. */
val DEFAULT_TOBACCO_EMC = BigDecimal(129)

/**
 * Модель настроек оплаты и чека.
 *
 * @property markingSettings Настройки маркировки
 * @property saleSettings Настройки продажи
 */
@Parcelize
data class PaymentSettings(
    val markingSettings: MarkingSettings,
    val saleSettings: SaleSettings,
    val minTobaccoPrice: BigDecimal,
    val paywaySettings: PaywaySettings,
    val salarySettings: SalarySettings
) : Parcelable {
    companion object {
        fun stub() = PaymentSettings(
            MarkingSettings.stub(),
            SaleSettings.stub(),
            BigDecimal.ZERO,
            PaywaySettings.stub(),
            SalarySettings.stub()
        )
    }

    /** Доступность оплаты продажи наличными */
    val isPaymentByCashEnabled: Boolean
        get() = paywaySettings.isPaymentByCashEnabled

    /** Включена ли проверка на гос.ограничение цен? */
    val isSkipPriceCheckingEnabled : Boolean
        get() = markingSettings.skipPriceChecking

    /** См.[DefaultAlcoholPrice] */
    val defaultAlcoholPrice : DefaultAlcoholPrice
        get() = markingSettings.defaultAlcoholPrice

    /** См.[GovTobaccoPriceCheckType] */
    val govTobaccoPriceCheckType : GovTobaccoPriceCheckType?
        get() = markingSettings.govTobaccoPriceCheckType

    /** См.[ProblematicCodeProcessType] */
    val problematicCodeProcessType: ProblematicCodeProcessType
        get() = markingSettings.problematicCodeProcessType
}

/** @SelfDocumented */
fun ControllerPaymentSettings.map(): PaymentSettings = PaymentSettings(
    markingSettings.map(),
    saleSettings.map(),
    minTobaccoPrice ?: DEFAULT_TOBACCO_EMC,
    paywaySettings.map(),
    salarySettings.map()
)

/** @SelfDocumented */
fun PaymentSettings.map(): ControllerPaymentSettings = ControllerPaymentSettings(
    markingSettings.map(),
    saleSettings.map(),
    salarySettings.map(),
    minTobaccoPrice,
    paywaySettings.map()
)
