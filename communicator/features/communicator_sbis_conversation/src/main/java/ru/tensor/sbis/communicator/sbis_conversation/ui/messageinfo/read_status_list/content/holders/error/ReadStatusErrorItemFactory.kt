package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.error

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import javax.inject.Inject

/**
 * Фабрика элементов списка для показа ошибок в списке статусов прочитанности
 *
 * @author vv.chekurda
 */
internal interface ReadStatusErrorItemFactory {

    /**
     * Создать элемент ошибки сети
     */
    fun createNetworkErrorItem(): AnyItem
}

/**
 * Реализация фабрики [ReadStatusErrorItemFactory]
 *
 * @property errorViewHolderHelper вспомогательный класс для создания холдеров ошибки
 */
internal class ReadStatusErrorItemFactoryImpl @Inject constructor(
    private val errorViewHolderHelper: ViewHolderHelper<Any, ViewHolder>
) : ReadStatusErrorItemFactory {

    override fun createNetworkErrorItem(): AnyItem {
        val comparable = object : ComparableItem<Any> {
            override fun areTheSame(otherItem: Any): Boolean =
                otherItem.javaClass.simpleName == javaClass.simpleName
        }
        return Item(
            Any(),
            errorViewHolderHelper,
            comparable,
            Options(customSidePadding = true)
        )
    }
}