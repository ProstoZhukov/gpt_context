package ru.tensor.sbis.recipient_selection.employee

import android.util.Log
import androidx.core.content.res.ResourcesCompat
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.UUIDUtils.NIL_UUID
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilterKeys
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import ru.tensor.sbis.employees.generated.EmployeeEventData
import ru.tensor.sbis.employees.generated.EmployeeEventType
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.multiselection.MultiSelectionContract
import ru.tensor.sbis.mvp.multiselection.MultiSelectionPresenter
import ru.tensor.sbis.mvp.multiselection.data.BaseFilterKeys
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.mvp.multiselection.data.SelectionFilter
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.employee.data.EmployeeSelectionPagedListResult
import ru.tensor.sbis.recipient_selection.employee.data.ParentFolderData
import ru.tensor.sbis.recipient_selection.employee.ui.EmployeesSelectionResultManager
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesFolderSelectionItem
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesSelectionPathItem
import ru.tensor.sbis.recipient_selection.employee.ui.data.result.EmployeesSelectionResultData
import java.util.UUID
import ru.tensor.sbis.design.design_dialogs.R as RDesignDialogs

@Deprecated("https://online.sbis.ru/opendoc.html?guid=8192fa85-349f-4040-8d28-f850e33b898e")
internal class EmployeeSelectionPresenter(
        private val parameters: EmployeesSelectionFilter,
        interactor: EmployeeSelectionInteractor,
        selectionResultManager: EmployeesSelectionResultManager,
        scrollHelper: ScrollHelper,
        networkUtils: NetworkUtils
) : MultiSelectionPresenter<EmployeeSelectionInteractor, EmployeesSelectionResultManager>(
        Int.MAX_VALUE, parameters.isSingleChoice(), false, networkUtils, scrollHelper, interactor, selectionResultManager
) {

    companion object {
        private const val IS_PROHIBITED_ERROR_VALUE = "Prohibited"
    }

    private var currentFolderUuid: UUID = NIL_UUID
    private var parentFolder: ParentFolderData? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val refreshDisposable = SerialDisposable()
    private var isInitialLoading: Boolean = true

    init {
        createRefreshCallbackSubscription()
        makeSearchRequest()
    }

    private fun createRefreshCallbackSubscription() {
        mInteractor.setDataRefreshCallback()
            .subscribe { event ->
                if (needFolderUpdate(event)) {
                    searchItemsOnRefreshCallback()
                }
            }
            .storeIn(compositeDisposable)
    }

    private fun searchItemsOnRefreshCallback() {
        mIsItemsLoading = true
        refreshDisposable.set(
            mInteractor.searchItems(createRefreshFilter())
                .filter { it.dataList.isNotEmpty() }
                .doOnNext { doOnNext(it) }
                .subscribe(
                    {
                        mIsItemsLoading = false
                        processUpdatingDataListResult(it, false)
                    }, this::onItemsLoadingError
                )
        )
    }

    override fun createFilter(): SelectionFilter {
        return super.createFilter()
                .with(EmployeesSelectionFilterKeys.FOLDER_UUID, currentFolderUuid)
                .with(EmployeesSelectionFilterKeys.NEED_OPEN_PROFILE_ON_PHOTO_CLICK, parameters.needOpenProfileOnPhotoClick())
                .with(EmployeesSelectionFilterKeys.ONLY_WITH_ACCESS_TO_SBIS, parameters.onlyWithAccessToSbis())
                .with(EmployeesSelectionFilterKeys.EXCLUDE_CURRENT_USER, parameters.excludeCurrentUser())
                .with(EmployeesSelectionFilterKeys.CAN_SELECT_FOLDER, parameters.canSelectFolder())
    }

    private fun createRefreshFilter(): SelectionFilter =
        createFilter().apply {
            val lastItemPosition = getInt(BaseFilterKeys.FROM_POSITION)
            val count = if (lastItemPosition != 0) getInt(BaseFilterKeys.FROM_POSITION) else pageSize
            with(BaseFilterKeys.FROM_POSITION, 0)
            with(BaseFilterKeys.ITEMS_COUNT, count)
            with(BaseFilterKeys.FROM_PULL_TO_REFRESH, false)
        }

    override fun onItemClicked(item: MultiSelectionItem, position: Int, isClickedOnItem: Boolean) {
        if (!isClickedOnItem) {
            super.onItemClicked(item, position, isClickedOnItem)
            return
        }

        when (item) {
            is EmployeesSelectionPathItem -> onFolderChanged(item.group.uuid, item.group.groupName)
            is EmployeesFolderSelectionItem -> if (!isFolderChecked(item)) onFolderChanged(item.group.uuid, item.group.groupName)
                                               else super.onItemClicked(item, position, isClickedOnItem)
            else -> super.onItemClicked(item, position, isClickedOnItem)
        }
    }

    private fun isFolderChecked(item: MultiSelectionItem) = mCheckedItems.indexOfFirst { UUIDUtils.equals(it.uuid, item.uuid) } >= 0

    override fun onBackButtonClicked(): Boolean =
        if (!UUIDUtils.equals(currentFolderUuid, NIL_UUID)) {
            onFolderChanged(parentFolder!!.uuid, parentFolder!!.name)
            true
        } else {
            false
        }

    private fun onFolderChanged(selectedFolderUuid: UUID, title: String) {
        currentFolderUuid = selectedFolderUuid
        (mView as? EmployeeSelectionViewContract)?.setTitle(title)
        mView!!.clearSearchQuery()
        makeSearchRequest()
    }

    /**
     * Если среди пришедших uuid-ов будет uuid открытого в данный момент подразделения, то нужно обновить экран
     *
     * @return true - если нужно обновить экран
     */
    private fun needFolderUpdate(event: EmployeeEventData): Boolean {
        Log.e("TAGTAG", "event $event")
        return event.eventType == EmployeeEventType.DATA_REFRESHED || event.folderUuid == currentFolderUuid
    }

    override fun initStartSelectedItemsList() {
        mCheckedItems = LinkedHashSet(mSelectionResultManager.selectionResult.fullList)

        mDisplayedCollection = ArrayList(mCheckedItems)
    }

    override fun putResultInSelectionResultManager(isSuccess: Boolean, selectedItems: List<MultiSelectionItem>) {
        if (isSuccess) {
            mSelectionResultManager.putNewData(EmployeesSelectionResultData(selectedItems))
        } else {
            mSelectionResultManager.putResultCanceled()
        }
    }

    override fun processUpdatingDataListResult(pagedListResult: PagedListResult<MultiSelectionItem>, updatingFromTail: Boolean) {
        (mView as? EmployeeSelectionViewContract)?.listUpdated()
        super.processUpdatingDataListResult(pagedListResult, updatingFromTail)

        val errorCode = pagedListResult.commandStatus?.errorCode
        val errorMessage = pagedListResult.commandStatus?.errorMessage

        val stubView = if (errorCode == ErrorCode.WARNING && errorMessage == IS_PROHIBITED_ERROR_VALUE) {
            ImageStubContent(
                StubViewImageType.ETC,
                R.string.recipient_selection_no_access,
                ResourcesCompat.ID_NULL
            )
        }
        else {
            null
        }

        (mView as? EmployeeSelectionViewContract)?.showStubView(stubView)

        mHasOlderPage = pagedListResult.hasMore()
        if (pagedListResult is EmployeeSelectionPagedListResult) {
            parentFolder = pagedListResult.parentFolder
        }
        if (pagedListResult.dataList.isNotEmpty()) {
            isInitialLoading = false
        }
    }

    override fun processUpdatingDataListError(error: Throwable) {
        (mView as? EmployeeSelectionViewContract)?.listUpdated()
        super.processUpdatingDataListError(error)
    }

    override fun hideProgress() {
        if (!isInitialLoading) {
            super.hideProgress()
        }
    }

    override fun showNotFoundMessage() {
        if (!isInitialLoading) {
            super.showNotFoundMessage()
        }
    }

    override fun showEmptyViewIfNeeded(
        view: MultiSelectionContract.View,
        dataList: MutableList<MultiSelectionItem>?,
        errorMessageRes: Int,
        errorDetailsRes: Int
    ) {
        if (!isInitialLoading) {
            super.showEmptyViewIfNeeded(view, dataList, errorMessageRes, errorDetailsRes)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun getSingleCountRestriction() = RDesignDialogs.string.design_dialogs_recipients_single_count_restriction

    override fun getMaxCountRestriction() = RDesignDialogs.string.design_dialogs_recipients_max_count_restriction

    override fun getItemsNotFoundMessage() = R.string.recipient_selection_employees_not_found

    override fun getAllItemsWasSelectedMessage() = R.string.recipient_selection_all_employees_was_selected

    override fun isCrudSupported() = true
}