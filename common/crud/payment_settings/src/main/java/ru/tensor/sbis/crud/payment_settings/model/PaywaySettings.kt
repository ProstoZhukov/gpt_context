package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.PaywaySettings as ControllerPaywaySettings

/**
 * Настройки способа оплаты.
 *
 * @property isPersonalSettings - Индивидуальные или глобальные настройки
 * @property allowCash - Оплата банковской картой.
 * @property requireCashSum - Требовать ввод суммы при оплате наличными.
 * @property allowBank - Оплата банковской картой.
 * @property allowQrCode - Быстрые платежи по QR коду.
 * @property allowNonFiscal - Нефискальные операции.
 * @property allowPartial - Частичная оплата.
 * @property allowInternet - Оплата через Интернет.
 * @property allowExternal - Оплата через внешние агрегаторы.
 * @property allowNonrevenue - Прием платежей без выручки.
 */
@Parcelize
data class PaywaySettings(
    var isPersonalSettings: Boolean,
    var allowCash: Boolean,
    var requireCashSum: Boolean,
    var allowBank: Boolean,
    var allowQrCode: Boolean,
    var allowNonFiscal: Boolean,
    var allowPartial: Boolean,
    var allowInternet: Boolean,
    var allowExternal: Boolean,
    var allowNonrevenue: Boolean
) : Parcelable {
    companion object {
        fun stub() = PaywaySettings(
            isPersonalSettings = false,
            allowCash = false,
            requireCashSum = false,
            allowBank = false,
            allowQrCode = false,
            allowNonFiscal = false,
            allowPartial = false,
            allowInternet = false,
            allowExternal = false,
            allowNonrevenue = false
        )
    }

    /** Доступность оплаты продажи наличными */
    val isPaymentByCashEnabled: Boolean
        get() = allowCash
}

/**
 *  Маппер для преобразования модели контроллера во вью модель.
 */
fun ControllerPaywaySettings.map(): PaywaySettings = PaywaySettings(
    isPersonalSettings,
    allowCash,
    requireCashSum,
    allowBank,
    allowQrCode,
    allowNonFiscal,
    allowPartial,
    allowInternet,
    allowExternal,
    allowNonrevenue
)

/**
 * Маппер для преобразования вью модели в модель контроллера.
 */
fun PaywaySettings.map(): ControllerPaywaySettings = ControllerPaywaySettings(
    isPersonalSettings,
    allowCash,
    requireCashSum,
    allowBank,
    allowQrCode,
    allowNonFiscal,
    allowPartial,
    allowInternet,
    allowExternal,
    allowNonrevenue
)