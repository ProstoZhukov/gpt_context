package ru.tensor.sbis.recipient_selection.employee.di

import android.app.Application
import android.content.Context
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.recipient_selection.employee.EmployeeSelectionPlugin

/**
 * Провайдер di компонентов выбора сотрудников
 *
 * @author vv.chekurda
 */
internal object EmployeeSelectionComponentProvider {

    fun getEmployeesSelectionComponent(application: Application, parameters: EmployeesSelectionFilter): EmployeeSelectionComponent =
        DaggerEmployeeSelectionComponent.builder()
            .parameters(parameters)
            .employeeSelectionSingletonComponent(getEmployeesSelectionSingletonComponent(application))
            .build()

    fun getEmployeesSelectionSingletonComponent(context: Context): EmployeeSelectionSingletonComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return EmployeeSelectionPlugin.singletonComponent
    }

}