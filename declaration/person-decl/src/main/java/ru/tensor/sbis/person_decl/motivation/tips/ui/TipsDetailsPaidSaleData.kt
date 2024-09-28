package ru.tensor.sbis.person_decl.motivation.tips.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Модель данных c набором айди заказа.
 *
 * @param saleId идентификатор заказа.
 * @param tableId идентификатор стола.
 */
@Parcelize
data class TipsDetailsPaidSaleData(
    val saleId: UUID?,
    val tableId: UUID?
) : Parcelable {

    companion object {
        //** SelfDocumented */
        val EMPTY = TipsDetailsPaidSaleData(null, null)
    }
}