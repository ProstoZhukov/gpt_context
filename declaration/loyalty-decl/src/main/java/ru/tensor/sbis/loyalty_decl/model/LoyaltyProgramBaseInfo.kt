package ru.tensor.sbis.loyalty_decl.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Базовая информация о программе лояльности.
 * @param name Наименование скидки.
 * @param shortDescription  Краткое описание скидки.
 * @param imageUrl URL картинки.
 */
@Parcelize
data class LoyaltyProgramBaseInfo(
    val name: String,
    val shortDescription: String?,
    val imageUrl: String
) : Parcelable, Serializable {

    /** Описание скидки. */
    @IgnoredOnParcel
    val description = if (!shortDescription.isNullOrBlank()) shortDescription else name
}