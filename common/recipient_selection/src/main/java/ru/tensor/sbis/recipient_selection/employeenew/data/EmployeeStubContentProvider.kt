package ru.tensor.sbis.recipient_selection.employeenew.data

import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode.NETWORK_ERROR
import ru.tensor.sbis.common.generated.ErrorCode.OTHER_ERROR
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.contract.AllItemsSelected
import ru.tensor.sbis.design.selection.ui.contract.Data
import ru.tensor.sbis.design.selection.ui.contract.NoData
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubInfo
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchResult

/**
 * Провайдер фабрики контента заглушек [StubViewContentFactory] для выбора получателей по сотрудникам
 *
 * @author vv.chekurda
 */
class EmployeeStubContentProvider : SelectorStubContentProvider<EmployeeSearchResult> {

    override fun provideStubViewContentFactory(result: SelectorStubInfo<EmployeeSearchResult>?): StubViewContentFactory =
        {
            requireNotNull(result)
            // TODO: 22.04.2021 Удалить после реализации проверки в контроллере https://online.sbis.ru/opendoc.html?guid=545246cc-dde4-4a16-b952-445f63074eb3
            if (!NetworkUtils.isConnected(it)) {
                StubViewCase.NO_CONNECTION.getContent()
            } else when (result) {
                is Data          -> createStubForData(result.data)
                AllItemsSelected -> createStubForAllItemsSelected()
                NoData           -> createStubContentForAbsentData()
            }
        }

    private fun createStubForData(result: EmployeeSearchResult) =
        if (result.status.errorCode == NETWORK_ERROR)
            StubViewCase.NO_CONNECTION.getContent()
        else
            StubViewCase.NO_FILTER_RESULTS.getContent()
}

private fun createStubForAllItemsSelected() = ImageStubContent(
    StubViewCase.NO_FILTER_RESULTS.imageType,
    R.string.selection_all_items_selected,
    ResourcesCompat.ID_NULL
)

private fun createStubContentForAbsentData() = ImageStubContent(
    StubViewCase.SBIS_ERROR.imageType,
    StubViewCase.SBIS_ERROR.messageRes,
    ResourcesCompat.ID_NULL
)

internal fun createErrorResult(params: Map<String, String>?): EmployeeSearchResult =
    EmployeeSearchResult(
        arrayListOf(),
        null,
        hasMore = false,
        folderSyncCompleted = false,
        status = CommandStatus(if (params?.get(ERROR_TYPE) == NETWORK) NETWORK_ERROR else OTHER_ERROR, "")
    )

internal val Map<String, String>?.containsError: Boolean
    get() = this?.get(EVENT_TYPE) == ERROR

private const val EVENT_TYPE = "event_type"
private const val ERROR_TYPE = "error_type"
private const val ERROR = "error"
private const val NETWORK = "Network"