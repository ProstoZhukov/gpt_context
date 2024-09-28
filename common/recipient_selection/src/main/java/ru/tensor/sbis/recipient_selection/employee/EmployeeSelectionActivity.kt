package ru.tensor.sbis.recipient_selection.employee

import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionFilter
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.profile.ui.RecipientSelectionActivity
import ru.tensor.sbis.design.R as RDesign

/**
 * Активити выбора получателей из сотрудников.
 */
internal class EmployeeSelectionActivity : RecipientSelectionActivity() {

    override fun initializeSelectionFragment() {
        var fragment = supportFragmentManager.findFragmentByTag(EmployeeSelectionFragment::class.java.simpleName) as EmployeeSelectionFragment?
        val extras = intent.extras

        if (fragment == null && extras != null) {
            fragment = EmployeeSelectionFragment.newInstance(EmployeesSelectionFilter(extras))
            supportFragmentManager.beginTransaction()
                .replace(contentViewId, fragment, EmployeeSelectionFragment::class.java.simpleName)
                .commit()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(RDesign.anim.nothing, RDesign.anim.right_out)
    }
}