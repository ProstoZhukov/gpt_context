package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees

import io.reactivex.Observable
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactsInteractor
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Интерактор добавления сотрудников внутри компании в реестра контактов.
 *
 * @author da.zhukov
 */
interface AddInternalEmployeesInteractor : AddContactsInteractor {
    /**
     * Поиск сотрудников
     *
     * @param searchString поисковый запрос
     * @param from         с позиции элемента
     * @param count        количество запрашиваемых элементов
     * @return модель результата списка найденных сотрудников
     */
    fun searchInternalEmployees(
        searchString: String,
        from: Int,
        count: Int
    ): Observable<PagedListResult<AddContactModel>>

    /**
     * Загрузка страницы сотрудников
     *
     * @param searchString поисковый запрос
     * @param from         с позиции элемента
     * @param count        количество запрашиваемых элементов
     * @return модель результата списка найденных сотрудников
     */
    fun loadInternalEmployeesPage(
        searchString: String,
        from: Int,
        count: Int
    ): Observable<PagedListResult<AddContactModel>>
}