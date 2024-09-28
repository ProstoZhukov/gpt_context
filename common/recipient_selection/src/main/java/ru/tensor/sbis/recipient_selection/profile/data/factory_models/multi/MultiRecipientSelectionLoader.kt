package ru.tensor.sbis.recipient_selection.profile.data.factory_models.multi

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager
import ru.tensor.sbis.recipient_selection.profile.data.PersonSelectorItemModelImpl
import java.util.UUID
import javax.inject.Inject

/**
 * Реализация подстановки уже выбранных получателей при инициализирующей загрузке множественного выбора
 *
 * @author vv.chekurda
 */
internal class MultiRecipientSelectionLoader @Inject constructor(
    private val recipientSelectionResultManager: RecipientSelectionResultManager,
    private val employeeProfileService: DependencyProvider<EmployeeProfileControllerWrapper>
) : MultiSelectionLoader<RecipientSelectorItemModel> {

    override fun loadSelectedItems(): List<RecipientSelectorItemModel> =
        recipientSelectionResultManager.selectionResult.allContacts.mapNotNull { contact ->
            if (contact.isEmptyModel()) {
                getRecipientSelectorItemFromCache(contact.uuid)
                    ?: getDefaultRecipientSelectorItem(contact)
            } else {
                getDefaultRecipientSelectorItem(contact)
            }
        }

    private fun getDefaultRecipientSelectorItem(contact: ContactVM): RecipientSelectorItemModel? {
        if (contact.name == null && contact.data1 == null && contact.data2 == null) return null
        val personData = PersonData(
            contact.uuid,
            contact.rawPhoto,
            contact.name?.let { employeeProfileService.get().getPersonInitialsStubData(it.lastName, it.firstName) }
        )
        return PersonSelectorItemModelImpl(contact, personData)
    }

    private fun getRecipientSelectorItemFromCache(personUuid: UUID): RecipientSelectorItemModel? {
        val employee = employeeProfileService.get().getEmployeeProfileFromCache(personUuid) ?: return null
        val contact = ContactVM().apply {
            uuid = employee.uuid
            rawPhoto = employee.photoUrl
            name = employee.name
            data1 = employee.position
            data2 = employee.companyOrDepartment
            initialsStubData = employee.initialsStubData
        }
        val personData = PersonData(
            contact.uuid,
            contact.rawPhoto,
            employeeProfileService.get().getPersonInitialsStubData(contact.name.lastName, contact.name.firstName)
        )
        return PersonSelectorItemModelImpl(contact, personData)
    }

    private fun ContactVM.isEmptyModel(): Boolean =
        renderedName.isNullOrBlank() && rawPhoto.isNullOrBlank()
}