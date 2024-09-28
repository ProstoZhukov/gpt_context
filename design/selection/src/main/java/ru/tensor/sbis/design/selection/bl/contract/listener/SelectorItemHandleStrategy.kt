package ru.tensor.sbis.design.selection.bl.contract.listener

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import java.io.Serializable

/**
 * Пользовательский обработчик нажатий на элементы
 *
 * @author us.bessonov
 */
interface SelectorItemHandleStrategy<DATA : SelectorItem> : Serializable {

    /**
     * Вызывается при нажатии на элемент списка. Должен возвращать значение, определяющее обработку клика компонентом
     */
    fun onItemClick(item: DATA): ClickHandleStrategy
}