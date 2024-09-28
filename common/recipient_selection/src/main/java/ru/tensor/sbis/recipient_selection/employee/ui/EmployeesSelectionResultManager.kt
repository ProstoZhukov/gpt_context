package ru.tensor.sbis.recipient_selection.employee.ui

import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager
import ru.tensor.sbis.mvp.multiselection.data.BaseSelectionResultData
import ru.tensor.sbis.recipient_selection.employee.MutableEmployeesSelectionResultManagerContract
import ru.tensor.sbis.recipient_selection.employee.ui.data.result.EmployeesSelectionResultData


/**
 * Менеджер для получения результата выбора сотрудников
 */
class EmployeesSelectionResultManager : MultiSelectionResultManager<EmployeesSelectionResultData>(),
    MutableEmployeesSelectionResultManagerContract {

    private val EMPTY_RESULT_DATA = EmployeesSelectionResultData(BaseSelectionResultData.RESULT_CLEARED, ArrayList())

    init {
        mSelectionSubject.onNext(EMPTY_RESULT_DATA)
    }

    override fun clearSelectionResult() {
        mSelectionSubject.onNext(EMPTY_RESULT_DATA)
    }

    override fun putResultCanceled() {
        mSelectionSubject.onNext(EmployeesSelectionResultData(BaseSelectionResultData.RESULT_CANCELED, null))
    }
}