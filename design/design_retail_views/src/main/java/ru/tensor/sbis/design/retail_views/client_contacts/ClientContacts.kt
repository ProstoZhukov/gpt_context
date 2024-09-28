package ru.tensor.sbis.design.retail_views.client_contacts

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель данных представления контактов клиента.
 * @param clientName Имя клиента.
 * @param phoneField Данные поля телефона.
 * @param emailField Данные поля электронной почты.
 */
@Parcelize
data class ClientContacts(
    val clientName: String,
    val phoneField: SendReceiptField,
    val emailField: SendReceiptField
) : Parcelable {

    /** Модель данные поля ввода с чек-боксом. */
    @Parcelize
    data class SendReceiptField(
        val value: String,
        val checked: Boolean
    ) : Parcelable
}
