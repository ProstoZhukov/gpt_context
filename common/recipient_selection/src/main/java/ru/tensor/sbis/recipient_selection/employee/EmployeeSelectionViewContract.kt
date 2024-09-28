package ru.tensor.sbis.recipient_selection.employee

import ru.tensor.sbis.design.stubview.StubViewContent

interface EmployeeSelectionViewContract {

    /**
     * Задать текст шапки
     */
    fun setTitle(title: String?)

    /**
     * Показать заглушку
     */
    fun showStubView(content: StubViewContent?)

    /**
     * Лист обновлен
     */
    fun listUpdated()

}