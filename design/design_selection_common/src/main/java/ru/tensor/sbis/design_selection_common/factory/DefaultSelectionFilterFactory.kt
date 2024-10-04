package ru.tensor.sbis.design_selection_common.factory

import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterMeta
import ru.tensor.sbis.recipients.generated.RecipientFilter
import ru.tensor.sbis.recipients.generated.RecipientId

/**
 * Фабрика фильтров контроллера компонента выбора.
 *
 * @author vv.chekurda
 */
class DefaultSelectionFilterFactory constructor(
    mapper: SelectionItemMapper<*, *, *, *>
) : SelectionFilterFactory<Any, SelectionItemId> {

    @Suppress("UNCHECKED_CAST")
    private val mapper = mapper as SelectionItemMapper<*, RecipientId, *, SelectionItemId>

    override fun createFilter(meta: SelectionFilterMeta<SelectionItemId>): Any =
        RecipientFilter(
            meta.query,
            meta.filterItemId?.let(mapper::getId)
        )
}