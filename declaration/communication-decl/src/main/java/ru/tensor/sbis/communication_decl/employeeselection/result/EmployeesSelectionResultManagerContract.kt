package ru.tensor.sbis.communication_decl.employeeselection.result

import io.reactivex.Observable

/**
 * Контракт менеджера для получения результата выбора сотрудников с возможностью изменения
 */
interface EmployeesSelectionResultManagerContract {

    /** @SelfDocumented */
    fun clearSelectionResult()

    /** @SelfDocumented */
    fun putResultCanceled()

    /** @SelfDocumented */
    fun getSelectionDoneObservable(): Observable<out EmployeesSelectionResultDataContract>
}
