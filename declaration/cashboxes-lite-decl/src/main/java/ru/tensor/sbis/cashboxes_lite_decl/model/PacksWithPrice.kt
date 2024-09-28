package ru.tensor.sbis.cashboxes_lite_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.catalog_decl.catalog.Packs
import java.math.BigDecimal

/**
 * Класс для связи конкретной упаковки и её стоимости
 * @property packs модель упаковки
 * @property price цена упаковки
 * @property isSelectedByUser является ли упаковка выбранной упаковкой для номенклатуры?
 */
@Parcelize
data class PacksWithPrice(
    val packs: Packs,
    val price: BigDecimal,
    val calculatedPrice: Double? = null,
    val isSelectedByUser: Boolean = false,
    val manualPrice: BigDecimal? = null
) : Parcelable