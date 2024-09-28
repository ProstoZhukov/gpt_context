package ru.tensor.sbis.recipient_selection.employee.di

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.recipient_selection.employee.contract.EmployeeSelectionDependency

/**
 * Инициализатор компонента модуля выбора сотрудников
 */
class EmployeeSelectionSingletonComponentInitializer(
        private val dependency: EmployeeSelectionDependency
) : BaseSingletonComponentInitializer<EmployeeSelectionSingletonComponent>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): EmployeeSelectionSingletonComponent {
        return DaggerEmployeeSelectionSingletonComponent.factory().create(dependency, commonSingletonComponent)
    }
}