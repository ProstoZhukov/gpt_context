package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import io.reactivex.Completable
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.communicator.generated.ChatAdministratorsController
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.util.*

/**
 * Wrapper команд для контроллера [ChatAdministratorsController] для работы с администраторами чата
 */
internal interface ChatAdministratorsCommandWrapper {

    /** @SelfDocumented */
    val listCommand: BaseListObservableCommand<PagedListResult<ThemeParticipant>, ChatAdministratorsFilter, DataRefreshedChatAdministratorsControllerCallback>

    /** @SelfDocumented */
    fun addAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>): Completable

    /** @SelfDocumented */
    fun removeAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>): Completable
}
