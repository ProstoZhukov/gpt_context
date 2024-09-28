package ru.tensor.sbis.design.recipient_selection.domain.factory.result

import androidx.annotation.WorkerThread
import io.reactivex.Single
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientDepartment
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPerson
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionData
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionItem
import ru.tensor.sbis.design.recipient_selection.RecipientSelectionPlugin.singletonComponent
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientDepartmentItem
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientPersonItem
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

/**
 * Интерактор для получения модели результата по списку выбранных элементов в компоненте выбора получателей.
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionInteractor @Inject constructor() : BaseInteractor(), Serializable {

    private val employeeProfileController: EmployeeProfileControllerWrapper
        get() = singletonComponent.dependency.employeeProfileControllerWrapper.get()

    /**
     * Получить данные результата компонента выбора получателей по списку результата компонента выбора.
     *
     * @param result список выбранных элементов.
     * @param unfoldDepartments true, если необходимо распаковать папки и выгрузить список получателей из них.
     */
    fun getRecipientSelectionData(
        result: SelectionComponentResult<RecipientItem>,
        unfoldDepartments: Boolean
    ): Single<RecipientSelectionData> =
        Single.fromCallable {
            val recipients = result.items.toRecipients()
            val (persons, departments) = recipients.group()
            val allPersons = if (unfoldDepartments) persons.plus(departments.getRecipientPersons()) else persons
            RecipientSelectionData(
                recipients = recipients,
                allPersons = allPersons,
                appended = result.appended
            )
        }.compose(getSingleBackgroundSchedulers())

    private fun List<RecipientItem>.toRecipients(): List<RecipientSelectionItem> =
        map { item ->
            when (item) {
                is RecipientPersonItem -> item.toRecipientPerson()
                is RecipientDepartmentItem -> item.toRecipientDepartment()
            }
        }

    private fun List<RecipientSelectionItem>.group(): Pair<List<RecipientPerson>, List<RecipientDepartment>> {
        val persons = mutableListOf<RecipientPerson>()
        val departments = mutableListOf<RecipientDepartment>()
        forEach { item ->
            when (item) {
                is RecipientPerson -> persons.add(item)
                is RecipientDepartment -> departments.add(item)
                else -> Timber.e("Unsupported selection result item type = ${item.javaClass.simpleName}")
            }
        }
        return persons to departments
    }

    @WorkerThread
    private fun List<RecipientDepartment>.getRecipientPersons(): List<RecipientPerson> {
        val persons = mutableListOf<RecipientPerson>()
        forEach { department ->
             val departmentPersons = employeeProfileController
                 .getProfilesByGroupUuid(department.uuid)
                 .map { it.toRecipientPerson() }
            persons.addAll(departmentPersons)
        }
        return persons
    }

    private fun RecipientPersonItem.toRecipientPerson(): RecipientPerson =
        RecipientPersonModel(
            uuid = id.uuid,
            faceId = faceId,
            name = personName,
            photoUrl = photoData?.photoUrl,
            initialsStubData = photoData?.initialsStubData,
            companyOrDepartment = subtitle.orEmpty(),
            inMyCompany = isInMyCompany,
            position = position
        )

    private fun EmployeeProfile.toRecipientPerson(): RecipientPerson =
        RecipientPersonModel(
            uuid = uuid,
            faceId = faceId,
            name = name,
            photoUrl = photoUrl,
            initialsStubData = initialsStubData,
            companyOrDepartment = companyOrDepartment,
            inMyCompany = inMyCompany,
            position = position,
            hasAccess = hasAccess
        )

    private fun RecipientDepartmentItem.toRecipientDepartment(): RecipientDepartment =
        RecipientDepartmentModel(
            uuid = id.uuid,
            faceId = faceId,
            name = title,
            chief = subtitle.orEmpty(),
            employeeCount = counter ?: 0
        )
}