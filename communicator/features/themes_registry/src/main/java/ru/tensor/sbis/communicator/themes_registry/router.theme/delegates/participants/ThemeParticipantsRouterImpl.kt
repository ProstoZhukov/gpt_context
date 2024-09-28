package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.participants

import android.content.Intent
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider.Companion.DIALOG_PARTICIPANTS_ACTIVITY_CODE
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.BaseThemeRouterDelegate
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.ConversationParticipantsActivity
import java.util.UUID

/**
 * Реализация роутера участников переписки реестра диалогов
 *
 * @author vv.chekurda
 */
internal class ThemeParticipantsRouterImpl :
    BaseThemeRouterDelegate(),
    ThemeParticipantsRouter {

    /** @SelfDocumented */
    override fun showProfile(uuid: UUID) {
        communicatorThemesRouter?.showProfile(uuid)
    }

    /** @SelfDocumented */
    override fun showConversationMembers(conversation: ConversationModel) = safeContext {
        val activityIntent = Intent(requireContext(), ConversationParticipantsActivity::class.java).apply {
            putExtra(ConversationParticipantsActivity.KEY_DIALOG_UUID, conversation.uuid)
            putExtra(ConversationParticipantsActivity.KEY_NEW_DIALOG, false)
            putExtra(ConversationParticipantsActivity.KEY_IS_CHAT, false)
            putExtra(ConversationParticipantsActivity.KEY_NEW_DIALOG, false)
            putExtra(ConversationParticipantsActivity.KEY_IS_FROM_COLLAGE, true)
        }
        startActivityForResult(activityIntent, DIALOG_PARTICIPANTS_ACTIVITY_CODE)
    }
}