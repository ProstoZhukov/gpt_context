package ru.tensor.sbis.communication_decl.employeeselection

import ru.tensor.sbis.communication_decl.employeeselection.result.EmployeesSelectionResultManagerContract
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик менеджера результата экрана выбора сотрудников
 *
 * @author vv.chekurda
 */
interface EmployeesSelectionResultManagerProvider : Feature {

    /**
     * Получить менеджер для получения результата выбора сотрудников
     */
    fun getEmployeesSelectionResultManager(): EmployeesSelectionResultManagerContract
}