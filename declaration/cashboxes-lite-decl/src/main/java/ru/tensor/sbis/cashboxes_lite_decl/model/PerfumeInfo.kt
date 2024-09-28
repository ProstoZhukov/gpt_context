package ru.tensor.sbis.cashboxes_lite_decl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.catalog_decl.catalog.Packs
import ru.tensor.sbis.catalog_decl.catalog.Packs.Companion.convertQuantity
import java.io.Serializable
import java.math.BigDecimal

/**
 * Надор данных о разливных духах
 *
 * @param quantityLeft - остаток для продажи/возврата
 * @param packOfQuantity - упаковка в которой измеряется [quantityLeft]
 * @param isOpenedPerfume - были ли духи частично проданы
 */
@Parcelize
data class PerfumeInfo(
    val quantityLeft: BigDecimal,
    val packOfQuantity: Packs,
    val isOpenedPerfume: Boolean
) : Parcelable, Serializable {

    /**
     * Конвертирует количество, соответствующее ед. измерения переданной упаковки
     */
    fun convertQuantity(packs: Packs?): BigDecimal =
        packOfQuantity.convertQuantity(quantityLeft, packs)
}

/**
 * Были ли разливные духи частично проданы для nullable [PerfumeInfo]
 */
fun PerfumeInfo?.wasOpened() = this?.isOpenedPerfume == true

/**
 * Является ли товар разливными духами
 */
fun PerfumeInfo?.isDraftPerfume() = this != null