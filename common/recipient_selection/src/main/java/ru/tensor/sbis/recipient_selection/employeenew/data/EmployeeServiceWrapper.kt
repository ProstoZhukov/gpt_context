package ru.tensor.sbis.recipient_selection.employeenew.data

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.employees.generated.EmployeeEventData
import ru.tensor.sbis.employees.generated.EmployeesRefreshedCallback
import ru.tensor.sbis.list.base.data.ServiceWrapper
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchFilter
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchResult
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper
import java.util.*

/**
 * Обертка над микросервисом сотрудников.
 * Держит подписку на [DataRefreshCallback]
 *
 * @author sr.golovkin on 31.07.2020
 */
class EmployeeServiceWrapper(private val controller: DependencyProvider<EmployeesControllerWrapper>): ServiceWrapper<EmployeeSearchResult, EmployeeSearchFilter> {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit) =
        controller.get().setEmployeesRefreshedEvent().subscribeUnmanaged(
            object : EmployeesRefreshedCallback() {
                override fun onEvent(eventData: EmployeeEventData) {
                    callback(emptyMap())
                }
            }
        )

    private val HashMap<String, String>.containsRootFolderChanges get() =
        if (get(EVENT_TYPE_KEY) == UPDATE_VALUE && containsKey(CHANGED_FOLDERS_KEY)) {
            val foldersUuidToUpdate: List<String> = get(CHANGED_FOLDERS_KEY)!!.split(",")
            foldersUuidToUpdate.contains(UPDATE_MY_COMPANY_ROOT_FOLDER_KEY)
        } else {
            false
        }

    override fun list(filter: EmployeeSearchFilter): EmployeeSearchResult {
        return controller.get().list(filter)
    }

    override fun refresh(filter: EmployeeSearchFilter, params: Map<String, String>): EmployeeSearchResult {
        return if (params.containsError) createErrorResult(params)
        else controller.get().refresh(filter)
    }
}

private const val UPDATE_MY_COMPANY_ROOT_FOLDER_KEY = "root_-2"
private const val EVENT_TYPE_KEY = "event_type"
private const val CHANGED_FOLDERS_KEY = "changed_folders"
private const val UPDATE_VALUE = "update"