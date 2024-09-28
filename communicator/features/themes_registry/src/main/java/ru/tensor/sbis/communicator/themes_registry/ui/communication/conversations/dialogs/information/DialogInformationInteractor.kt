package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information

import io.reactivex.Single
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import java.util.*

/**
 * Интерактор экрана информации о диалоге.
 *
 * @author da.zhukov
 */
internal interface DialogInformationInteractor {

    /** @SelfDocumented */
    val themeParticipantsCommandWrapper: ThemeParticipantsCommandWrapper

    /**
     * Получить модели профилей получателей релевантного сообщения.
     * */
    fun getRelevantMessageReceivers(conversationUuid: UUID): Single<List<ThemeParticipantListItem.ThemeParticipant>>

    /**
     * Получить модели профилей участников переписки.
     * */
    fun getThemeParticipantList(participantsUuids: List<UUID>): Single<List<ThemeParticipantListItem.ThemeParticipant>>

    /**
     * Установить название диалога.
     */
    fun setDialogTitle(dialogUuid: UUID, newTitle: String): Single<CommandStatus>
}