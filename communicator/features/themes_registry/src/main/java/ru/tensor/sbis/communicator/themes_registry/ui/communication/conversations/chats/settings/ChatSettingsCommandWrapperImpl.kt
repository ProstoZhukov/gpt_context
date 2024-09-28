package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeParticipantsController
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsRepository
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.util.*

/**
 * Реализация wrapper-a команд для контроллера [ThemeParticipantsController] для работы с администраторами чата.
 *
 * @author dv.baranov
 */
internal class ChatSettingsCommandWrapperImpl(
    private val repository: ThemeParticipantsRepository,
    override val listCommand: BaseListObservableCommand<PagedListResult<ChatSettingsItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>
) :
    ChatSettingsCommandWrapper,
    BaseInteractor() {

    override fun addParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable =
        Completable.fromCallable { repository.addParticipants(themeUuid, participantsUuid) }
            .compose(completableBackgroundSchedulers)

    override fun removeParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable =
        Completable.fromCallable { repository.removeParticipants(themeUuid, participantsUuid) }
            .compose(completableBackgroundSchedulers)

    override fun getCurrentUserUUID(): Single<UUID> =
        Single.fromCallable<UUID> { repository.getCurrentUserUUID() }
            .compose(getSingleBackgroundSchedulers())
}
