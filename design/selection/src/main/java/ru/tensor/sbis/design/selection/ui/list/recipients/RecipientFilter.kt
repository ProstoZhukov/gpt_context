package ru.tensor.sbis.design.selection.ui.list.recipients

import ru.tensor.sbis.design.selection.ui.contract.recipient.RecipientSelectorDataProvider
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel

/**
 * Модель фильтра для передачи параметров в [RecipientSelectorDataProvider.fetchItems]
 *
 * @author ma.kolpakov
 */
internal data class RecipientFilter<out DATA : RecipientSelectorItemModel>(
    val selection: Set<DATA>,
    val items: List<DATA>,
    val searchText: String
)