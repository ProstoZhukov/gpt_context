package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract

import io.reactivex.Single
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import java.util.*

/**
 * Интерактор списка участников диалога.
 */
internal interface DialogParticipantsInteractor {

    /** @SelfDocumented */
    val themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper

    /** Получить модели профилей получателей релевантного сообщения */
    fun getRelevantMessageReceivers(conversationUuid: UUID): Single<List<ThemeParticipantListItem.ThemeParticipant>>

    /**
     * Получить модели профилей участников переписки.
     */
    fun getThemeParticipantList(participantsUuids: List<UUID>): Single<List<ThemeParticipantListItem.ThemeParticipant>>
}