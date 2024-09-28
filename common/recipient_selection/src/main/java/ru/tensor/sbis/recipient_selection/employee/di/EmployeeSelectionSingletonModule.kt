package ru.tensor.sbis.recipient_selection.employee.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.recipient_selection.employee.ui.EmployeesSelectionResultManager
import ru.tensor.sbis.common.util.di.PerApp

/**
 * Модуль, предоставляющий зависимость для компонента выбора сотрудников:
 * менеджер, отвечающий за результаты выбора
 */
@Module
internal class EmployeeSelectionSingletonModule {

    @Provides
    @PerApp
    fun provideEmployeeSelectionResultManager(): EmployeesSelectionResultManager {
        return EmployeesSelectionResultManager()
    }

}