package ru.tensor.sbis.recipient_selection.profile.data.factory_models.repost

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.ui.contract.MultiSelectionLoader
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.recipient_selection.profile.data.PersonSelectorItemModelImpl
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager

/**
 * Реализация подстановки уже выбранных контактов для репоста при инициализирующей загрузке
 *
 * @author vv.chekurda
 */
internal class ContactSelectionForRepostLoader constructor(
    private val contactSelectionResultManager: RecipientSelectionResultManager,
    private val employeeProfileService: DependencyProvider<EmployeeProfileControllerWrapper>
) : MultiSelectionLoader<RecipientSelectorItemModel> {

    override fun loadSelectedItems(): List<RecipientSelectorItemModel> =
        contactSelectionResultManager.selectionResult.allContacts.map { contact ->
            val personData = PersonData(
                contact.uuid,
                contact.rawPhoto,
                employeeProfileService.get().getPersonInitialsStubData(contact.name.lastName, contact.name.firstName)
            )
            PersonSelectorItemModelImpl(contact, personData)
        }
}