package ru.tensor.sbis.verification_decl.verification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Контакт для верификации.
 * @param confirmationType тип подтверждения контакта
 * @param data сам контакт (email/телефон) который будет отображен пользователю. Строка. Опционален. По умолчанию null.
 * @param uuid уникальный идентификатор контакта. Опционален. По умолчанию null.
 *
 * @author ar.leschev
 */
@Parcelize
data class VerificationContact(
    val confirmationType: ConfirmationType,
    val data: String? = null,
    val uuid: String? = null
) : Parcelable