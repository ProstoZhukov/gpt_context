package ru.tensor.sbis.design.selection.ui.contract.recipient

import androidx.annotation.WorkerThread
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.domain.boundary.Repository
import java.io.Serializable

/**
 * Поставщик данных для работы по принципу репозитория (в связке с [Repository])
 *
 * @author ma.kolpakov
 */
interface RecipientSelectorDataProvider<DATA : RecipientSelectorItemModel> : Serializable {

    /**
     * Вызывается когда нужно получить список показываемых моделей
     *
     * @param selected выбранные получатели
     * @param items полный список получателей которые сейчас показываются. Может быть пустым
     * @param searchText поисковая строка
     *
     * @return список всех получателей которые нужно показывать. Должны возвращаться в том числе и выбранные получатели
     */
    @WorkerThread
    fun fetchItems(selected: Set<DATA>, items: List<DATA>, searchText: String): RecipientListModel<DATA>
}