package ru.tensor.sbis.recipient_selection.employee.contract

import ru.tensor.sbis.verification_decl.login.CurrentAccount
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper

/**
 * Интерфейс внешних зависимостей выбора сотрудников
 *
 * @author vv.chekurda
 */
interface EmployeeSelectionDependency :
    EmployeesControllerWrapper.Provider,
    CurrentAccount