package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.base.domain.entity.paging.ITEMS_ON_PAGE

/**
 * Реализация подписки [PrefetchCheckFunction], которая используется при отсутствии внешней
 *
 * @author ma.kolpakov
 */
internal class DefaultPrefetchCheckFunction : PrefetchCheckFunction<SelectorItemModel> {

    override fun needToPrefetch(
        selectedItems: List<SelectorItemModel>,
        availableItems: List<SelectorItemModel>
    ): PrefetchMode? {
        return if (availableItems.size < ITEMS_ON_PAGE) PrefetchMode.PREFETCH else null
    }
}