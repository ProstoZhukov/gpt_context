package ru.tensor.sbis.communicator.contacts_declaration.model.contact.model

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communicator.contacts_declaration.model.contact.ContactProfile
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfileData
import ru.tensor.sbis.profile_service.models.person.PersonData
import java.util.*

/**
 * Реализация модели контакта
 * @see ContactProfile
 *
 * @author vv.chekurda
 */
@Parcelize
data class ContactProfileModel @JvmOverloads constructor(
    override val employee: EmployeeProfile,
    override val isInMyContacts: Boolean = false,
    override val lastMessageDate: Date? = null,
    override val isMyAccountManager: Boolean = false,
    override val nameHighlight: List<Int> = listOf(),
    override val commentHighlight: List<Int> = listOf(),
    override val folderUuid: UUID? = null,
    override val comment: String? = null,
    override val changesIsForbidden: Boolean = false
) : ContactProfile,
    EmployeeProfileData by employee,
    PersonData by employee