package ru.tensor.sbis.design.selection.ui.list

import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.list.ResultMapper
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.stub.StubContentProviderAdapter
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity

internal class SelectionListScreenEntityFactory<SERVICE_RESULT : Any, FILTER, ANCHOR>(
    private val mapper: ResultMapper<SERVICE_RESULT>,
    private val filterCreator: SelectorFilterCreator<FILTER, ANCHOR>,
    private val prefetchCheckFunction: PrefetchCheckFunction<SelectorItemModel>,
    private val stubContentProvider: StubContentProviderAdapter<SERVICE_RESULT>,
    private val pagingEntity: PagingEntity<ANCHOR, SERVICE_RESULT, FILTER>
) : EntityFactory<SelectionListScreenEntity<SERVICE_RESULT, FILTER, ANCHOR>, SERVICE_RESULT> {

    override fun createEntity() = SelectionListScreenEntity(
        mapper,
        filterCreator,
        prefetchCheckFunction,
        stubContentProvider,
        pagingEntity
    )

    override fun updateEntityWithData(
        page: Int,
        entity: SelectionListScreenEntity<SERVICE_RESULT, FILTER, ANCHOR>,
        serviceResult: SERVICE_RESULT
    ) {
        entity.update(page, serviceResult)
    }
}