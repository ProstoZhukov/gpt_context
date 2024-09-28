package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.communicator.generated.ChatAdministratorsController
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.util.*

/**
 * Wrapper команд для контроллера [ChatAdministratorsController] для работы с администраторами чата.
 *
 * @author dv.baranov
 */
internal interface ChatSettingsCommandWrapper {

    /** @SelfDocumented */
    val listCommand: BaseListObservableCommand<PagedListResult<ChatSettingsItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>

    /** @SelfDocumented */
    fun addParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable

    /** @SelfDocumented */
    fun removeParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable

    /** @SelfDocumented */
    fun getCurrentUserUUID(): Single<UUID>
}
