package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.service

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageReceiverReadStatusControllerCallback
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusController
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusFilter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.META_IS_FIRST_PAGE
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.META_IS_SEARCH_RESULT
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListServiceWrapper
import ru.tensor.sbis.list.base.data.ServiceWrapper
import javax.inject.Inject

/**
 * Реализация обертки над микросервисом списка статусов прочитанности сообщения
 * @see [ServiceWrapper]
 *
 * @property controller микросервис для загрузки списка
 *
 * @author vv.chekurda
 */
internal class ReadStatusListServiceWrapperImpl @Inject constructor(
    private val controller: DependencyProvider<MessageReceiverReadStatusController>,
    private val activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer
) : ReadStatusListServiceWrapper {

    override fun setCallbackAndReturnSubscription(callback: (Map<String, String>) -> Unit): Any =
        controller.get().dataRefreshed().subscribe(object : DataRefreshedMessageReceiverReadStatusControllerCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                callback(param)
            }
        })

    override fun list(filter: MessageReceiverReadStatusFilter): ReadStatusListResult =
        controller.get().list(filter)
            .addMetaData(filter)
            .registryUuidsActivityStatus()

    override fun refresh(filter: MessageReceiverReadStatusFilter, params: Map<String, String>): ReadStatusListResult =
        controller.get().refresh(filter)
            .registryUuidsActivityStatus()

    /**
     * Добавление информации, необходимой для корректного отображения холдера ошибки сети
     */
    private fun ReadStatusListResult.addMetaData(
        filter: MessageReceiverReadStatusFilter
    ): ReadStatusListResult = apply {
        if (metadata == null) metadata = HashMap()
        if (filter.anchor.personUuid == null) {
            metadata!![META_IS_FIRST_PAGE.first] = META_IS_FIRST_PAGE.second
        }
        if (filter.searchString.isNotBlank()) {
            metadata!![META_IS_SEARCH_RESULT.first] = META_IS_SEARCH_RESULT.second
        }
    }

    private fun ReadStatusListResult.registryUuidsActivityStatus(): ReadStatusListResult =
        this.apply {
            activityStatusSubscriptionInitializer.initialize(
                result.map {
                    it.profile.person.uuid
                }
            )
        }
}