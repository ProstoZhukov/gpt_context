package ru.tensor.sbis.communication_decl.employeeselection.result


/**
 * Контракт для результата выбора сотрудников
 */
interface EmployeesSelectionResultDataContract {
    /**
     * @return Список моделей выбранных сотрудников и папок для репоста новости
     */
    fun getEmployeesAndFoldersData(): List<EmployeeSelectionData>

    /**
     * @return Список моделей выбранных сотрудников и папок
     */
    fun getEmployeesAndFoldersDataForRepost(): List<RecipientSelectionResultDataForRepostContract>

    /**
     * @see ru.tensor.sbis.mvp.multiselection.data.BaseSelectionResultData
     */
    fun isSuccess(): Boolean
}
