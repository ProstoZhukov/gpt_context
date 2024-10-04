package ru.tensor.sbis.design.selection.ui.list.recipients

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientListModel
import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientSelectorDataProvider
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntity
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.boundary.Repository

/**
 * Реализация [Repository] для загрузки получателей из [RecipientSelectorDataProvider]
 *
 * @author ma.kolpakov
 */
internal class RecipientRepository<ANCHOR>(
    private val dataProvider: RecipientSelectorDataProvider<RecipientSelectorItemModel>
) : Repository<
        SelectionListScreenEntity<RecipientListModel<RecipientSelectorItemModel>,
            RecipientFilter<*>, ANCHOR>,
        RecipientFilter<*>
        > {

    override fun update(
        entity: SelectionListScreenEntity<RecipientListModel<RecipientSelectorItemModel>, RecipientFilter<*>, ANCHOR>,
        filterProvider: FilterAndPageProvider<RecipientFilter<*>>
    ): Observable<
        SelectionListScreenEntity<RecipientListModel<RecipientSelectorItemModel>, RecipientFilter<*>, ANCHOR>
        > = Observable.fromCallable {
        val filter = filterProvider.getServiceFilter()
        dataProvider.fetchItems(filter.selection, filter.items, filter.searchText)
    }.map {
        synchronized(entity) {
            entity.apply { update(filterProvider.getPageNumber(), it) }
        }
    }

    override fun destroy() = Unit
}
