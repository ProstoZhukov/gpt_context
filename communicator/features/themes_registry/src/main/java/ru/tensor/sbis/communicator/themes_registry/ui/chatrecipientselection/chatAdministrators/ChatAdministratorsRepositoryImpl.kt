package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.ChatAdministratorsController
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantMapOfStringString
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*

/** @SelfDocumented */
internal class ChatAdministratorsRepositoryImpl(private val controller: DependencyProvider<ChatAdministratorsController>) :
        ChatAdministratorsRepository {

    override fun list(filter: ChatAdministratorsFilter): ListResultOfThemeParticipantMapOfStringString =
            controller.get().list(filter)

    override fun refresh(filter: ChatAdministratorsFilter): ListResultOfThemeParticipantMapOfStringString =
            controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedChatAdministratorsControllerCallback): Subscription
        = controller.get().dataRefreshed().subscribe(callback)

    override fun addAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>) =
            controller.get().addAdministrators(chatUuid, adminsUuid)

    override fun removeAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>) =
            controller.get().removeAdministrators(chatUuid, adminsUuid)
}
