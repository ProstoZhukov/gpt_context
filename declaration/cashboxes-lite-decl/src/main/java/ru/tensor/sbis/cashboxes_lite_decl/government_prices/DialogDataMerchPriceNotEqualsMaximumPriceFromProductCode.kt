package ru.tensor.sbis.cashboxes_lite_decl.government_prices

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/** Набор данных для отображения диалога "Цена отличается от максимальной розничной цены". */
@Parcelize
data class DialogDataMerchPriceNotEqualsMaximumPriceFromProductCode(
    val merchandiseName: String,
    val minimalGovernmentPrice: BigDecimal,
    val actualMerchandisePrice: BigDecimal,
    val maximumPriceByProductCode: BigDecimal,
    val isContinueWithViolationActionAvailable: Boolean
) : Parcelable