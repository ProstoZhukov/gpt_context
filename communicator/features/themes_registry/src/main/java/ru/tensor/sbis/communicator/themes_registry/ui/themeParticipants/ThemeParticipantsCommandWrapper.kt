package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import java.util.*
import ru.tensor.sbis.communicator.generated.ThemeParticipant as ControllerThemeParticipant

/**
 * Wrapper команд для контроллера [ControllerThemeParticipant] для работы с участниками диалога/чата
 */
internal interface ThemeParticipantsCommandWrapper {

    /** @SelfDocumented */
    val listCommand: BaseListObservableCommand<PagedListResult<ThemeParticipantListItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback>

    /** @SelfDocumented */
    fun addParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable

    /** @SelfDocumented */
    fun removeParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>): Completable

    /** @SelfDocumented */
    fun getCurrentUserUUID(): Single<UUID>
}
