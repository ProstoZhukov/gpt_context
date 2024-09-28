package ru.tensor.sbis.recipient_selection.employee.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionAdapter
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.recipient_selection.employee.EmployeeSelectionPresenter
import ru.tensor.sbis.recipient_selection.employee.ui.EmployeesSelectionResultManager
import ru.tensor.sbis.recipient_selection.employee.EmployeeSelectionInteractor
import ru.tensor.sbis.recipient_selection.employee.contract.EmployeeSelectionDependency

/**
 * Модуль, предоставляющий зависмости для компонента выбора сотрудников:
 * interactor, адаптер, презентер
 */
@Module
internal class EmployeeSelectionModule {

    @Provides
    fun provideInteractor(context: Context, employeeSelectionDependency: EmployeeSelectionDependency) =
        EmployeeSelectionInteractor(
            context,
            employeeSelectionDependency.getEmployeesControllerWrapper(),
            employeeSelectionDependency
        )

    @Provides
    fun provideAdapter(filter: EmployeesSelectionFilter) = MultiSelectionAdapter(filter.isSingleChoice())

    @Provides
    fun provideEmployeeSelectionPresenter(parameters: EmployeesSelectionFilter,
                                          interactor: EmployeeSelectionInteractor,
                                          networkUtils: NetworkUtils,
                                          scrollHelper: ScrollHelper,
                                          selectionResultManager: EmployeesSelectionResultManager
    ) = EmployeeSelectionPresenter(parameters, interactor, selectionResultManager, scrollHelper, networkUtils)
}