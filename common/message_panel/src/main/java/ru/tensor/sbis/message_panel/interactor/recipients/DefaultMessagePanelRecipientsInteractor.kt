package ru.tensor.sbis.message_panel.interactor.recipients

import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.DelegateListMapper
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionData
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientDepartmentItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.message_panel.model.mapper.ContactVMItemMapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper
import java.util.*

/**
 * @author vv.chekurda
 */
class DefaultMessagePanelRecipientsInteractor(
    private val employeeProfileServiceWrapper: DependencyProvider<EmployeeProfileControllerWrapper>,
    private val recipientsServiceWrapper: DependencyProvider<RecipientsController>,
    private val employeesServiceWrapper: DependencyProvider<EmployeesControllerWrapper>?,
    private val contactItemMapper: ContactVMItemMapper
) : MessagePanelRecipientsInteractor {

    private val contactVMListMapper = DelegateListMapper(contactItemMapper)

    override fun loadRecipientModels(selectionData: RecipientSelectionData): Maybe<List<RecipientItem>> =
        Maybe.fromCallable {
            if (employeesServiceWrapper != null) {
                val persons = employeeProfileServiceWrapper.get()
                    .getEmployeeProfilesFromCache(selectionData.personsUuids)
                    .map(contactItemMapper::apply)
                    .map(::RecipientPersonItem)

                val departments = selectionData.departments.map { department ->
                    val departmentPersons = employeeProfileServiceWrapper.get()
                        .getProfilesByGroupUuid(department.uuid)
                        .map(contactItemMapper::apply)
                        .map(::RecipientPersonItem)
                    RecipientDepartmentItem(
                        department.uuid,
                        department.name,
                        departmentPersons
                    )
                }
                departments + persons
            } else {
                employeeProfileServiceWrapper.get()
                    .getEmployeeProfilesFromCache(selectionData.allPersonsUuids)
                    .map(contactItemMapper::apply)
                    .map(::RecipientPersonItem)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun loadRecipientModels(recipients: List<UUID>): Maybe<List<RecipientItem>> =
        Maybe.fromCallable {
            employeeProfileServiceWrapper.get().getEmployeeProfilesFromCache(recipients)
        }
            .map(contactVMListMapper::apply)
            .map { it.map(::RecipientPersonItem) as List<RecipientItem> }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun checkAllMembersSelected(dialogUuid: UUID, selectedRecipients: ArrayList<UUID>): Boolean =
        recipientsServiceWrapper.get().getIsForAllMembers(dialogUuid, selectedRecipients)
}
