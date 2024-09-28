package ru.tensor.sbis.recipient_selection.employee.contract

import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionProvider
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionResultManagerProvider
import ru.tensor.sbis.recipient_selection.employee.di.MutableEmployeesSelectionResultManagerProvider

/**
 * API выбора сотрудников модуля recipient_selection
 *
 * @author vv.chekurda
 */
interface EmployeeSelectionFeature : EmployeesSelectionProvider,
    EmployeesSelectionResultManagerProvider, MutableEmployeesSelectionResultManagerProvider