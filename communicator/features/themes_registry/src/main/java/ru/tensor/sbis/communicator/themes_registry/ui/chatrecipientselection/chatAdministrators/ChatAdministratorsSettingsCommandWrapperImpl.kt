package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import io.reactivex.Completable
import ru.tensor.sbis.communicator.generated.ChatAdministratorsController
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.util.*

/**
 * Реализация Wrapper-а команд [ChatAdministratorsSettingsCommandWrapper] для работы с администраторами чата.
 *
 * @author dv.baranov
 */
internal class ChatAdministratorsSettingsCommandWrapperImpl(
    private val repository: ChatAdministratorsRepository,
    override val listCommand: BaseListObservableCommand<
            PagedListResult<ChatSettingsItem>,
            ChatAdministratorsFilter,
            DataRefreshedChatAdministratorsControllerCallback>
) : ChatAdministratorsSettingsCommandWrapper,
    BaseInteractor() {

    /** @SelfDocumented */
    override fun addAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>): Completable =
        Completable.fromRunnable { repository.addAdministrators(chatUuid, adminsUuid) }
            .compose(completableBackgroundSchedulers)

    /** @SelfDocumented */
    override fun removeAdministrators(chatUuid: UUID, adminsUuid: ArrayList<UUID>): Completable =
        Completable.fromRunnable { repository.removeAdministrators(chatUuid, adminsUuid) }
            .compose(completableBackgroundSchedulers)
}