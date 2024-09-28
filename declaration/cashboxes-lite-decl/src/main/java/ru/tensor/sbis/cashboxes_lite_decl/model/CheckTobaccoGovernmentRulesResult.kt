package ru.tensor.sbis.cashboxes_lite_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/** Результат проверок табака и его КМ в контексте применимости его к позиции по части МРЦ/ЕМЦ/... */
@Parcelize
sealed interface CheckTobaccoGovernmentRulesResult : Parcelable {

    /** КМ и табак удовлетворяет всем требованиям. */
    @Parcelize
    object Ok : CheckTobaccoGovernmentRulesResult

    /** КМ и табак удовлетворяет всем требованиям, но цену табака необходимо заменить на другую. */
    @Parcelize
    class OkButPriceMustBeOverridden(val overriddenPrice: BigDecimal) : CheckTobaccoGovernmentRulesResult

    /**
     * Цена табака меньше чем ЕМЦ.
     *
     * @param minimalGovernmentPrice ЕМЦ табака.
     * @param isContinueWithViolationAvailable - Доступность возможности игнорировать результат этой проверки.
     * */
    @Parcelize
    class TobaccoPriceLessThatMinimalGovernmentPrice(
        val minimalGovernmentPrice: BigDecimal,
        val isContinueWithViolationAvailable: Boolean
    ) : CheckTobaccoGovernmentRulesResult

    /**
     * МРЦ из КМ меньше, чем ЕМЦ.
     *
     * @param minimalGovernmentPrice ЕМЦ табака.
     * @param maximalPriceFromProductCode МРЦ табака из КМ.
     * @param isContinueWithViolationAvailable - Доступность возможности игнорировать результат этой проверки.
     * */
    @Parcelize
    class MaximalPriceFromProductCodeLessThatMinimalGovernmentPrice(
        val minimalGovernmentPrice: BigDecimal,
        val maximalPriceFromProductCode: BigDecimal,
        val isContinueWithViolationAvailable: Boolean
    ) : CheckTobaccoGovernmentRulesResult

    /**
     * Цена табака не совпадает с МРЦ.
     *
     * @param minimalGovernmentPrice ЕМЦ табака.
     * @param maximalPriceFromProductCode МРЦ табака из КМ.
     * @param isContinueWithViolationAvailable - Доступность возможности игнорировать результат этой проверки.
     * */
    @Parcelize
    class TobaccoPriceNotEqualsMaximalPriceFromProductCode(
        val minimalGovernmentPrice: BigDecimal,
        val maximalPriceFromProductCode: BigDecimal,
        val isContinueWithViolationAvailable: Boolean
    ) : CheckTobaccoGovernmentRulesResult
}