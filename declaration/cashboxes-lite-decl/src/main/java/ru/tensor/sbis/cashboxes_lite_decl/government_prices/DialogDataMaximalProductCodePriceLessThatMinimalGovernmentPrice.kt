package ru.tensor.sbis.cashboxes_lite_decl.government_prices

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/** Набор данных для отображения диалога "Максимальная розничная цена ниже минимальной по закону". */
@Parcelize
data class DialogDataMaximalProductCodePriceLessThatMinimalGovernmentPrice(
    val merchandiseName: String,
    val minimalGovernmentPrice: BigDecimal,
    val actualMerchandisePrice: BigDecimal,
    val maximumPriceByProductCode: BigDecimal,
    val isContinueWithViolationActionAvailable: Boolean
) : Parcelable