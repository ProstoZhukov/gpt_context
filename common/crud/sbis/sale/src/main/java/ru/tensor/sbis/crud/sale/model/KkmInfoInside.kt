package ru.tensor.sbis.crud.sale.model

import androidx.annotation.StringRes
import ru.tensor.sbis.crud.sbis.sale.R
import java.util.*

/**
 * Класс содержащий данные о типе фискального накопителя
 */
enum class FiscalVolumeTypeInside {
    UNDEFINED,
    @Suppress("unused")
    FISCAL_MEMORY,
    @Suppress("unused")
    EKLZ,
    @Suppress("unused")
    FISCAL_STORAGE
}

/**
 * Данные о состоянии сессии
 */
enum class SessionStateInside(val value: Int) {
    @Suppress("unused")
    SESSION_UNKNOWN(-1),
    @Suppress("unused")
    SESSION_CLOSED(0),
    @Suppress("unused")
    SESSION_OPENED_24_NOT_EXPIRED(1),
    @Suppress("unused")
    SESSION_OPENED_24_EXPIRED(2),
    @Suppress("unused")
    SESSION_CLOSED_IN_FISCAL_STORAGE_OPENED(3),
    @Suppress("unused")
    SESSION_OPENED_IN_FISCAL_STORAGE_CLOSED(4)
}

/**
 * Данные о поддерживаемых системах налогооблажения в ККМ
 */
enum class TaxSystemsInside(val code: Int, @StringRes val nameRes: Int) {
    @SuppressWarnings("unused")
    NOT_SUPPORTED(-1, R.string.crud_sale_not_supported_tax_system),
    @SuppressWarnings("unused")
    GENERAL(1, R.string.crud_sale_general_tax_system),
    @SuppressWarnings("unused")
    SIMPLIFIED_INCOME(2, R.string.crud_sale_simplified_income_tax_system),
    @SuppressWarnings("unused")
    SIMPLIFIED_INCOME_EXPENDITURE(4, R.string.crud_sale_simplified_income_expenditure_tax_system),
    @SuppressWarnings("unused")
    UNIFIED_AGRICULTURAL(16, R.string.crud_sale_unified_agricultural_tax_system),
    @SuppressWarnings("unused")
    PATENT(32, R.string.crud_sale_patent_tax_system);

    companion object {
        fun fromInt(code: Int?): TaxSystemsInside {
            return values().firstOrNull { it.code == code } ?: NOT_SUPPORTED
        }

        /**
         * Получение списка СНО по бит маске
         */
        fun getTaxSystemsFromBitMask(code: Int?): List<TaxSystemsInside> =
            if (code != null) {
                values().filter { code and it.code == it.code }
            } else {
                listOf()
            }
    }
}

/**
 * Класс содержащий данные о ККМ
 */
class KkmInfoInside(
    val fdLifeState: Int = 0,
    @SuppressWarnings("unused") mModel: String = "",
    @SuppressWarnings("unused") mFirmware: String = "",
    var isFiscalMode: Boolean = false,
    @Suppress("unused")
    var mFiscalVolumeType: FiscalVolumeTypeInside = FiscalVolumeTypeInside.UNDEFINED,
    @SuppressWarnings("unused") mFiscalVolumeSerialNumber: String = "",
    var mSerialNumber: String = "",
    var mRnm: String = "",
    var inn: String? = null,
    var mDateTime: Date? = null,
    @SuppressWarnings("unused") mPassword: String = "",
    @SuppressWarnings("unused") mSessionNumber: Int = 0,
    @SuppressWarnings("unused") mSessionState: SessionStateInside = SessionStateInside.SESSION_CLOSED,
    var taxSystems: List<TaxSystemsInside>? = null,
    var immediateReplacementCryptoCoProcessor: Boolean? = null,
    var cryptoCoProcessorResourceExhaustion: Boolean? = null,
    var fdMemoryOverflow: Boolean? = null,
    var ofdWaitingTimeExceeded: Boolean? = null,
    var fdCriticalError: Boolean? = null,
    var fdExpiryDate: Date? = null,
    @SuppressWarnings("unused") mOnlinePaymentsReady: Boolean? = null,
    @SuppressWarnings("unused") mInternetPaymentsFeature: Boolean? = null,
    @Suppress("unused") val mOfflineModeFeature: Boolean? = null,
    val excisableGoodsFeature: Boolean? = null,
    val isVat20Ready: Boolean? = null,
    val fiscalDocumentFormatVer: Int? = null,
    var errorMsg: String? = null)