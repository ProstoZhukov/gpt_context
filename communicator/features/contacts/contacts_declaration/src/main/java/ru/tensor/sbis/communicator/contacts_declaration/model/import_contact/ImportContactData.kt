package ru.tensor.sbis.communicator.contacts_declaration.model.import_contact

import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.generated.PhoneBookCard
import ru.tensor.sbis.communicator.generated.PhoneBookCardContact
import ru.tensor.sbis.communicator.generated.PhoneBookCardContactType

/**
 * Модель контакта для импорта.
 *
 * @property globalId - глобальный, уникальный идентификатор контакта.
 * @property surname фамилия контакта.
 * @property name имя контакта.
 * @property patronymic отчество контакта.
 * @property communications список пар (значение, тип коммуникации [CommunicationType]).
 *
 * @author dv.baranov
 */
data class ImportContactData(
    val globalId: String,
    val surname: String,
    val name: String,
    val patronymic: String,
    val communications: List<Communication> = emptyList(),
)

/** @SelfDocumented */
fun ImportContactData.mapToPhoneBookCard(): PhoneBookCard = PhoneBookCard(
    globalId,
    surname,
    name,
    patronymic,
    communications.filter { it.second != CommunicationType.NONE }.map {
        PhoneBookCardContact(
            globalId,
            it.second.toPhoneBookCardContactType(),
            it.first,
        )
    }.asArrayList(),
)

/**
 * Тип коммуникации с контактом.
 */
enum class CommunicationType {

    /** Личный номер. */
    MOBILE_PHONE,

    /** Домашний номер. */
    HOME_PHONE,

    /** Рабочий номер. */
    WORK_PHONE,

    /** Почта. */
    EMAIL,

    /** Тип, не подходящий под вышеперечисленные. */
    NONE,
}

private fun CommunicationType.toPhoneBookCardContactType() = when (this) {
    CommunicationType.MOBILE_PHONE -> PhoneBookCardContactType.MOBILE_PHONE
    CommunicationType.HOME_PHONE -> PhoneBookCardContactType.HOME_PHONE
    CommunicationType.WORK_PHONE -> PhoneBookCardContactType.WORK_PHONE
    CommunicationType.EMAIL -> PhoneBookCardContactType.EMAIL
    CommunicationType.NONE -> PhoneBookCardContactType.EMAIL
}

/** Пара (значение, тип коммуникации [CommunicationType]) для импорта контактов. */
typealias Communication = Pair<String, CommunicationType>
