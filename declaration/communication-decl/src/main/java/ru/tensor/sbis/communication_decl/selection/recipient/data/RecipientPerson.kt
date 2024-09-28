package ru.tensor.sbis.communication_decl.selection.recipient.data

import ru.tensor.sbis.person_decl.profile.model.Person

/**
 * Интерфейс персоны получателя.
 *
 * @property companyOrDepartment Компания, если персона в чужой организации.
 * Подразделение, если в своей организации.
 * null если чистый физик.
 * @property inMyCompany В нашей компании или нет.
 * @property position Должность. null если чистый физик.
 *
 * @author vv.chekurda
 */
interface RecipientPerson : RecipientSelectionItem, Person {
    val companyOrDepartment: String
    val inMyCompany: Boolean
    val position: String?
}