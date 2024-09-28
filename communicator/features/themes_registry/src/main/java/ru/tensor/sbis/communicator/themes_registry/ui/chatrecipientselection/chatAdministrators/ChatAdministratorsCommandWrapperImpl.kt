package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators

import io.reactivex.Completable
import ru.tensor.sbis.communicator.generated.ChatAdministratorsFilter
import ru.tensor.sbis.communicator.generated.DataRefreshedChatAdministratorsControllerCallback
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.util.*

/** @SelfDocumented */
internal class ChatAdministratorsCommandWrapperImpl(
    private val repository: ChatAdministratorsRepository,
    override val listCommand: BaseListObservableCommand<
            PagedListResult<ThemeParticipant>,
            ChatAdministratorsFilter,
            DataRefreshedChatAdministratorsControllerCallback>
) : ChatAdministratorsCommandWrapper,
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
