package ru.tensor.sbis.recipient_selection.employee

import android.content.Context
import io.reactivex.Observable
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilterKeys
import ru.tensor.sbis.employees.generated.EmployeeEventData
import ru.tensor.sbis.employees.generated.EmployeesRefreshedCallback
import ru.tensor.sbis.verification_decl.login.CurrentAccount
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.multiselection.MultiSelectionInteractor
import ru.tensor.sbis.mvp.multiselection.data.BaseFilterKeys
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.mvp.multiselection.data.SelectionFilter
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchFilter
import ru.tensor.sbis.profile_service.models.employee.EmployeeType
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper
import java.util.*

/**
 * Интерактор для работы с выбором сотрудников
 */
internal class EmployeeSelectionInteractor(
    private val context: Context,
    private val employeesControllerWrapper: DependencyProvider<EmployeesControllerWrapper>,
    private val currentAccount: CurrentAccount
) : BaseInteractor(), MultiSelectionInteractor {

    private val currentUserUuid: UUID? by lazy {
        currentAccount.getCurrentAccount()
            ?.personId
            ?.let(UUID::fromString)
    }

    companion object {
        private const val OUR_COMPANY_FILTER = -2L
    }

    private var isFirstSyncCall = true

    /**
     * Функция поиска по списку сотрудников
     */
    override fun searchItems(filter: SelectionFilter): Observable<PagedListResult<MultiSelectionItem>> {
        val searchString = filter.getString(BaseFilterKeys.SEARCH_QUERY)
        val folderUuid = filter.getSerializable(EmployeesSelectionFilterKeys.FOLDER_UUID) as UUID
        val from = filter.getInt(BaseFilterKeys.FROM_POSITION)
        val count = filter.getInt(BaseFilterKeys.ITEMS_COUNT)
        val isFromPullToRefresh = filter.getBoolean(BaseFilterKeys.FROM_PULL_TO_REFRESH)
        val requestRegularSync = isFirstSyncCall
        isFirstSyncCall = false
        val needOpenProfileOnPhotoClick = filter.getBoolean(EmployeesSelectionFilterKeys.NEED_OPEN_PROFILE_ON_PHOTO_CLICK)
        val onlyWithAccess: Boolean = filter.getBoolean(EmployeesSelectionFilterKeys.ONLY_WITH_ACCESS_TO_SBIS)
        val excludeCurrentUser: Boolean = filter.getBoolean(EmployeesSelectionFilterKeys.EXCLUDE_CURRENT_USER)
        val canSelectFolder = filter.getBoolean(EmployeesSelectionFilterKeys.CAN_SELECT_FOLDER)

        return Observable.fromCallable {
            val employeeSearchFilter = EmployeeSearchFilter(
                    OUR_COMPANY_FILTER,
                    if (UUIDUtils.isNilUuid(folderUuid)) null else folderUuid,
                    searchString?.ifBlank { null },
                    EmployeeType.WORKING,
                    from,
                    count,
                    requestRegularSync,
                    onlyWithAccess,
                    if (excludeCurrentUser) currentUserUuid?.let { arrayListOf(it) } else null
            )

            if (isFromPullToRefresh) {
                employeesControllerWrapper.get().list(employeeSearchFilter)
            } else {
                employeesControllerWrapper.get().refresh(employeeSearchFilter)
            }
        }
                .map { employeeSearchResult -> mapEmployeesSearchResult(employeeSearchResult, context, needOpenProfileOnPhotoClick, canSelectFolder) }
                .compose(getObservableBackgroundSchedulers())
    }

    /** @SelfDocumented */
    fun setDataRefreshCallback(): Observable<EmployeeEventData> =
        Observable.create { emitter ->
            val subscription = employeesControllerWrapper.get().setEmployeesRefreshedEvent().subscribe(
                object : EmployeesRefreshedCallback() {
                    override fun onEvent(eventData: EmployeeEventData) {
                        emitter.onNext(eventData)
                    }
                }
            )
            emitter.setCancellable { subscription.disable() }
        }.compose(getObservableBackgroundSchedulers())
}
