package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import java.util.*

/**
 * Интерфейс для связи с [ChatAdministratorsController]
 */
internal interface ChatAdministratorsRepository :
        BaseListRepository<ListResultOfThemeParticipantMapOfStringString, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback> {

    /** @SelfDocumented */
    fun addAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>)

    /** @SelfDocumented */
    fun removeAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>)
}
