package ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks

import android.os.Parcelable


/**
 * Конфигурация блока контактов для профиля
 *
 * @property videoCallIsAvailable - true, если должен быть элемент управления видеозвонком.
 * @property visibilityStatusIsAvailable - true, если должны быть переключатели управления видимостью контактов
 * @property cityAndCountryIsAvailable - true, если должно отображаться местоположение
 * @property showNotVerifiedContacts - true, если нужно показывать неподтвержденные номера
 * @property canEditVerifiedContacts - true, если можно редактировать подтвержденные номера (необходимо пройти процесс верификации).
 * @property needVerifyAddedPersonalContacts - true, если необходимо верифицировать добавляемые контакты.
 * @property restrictionOnAddingNewContactsIfHasVerifiedContacts - true, если необходимо установить ограничение на добавление новых контактов,
 * при условии что уже есть верифицированные контакты мобильного телефона и электронной почты.
 */
interface ContactsConfiguration : Parcelable {
    val videoCallIsAvailable: Boolean
    val visibilityStatusIsAvailable: Boolean
    val cityAndCountryIsAvailable: Boolean
    val showNotVerifiedContacts: Boolean
    val canEditVerifiedContacts: Boolean
    val needVerifyAddedPersonalContacts: Boolean
    val restrictionOnAddingNewContactsIfHasVerifiedContacts: Boolean
}