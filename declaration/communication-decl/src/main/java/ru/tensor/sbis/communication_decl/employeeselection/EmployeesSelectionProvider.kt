package ru.tensor.sbis.communication_decl.employeeselection

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.plugin_struct.feature.Feature

interface EmployeesSelectionProvider : Feature {

    /**
     * Предоставляет intent экрана выбора сотрудников для репоста новости
     *
     * @param context
     * @return intent для EmployeesSelectionActivity
     */
    fun getEmployeesRecipientSelectionActivityIntent(parameters: EmployeesSelectionFilter, context: Context): Intent

    fun getEmployeesRecipientSelectionFragment(parameters: EmployeesSelectionFilter): Fragment
}