package ru.tensor.sbis.communicator.common.themes_registry

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания интента на запуск экрана участников чата или диалога
 *
 * @author da.zhukov
 */
interface ConversationParticipantsFactory : Feature {

    /**
     * Создать интент на запуск экрана участников чата или диалога
     */
    fun createConversationParticipantsIntent(
        context: Context,
        conversationUuid: UUID,
        isNewDialog: Boolean,
        isChat: Boolean,
        isDialogInfo: Boolean,
        conversationName: String?,
        permissions: Permissions?,
        participantsUuids: ArrayList<UUID>?
    ): Intent
}