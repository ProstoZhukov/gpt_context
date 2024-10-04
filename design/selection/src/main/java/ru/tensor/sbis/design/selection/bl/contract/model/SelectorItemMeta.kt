package ru.tensor.sbis.design.selection.bl.contract.model

import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.vm.selection.SelectionViewModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import java.io.Serializable

/**
 * Информация об элементах меню. Формируется в компоненте выбора
 *
 * @author ma.kolpakov
 */
data class SelectorItemMeta internal constructor(
    internal var isSelected: Boolean = false,
    internal val formattedCounter: String? = null,
    internal var queryRanges: List<IntRange> = emptyList(),
    internal val handleStrategy: ClickHandleStrategy = ClickHandleStrategy.DEFAULT
) : Serializable {
    /**
     * Состояние выбора элемента
     */
    val selected: Boolean get() = isSelected

    /**
     * Тип [ViewHolderHelper] для элемента. Вычисляется только при создании т.к. операция может быть трудоёмкой
     */
    internal lateinit var viewHolderType: Any

    /**
     * Отметка элемента непригодным для дальнейшей работы. Например, при обновлении выбранных
     *
     * @see SelectionViewModel.updateSelection
     */
    internal fun invalidate() {
        queryRanges = emptyList()
    }
}