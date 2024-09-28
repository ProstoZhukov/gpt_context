package ru.tensor.sbis.loyalty_decl.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.math.BigDecimal
import java.util.UUID

/**
 * Модель скидки.
 * @param uuid - UUID скидки
 * @param name - Наименование скидки
 * @param shortDescription - Краткое описание скидки
 * @param amount - Сумма скидки
 * @param isAutoApplied - Признак автоматической скидки. Пользователь не может убрать автоматическую скидку самостоятельно
 * @param isApplied - Победила ли скидка в конкурсе скидок. Если скидка проиграла, то мы не показываем пользователю её цену, т.к она не
 * несёт в себе реального влияния на итоговую цену
 * @param isRounding - Признак скидки округления. Пользователь не может открыть карточку с информацией об этой скидке
 * @param paymentType - Битовая маска для определения типа скидки
 * @param imageUrl - URL картинки
 */
@Parcelize
data class LoyaltyProgram(
    val uuid: UUID?,
    val name: String,
    val shortDescription: String?,
    val amount: BigDecimal,
    val isAutoApplied: Boolean,
    val isApplied: Boolean,
    val isRounding: Boolean,
    val paymentType: Int,
    val imageUrl: String,
    val loyaltyProgramType: LoyaltyProgramType
) : Parcelable, Serializable {

    /** Описание скидки. */
    @IgnoredOnParcel
    val description = if (!shortDescription.isNullOrBlank()) shortDescription else name

    /** @SelfDocumented */
    fun toBaseInfo() = LoyaltyProgramBaseInfo(
        name = name,
        shortDescription = shortDescription,
        imageUrl = imageUrl
    )
}