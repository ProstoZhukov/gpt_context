package ru.tensor.sbis.recipient_selection.employee.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.recipient_selection.employee.ui.EmployeesSelectionResultManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.recipient_selection.employee.contract.EmployeeSelectionDependency

/**
 * DI компонент для внедрения общих зависимостей
 */
@PerApp
@Component(
        modules = [EmployeeSelectionSingletonModule::class],
        dependencies = [CommonSingletonComponent::class]
)
abstract class EmployeeSelectionSingletonComponent {

    internal abstract fun getNetworkUtils(): NetworkUtils

    internal abstract fun getContext(): Context

    internal abstract fun getScrollHelper(): ScrollHelper

    internal abstract val dependency: EmployeeSelectionDependency

    internal abstract fun getEmployeeSelectionResultManager(): EmployeesSelectionResultManager

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance dependency: EmployeeSelectionDependency, commonComponent: CommonSingletonComponent): EmployeeSelectionSingletonComponent
    }
}