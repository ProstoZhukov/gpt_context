package ru.tensor.sbis.recipient_selection.employee.di

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.recipient_selection.employee.MutableEmployeesSelectionResultManagerContract

/**
 * Поставщик менеджера результата экрана выбора сотрудников с возможностью изменения
 *
 * @author ra.petrov
 */
interface MutableEmployeesSelectionResultManagerProvider : Feature {

    /**
     * Получить менеджер для получения результата выбора сотрудников с возможностью изменения
     */
    fun getMutableEmployeesSelectionResultManager(): MutableEmployeesSelectionResultManagerContract
}