package ru.tensor.sbis.communicator.contacts_registry.utils

import android.content.Context
import ru.tensor.sbis.communicator.declaration.tab_history.EmployeesContactsTabHistory

/**
 * Реализация фичи для сохранения последней открытой вкладки сотрудников/контактов.
 *
 * @author dv.baranov
 */
internal class EmployeesContactsTabHistoryImpl(context: Context) : EmployeesContactsTabHistory {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun saveLastSelectedTab(navxId: String) =
        prefs.edit().putString(LAST_SELECTED_TAB_VALUE, navxId).apply()

    override fun getLastSelectedTab(): String =
        prefs.getString(LAST_SELECTED_TAB_VALUE, "") ?: ""
}

private const val SHARED_PREFERENCES_NAME = "EMPLOYEES_CONTACTS_TAB_HISTORY"
private const val LAST_SELECTED_TAB_VALUE = "LAST_SELECTED_TAB_VALUE"