package ru.tensor.sbis.recipient_selection.employeenew.data

import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.design.selection.ui.utils.getFilterPageStartIndexForMeta
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchResult

/**
 * Хелпер для обработки результата списочных методов и поддержки постраничной загрузки
 *
 * @author sr.golovkin on 01.08.2020
 */
class EmployeesResultHelper: ResultHelper<Int, EmployeeSearchResult> {

    override fun hasNext(result: EmployeeSearchResult): Boolean {
        return result.let { it.hasMore || (it.result.isEmpty() && it.folderSyncCompleted.not()) }
    }

    override fun isEmpty(result: EmployeeSearchResult): Boolean {
        return result.result.isEmpty()
    }

    override fun isStub(result: EmployeeSearchResult): Boolean {
        return result.status.errorCode != ErrorCode.SUCCESS
    }

    /**
     * Не фактический якорь, а значение, используемое при его определении в [getFilterPageStartIndexForMeta]
     */
    override fun getAnchorForNextPage(result: EmployeeSearchResult): Int = result.result.size

    override fun getAnchorForPreviousPage(result: EmployeeSearchResult): Int? = null
}