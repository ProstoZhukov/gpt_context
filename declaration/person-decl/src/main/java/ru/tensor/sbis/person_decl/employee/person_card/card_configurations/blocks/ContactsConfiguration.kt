package ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks

import android.os.Parcelable

/**
 * Конфигурация списка контактов сотрудника
 * @property availableContacts - список доступных контактов для отображения
 * @property isCountryVisible - видимость страны проживания сотрудника
 * @property isCityVisible - видимость города проживания сотрудника
 *
 * @author ra.temnikov
 */
interface ContactsConfiguration : Parcelable {
    val availableContacts: List<ContactType>
    val isCountryVisible: Boolean
    val isCityVisible: Boolean
}