package ru.tensor.sbis.communicator.declaration.tab_history

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для фичи сохранения последней открытой вкладки сотрудников/контактов.
 *
 * @author dv.baranov
 */
interface EmployeesContactsTabHistory : Feature {

    /**
     * Сохранить navxId последней открытой вкладки сотрудников/контактов в SharedPreferences.
     */
    fun saveLastSelectedTab(navxId: String)

    /**
     * Получить navxId последней открытой вкладки сотрудников/контактов из SharedPreferences.
     */
    fun getLastSelectedTab(): String
}
