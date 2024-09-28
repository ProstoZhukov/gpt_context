package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.item

import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.error.ReadStatusErrorItemFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.ReadStatusVM
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.factory.ReadStatusVMFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.isNetworkError
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.item.AnyItem
import javax.inject.Inject

/**
 * Фабрика binding элментов компонета для списка статусов прочитанности сообщения
 * @see [BindingItem]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusItemFactory {

    /**
     * Создать [AnyItem]
     *
     * @param cppModel модель контроллера статуса прочитанности сообщения
     */
    fun createItem(cppModel: MessageReceiverReadStatus): AnyItem

    /**
     * Создать список [AnyItem] из модели страницы контроллера
     *
     * @param listResult модель результата одной страницы
     */
    fun createItemList(listResult: ReadStatusListResult): List<AnyItem>
}

/**
 * Реализация фабрики binding элментов компонета для списка статусов прочитанности сообщения
 *
 * @property viewHolderHelper вспомогательный класс для создания холдеров списка
 * @property viewModelFactory фабрика моделей списка
 * @property optionsFactory   фабрика опций для элментов списка
 */
internal class ReadStatusBindingItemFactoryImpl @Inject constructor(
    private val viewHolderHelper: DataBindingViewHolderHelper<ReadStatusVM>,
    private val viewModelFactory: ReadStatusVMFactory,
    private val optionsFactory: ReadStatusOptionsFactory,
    private val errorItemFactory: ReadStatusErrorItemFactory,
) : ReadStatusItemFactory {

    override fun createItem(cppModel: MessageReceiverReadStatus): AnyItem {
        val vm = viewModelFactory.create(cppModel)
        return BindingItem(
            data = vm,
            dataBindingViewHolderHelper = viewHolderHelper,
            comparable = vm,
            options = optionsFactory.create(cppModel)
        )
    }

    override fun createItemList(listResult: ReadStatusListResult): List<AnyItem> =
        if (listResult.metadata.isNetworkError) {
            listOf(errorItemFactory.createNetworkErrorItem())
        } else {
            listResult.result.map(::createItem)
        }
}