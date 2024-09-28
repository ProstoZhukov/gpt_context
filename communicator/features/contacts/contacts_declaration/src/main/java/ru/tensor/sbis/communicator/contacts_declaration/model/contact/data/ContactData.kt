package ru.tensor.sbis.communicator.contacts_declaration.model.contact.data

import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import java.util.*

/**
 * Данные контакта.
 *
 * @property employee           модель информации о персоне сотрудника.
 * @property isInMyContacts     true, если у меня в контактах.
 * @property lastMessageDate    дата-время последнего сообщения с этой персоной.
 * @property isMyAccountManager true, если контакт является менеджером моего контакта.
 * @property nameHighlight      посветка имени при поиске.
 * @property commentHighlight   посветка комментария при поиске.
 * @property folderUuid         UUID родительской папки.
 * @property comment            комментарий к контаку (для полезных контактов).
 * @property changesIsForbidden true, если действия с контактом запрещены (удалить, перенсти в папку и т.п.).
 *
 * @author vv.chekurda
 */
interface ContactData {
    val employee: EmployeeProfile
    val isInMyContacts: Boolean
    val lastMessageDate: Date?
    val isMyAccountManager: Boolean
    val nameHighlight: List<Int>
    val commentHighlight: List<Int>
    val folderUuid: UUID?
    val comment: String?
    val changesIsForbidden: Boolean
}