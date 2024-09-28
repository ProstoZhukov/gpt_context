package ru.tensor.sbis.recipient_selection.employee

import ru.tensor.sbis.communication_decl.employeeselection.result.EmployeesSelectionResultManagerContract
import ru.tensor.sbis.recipient_selection.employee.ui.data.result.EmployeesSelectionResultData

/**
 *  Менеджер для получения результата выбора сотрудников с возможностью изменения
 *  @see ru.tensor.sbis.recipient_selection.employee.contract.EmployeeSelectionFeature
 */
interface MutableEmployeesSelectionResultManagerContract : EmployeesSelectionResultManagerContract {
    /**
     * Добавить данные в результат
     * @see ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager
     * @param employeesSelectionResultData Результат выбора сотрудников
     */
    fun putNewData(employeesSelectionResultData: EmployeesSelectionResultData?)
}