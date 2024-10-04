package ru.tensor.sbis.design.selection.ui.contract.list

import io.reactivex.functions.Function3
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Функция дополнения списка кэшированными выбранными ранее элементами
 *
 * @author us.bessonov
 */
internal interface RecentSelectionCachingFunction :
    Function3<List<SelectorItemModel>, List<SelectorItemModel>, Boolean, List<SelectorItemModel>> {

    /** @SelfDocumented */
    fun hasRecentlySelectedItems(): Boolean
}