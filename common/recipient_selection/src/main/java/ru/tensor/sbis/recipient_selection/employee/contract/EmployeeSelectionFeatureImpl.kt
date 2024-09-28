package ru.tensor.sbis.recipient_selection.employee.contract

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.recipient_selection.employee.EmployeeSelectionFragment
import ru.tensor.sbis.recipient_selection.employee.MutableEmployeesSelectionResultManagerContract
import ru.tensor.sbis.recipient_selection.employee.di.EmployeeSelectionComponentProvider
import ru.tensor.sbis.recipient_selection.employee.ui.EmployeesSelectionResultManager

/**
 * Реализация фичи выбора сотрудников
 *
 * @author vv.chekurda
 */
class EmployeeSelectionFeatureImpl(private val context: Context) : EmployeeSelectionFeature {

    override fun getEmployeesRecipientSelectionActivityIntent(parameters: EmployeesSelectionFilter, context: Context) =
        Intent(ACTION_EMPLOYEE_RECIPIENT_SELECTION_ACTIVITY)
            .setPackage(context.packageName)
            .putExtras(parameters.getBundle())

    //TODO https://online.sbis.ru/opendoc.html?guid=8192fa85-349f-4040-8d28-f850e33b898e
    override fun getEmployeesRecipientSelectionFragment(parameters: EmployeesSelectionFilter): Fragment =
        EmployeeSelectionFragment.newInstance(parameters)

    override fun getEmployeesSelectionResultManager(): EmployeesSelectionResultManager =
        EmployeeSelectionComponentProvider
            .getEmployeesSelectionSingletonComponent(context)
            .getEmployeeSelectionResultManager()

    override fun getMutableEmployeesSelectionResultManager(): MutableEmployeesSelectionResultManagerContract = getEmployeesSelectionResultManager()

    companion object {
        private const val ACTION_EMPLOYEE_RECIPIENT_SELECTION_ACTIVITY =
            BuildConfig.MAIN_APP_ID + ".EMPLOYEE_RECIPIENT_SELECTION_ACTIVITY"
    }
}