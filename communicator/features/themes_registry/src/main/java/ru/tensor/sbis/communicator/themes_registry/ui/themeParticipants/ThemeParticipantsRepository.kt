package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants

import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import java.util.*

/**
 * Интерфейс для связи с [ThemeParticipantsController]
 */
internal interface ThemeParticipantsRepository : BaseListRepository<
        ListResultOfThemeParticipantListItemMapOfStringString,
        ThemeParticipantsFilter,
        DataRefreshedThemeParticipantsControllerCallback> {

    /** @SelfDocumented */
    fun addParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>)

    /** @SelfDocumented */
    fun removeParticipants(themeUuid: UUID, participantsUuid: ArrayList<UUID>)

    /** @SelfDocumented */
    fun getCurrentUserUUID(): UUID?
}
