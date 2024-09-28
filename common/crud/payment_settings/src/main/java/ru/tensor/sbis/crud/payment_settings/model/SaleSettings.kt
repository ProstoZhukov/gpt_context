package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.ReceiptPrinting
import ru.tensor.sbis.retail_settings.generated.SaleSettings as ControllerSaleSettings

/**
 * Настройки продажи
 *
 * @property allowBonus разрешена ли оплата бонусами
 * @property allowCertificate разрешена ли оплата сертификатом
 * @property allowMixed разрешена ли смешанная оплата
 * @property allowSalary разрешена ли оплата под зарплату
 * @property receiptPrinting тип печати чеков/отчётов на чековой ленте (настройка стала актуальной в связи с тотальным дефицитом чековой ленты в России)
 * @property ignoreRest
 * @property maxNomenclatureCount максимальное количество номенклатур в чеке
 * @property maxNomenclaturePayment максимальная сумма номенклатур в чеке
 * @property maxPositionCount максимальное количество позиций в чеке
 * @property maxSalePayment максимальная стоимость позиции в чеке
 * @property priceType тип цены
 * @property nomenclatureSelect перечисление откуда разрешено выбирать номенклатуры
 * @property salaryIdentification тип оплаты под зарплату
 * @property saleRequireNomenclature признак возможности продажи условных товаров. true - можно любые товары, в том числе условные, false - продавать можно только по каталогу
 * @property shiftClosingWithdraw - автоматическое изъятие наличных при закрытии смены
 * @property negativeRest разрешена ли продажа в минус
 * @property salaryPayment тип оплаты под ЗП
 */
@Parcelize
data class SaleSettings(
    var allowBonus: Boolean,
    val allowCertificate: Boolean,
    val allowDiscountFreePrice: Boolean,
    val allowMixed: Boolean,
    val receiptPrinting: ReceiptPrinting,
    val nonfiscalPrinting: Boolean,
    val openclosePrinting: Boolean,
    val ignoreRest: Boolean,
    var maxNomenclatureCount: SaleLimits,
    var maxNomenclaturePayment: SaleLimits,
    var maxPositionCount: SaleLimits,
    var maxSalePayment: SaleLimits,
    var priceType: PriceType,
    var nomenclatureSelect: NomenclatureSelect,
    val isPersonalSettings: Boolean,
    var saleRequireNomenclature: Boolean,
    var shiftClosingWithdraw: Boolean,
    var negativeRest: NegativeRest,
    var isServiceChargeEnabled: Boolean,
) : Parcelable {

    companion object {
        fun stub() = SaleSettings(
            allowBonus = false,
            allowCertificate = false,
            allowDiscountFreePrice = false,
            allowMixed = false,
            receiptPrinting = ReceiptPrinting.PAPER,
            nonfiscalPrinting = false,
            openclosePrinting = false,
            ignoreRest = false,
            maxNomenclatureCount = SaleLimits(0, SaleLimitsTypes.MAX_NOMENCLATURE_COUNT),
            maxNomenclaturePayment = SaleLimits(0, SaleLimitsTypes.MAX_NOMENCLATURE_PAYMENT),
            maxPositionCount = SaleLimits(0, SaleLimitsTypes.MAX_POSITION_COUNT),
            maxSalePayment = SaleLimits(0, SaleLimitsTypes.MAX_SALE_PAYMENT),
            priceType = PriceType.FIXED_PRICE,
            nomenclatureSelect = NomenclatureSelect.PRICE_AND_CATALOG,
            isPersonalSettings = false,
            saleRequireNomenclature = false,
            shiftClosingWithdraw = false,
            negativeRest = NegativeRest.ALLOW,
            isServiceChargeEnabled = false
        )
    }

    /** Активна ли настройка "Не печатать если электронно"? */
    fun isElectronicPrintReceipt() = receiptPrinting == ReceiptPrinting.ELECTRONIC
}

/** Ограничения продажи. */
@Parcelize
data class SaleLimits(
    val limit: Int,
    private val typeOfLimit: SaleLimitsTypes
) : Parcelable {
    companion object {
        private const val ZERO_QUANTITY_INT = 0
    }
    /** Лимит. При нулевом значении берется максимально возможное. */
    val value: Int
        get() = if (limit == ZERO_QUANTITY_INT) typeOfLimit.limit else limit
}

/**
 * Типы ограничений в продаже.
 */
enum class SaleLimitsTypes(val limit: Int) {
    /** Максимальное количество по позиции в чеке. */
    MAX_NOMENCLATURE_COUNT(99999),
    /** Максимальная сумма по позиции в чеке. */
    MAX_NOMENCLATURE_PAYMENT(99999999),
    /** Максимальное количество позиций в чеке. */
    MAX_POSITION_COUNT(99),
    /** Максимальная сумма чека. */
    MAX_SALE_PAYMENT(99999999)
}

/** @SelfDocumented */
fun ControllerSaleSettings.map(): SaleSettings = SaleSettings(
    allowBonus,
    allowCertificate,
    allowDiscountFreePrice,
    allowMixed,
    receiptPrinting,
    nonfiscalPrinting,
    openclosePrinting,
    ignoreRest,
    SaleLimits(maxNomenclatureCount, SaleLimitsTypes.MAX_NOMENCLATURE_COUNT),
    SaleLimits(maxNomenclaturePayment, SaleLimitsTypes.MAX_SALE_PAYMENT),
    SaleLimits(maxPositionCount, SaleLimitsTypes.MAX_POSITION_COUNT),
    SaleLimits(maxSalePayment, SaleLimitsTypes.MAX_SALE_PAYMENT),
    priceType.map(),
    nomenclatureSelect.map(),
    isPersonalSettings,
    saleRequireNomenclature,
    shiftClosingWithdraw,
    negativeRest.map(),
    isServiceChargeEnabled
)

/** @SelfDocumented */
fun SaleSettings.map(): ControllerSaleSettings = ControllerSaleSettings(
    allowBonus,
    allowCertificate,
    allowMixed,
    receiptPrinting,
    nonfiscalPrinting,
    openclosePrinting,
    ignoreRest,
    maxNomenclatureCount.limit,
    maxNomenclaturePayment.limit,
    maxPositionCount.limit,
    maxSalePayment.limit,
    allowDiscountFreePrice,
    priceType.map(),
    nomenclatureSelect.map(),
    isPersonalSettings,
    saleRequireNomenclature,
    shiftClosingWithdraw,
    negativeRest.map(),
    isServiceChargeEnabled
)
