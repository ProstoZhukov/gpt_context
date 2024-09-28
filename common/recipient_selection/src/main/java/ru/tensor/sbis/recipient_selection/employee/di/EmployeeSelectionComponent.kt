package ru.tensor.sbis.recipient_selection.employee.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.recipient_selection.employee.EmployeeSelectionFragment
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.common.util.di.PerActivity
import ru.tensor.sbis.recipient_selection.employee.EmployeeSelectionPresenter

/**
 * DI компонент выбора сотрудников
 */
@PerActivity
@Component(
        modules = [EmployeeSelectionModule::class],
        dependencies = [EmployeeSelectionSingletonComponent::class]
)
internal interface EmployeeSelectionComponent {

    fun getPresenter(): EmployeeSelectionPresenter

    fun inject(fragment: EmployeeSelectionFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun parameters(parameters: EmployeesSelectionFilter): Builder

        fun employeeSelectionSingletonComponent(component: EmployeeSelectionSingletonComponent): Builder

        fun build(): EmployeeSelectionComponent
    }

}