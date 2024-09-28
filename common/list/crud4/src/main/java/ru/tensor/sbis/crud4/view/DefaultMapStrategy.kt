package ru.tensor.sbis.crud4.view

import androidx.annotation.AnyThread
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Маппер события обновления данных адаптера списка с установкой события нажатия на элемент.
 */
internal class DefaultMapStrategy<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM : Any, IDENTIFIER>(
    var itemMapper: ItemMapper<SOURCE_ITEM, OUTPUT_ITEM, IDENTIFIER>,
    private var actionMapper: DataChangeMapper<SOURCE_ITEM, OUTPUT_ITEM> = DataChangeMapper()
) {
    /**
     * Выполнить маппиинг события.
     */
    @AnyThread
    fun map(action: DataChange<SOURCE_ITEM>, actionDelegate : ItemActionDelegate<SOURCE_ITEM, IDENTIFIER>) = actionMapper.map(
        action,
        mapItem = { item->
            itemMapper.map(item,  actionDelegate)
        }
    )
}