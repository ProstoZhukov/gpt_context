package ru.tensor.sbis.design.selection.bl.utils

import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.listener.SelectorItemHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Реализация обработчика нажатий на элементы по умолчанию
 *
 * @author us.bessonov
 */
internal class DefaultSelectorItemHandleStrategy<DATA : SelectorItem> : SelectorItemHandleStrategy<DATA> {

    override fun onItemClick(item: DATA): ClickHandleStrategy = ClickHandleStrategy.DEFAULT
}