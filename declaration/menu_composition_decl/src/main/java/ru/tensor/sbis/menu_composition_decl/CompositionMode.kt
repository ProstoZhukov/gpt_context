package ru.tensor.sbis.menu_composition_decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Режим открытия комплекта.
 */
@Parcelize
sealed class CompositionMode : Parcelable {

    /** Комплект открыт из карточки заказа. */
    data class CardMode(val saleNomenclatureId: UUID) : CompositionMode()

    /** Комплект открыт из меню. */
    data class MenuMode(val compositionId: String) : CompositionMode()
}