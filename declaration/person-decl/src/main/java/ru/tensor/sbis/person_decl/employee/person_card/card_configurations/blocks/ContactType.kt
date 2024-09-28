package ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks

/**
 * Возможные типы контактов в карточке сотрудника
 * @property PHONE - обычный телефон, без конкретной привязки
 * @property WORK_PHONE @SelfDocumented
 * @property MOBILE_PHONE @SelfDocumented
 * @property HOME_PHONE @SelfDocumented
 * @property EMAIL @SelfDocumented
 * @property SKYPE @SelfDocumented
 * @property TELEGRAM @SelfDocumented
 * @property INSTAGRAM @SelfDocumented
 * @property OTHER - другой тип контактов, не перечисленный выше
 *
 * @author ra.temnikov
 */
enum class ContactType {
    PHONE,
    WORK_PHONE,
    MOBILE_PHONE,
    HOME_PHONE,
    EMAIL,
    SKYPE,
    TELEGRAM,
    INSTAGRAM,
    OTHER
}