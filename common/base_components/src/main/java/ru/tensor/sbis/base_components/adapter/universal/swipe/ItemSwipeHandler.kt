package ru.tensor.sbis.base_components.adapter.universal.swipe

import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem

/**
 * Обработчик свайпа элемента списка
 *
 * @author am.boldinov
 */
interface ItemSwipeHandler {

    /**
     * Вызывается после удаления элемента списка, как по свайпу так и по кнопке меню
     *
     * @param itemTypeId - идентификатор элемента списка, основанный на [UniversalBindingItem.itemTypeId]
     */
    fun onItemRemoved(itemTypeId: String)
}