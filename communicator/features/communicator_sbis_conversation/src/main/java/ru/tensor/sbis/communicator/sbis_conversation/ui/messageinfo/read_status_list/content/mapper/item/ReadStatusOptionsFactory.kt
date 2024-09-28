package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.item

import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.list.view.item.Options
import javax.inject.Inject

/**
 * Фабрика опций компонета для элементов списка статусов прочитанности сообщения
 * @see [Options]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusOptionsFactory {

    /**
     * Создать опции
     *
     * @param cppModel модель контроллера статуса прочитанности сообщения
     */
    fun create(cppModel: MessageReceiverReadStatus): Options
}

/**
 * Реализация фабрики опций компонета для элементов списка статусов прочитанности сообщения
 *
 * @property liveData параметры состояния вью-модели
 */
internal class ReadStatusOptionsFactoryImpl @Inject constructor(
    private val liveData: ReadStatusListVMLiveData
) : ReadStatusOptionsFactory {

    override fun create(cppModel: MessageReceiverReadStatus): Options =
        Options(
            clickAction = { liveData.dispatchOnItemClick(cppModel.profile.person.uuid) },
            customSidePadding = true
        )
}

