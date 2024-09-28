package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.conversation

import android.net.Uri
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.util.share.ConversationUtils
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.BaseThemeRouterDelegate
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.common.navigation.contract.ConversationScreen
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.*
import java.util.UUID

/**
 * Реализация роутера переписки реестра диалогов.
 * @see [ThemeConversationRouter].
 *
 * @author vv.chekurda
 */
internal class ThemeConversationRouterImpl :
    BaseThemeRouterDelegate(),
    ThemeConversationRouter {

    override val topConversation: UUID?
        get() = communicatorThemesRouter?.getTopSubContent()?.let { fragment ->
            if (fragment is ConversationScreen) {
                fragment.conversationUuid
            } else {
                null
            }
        }

    override fun showConversationDetailsScreen(
        params: ConversationDetailsParams,
        onCloseCallback: (() -> Unit)?
    ) {
        communicatorThemesRouter?.showConversationDetailsScreen(params, onCloseCallback)
    }

    override fun openConversationPreview(
        params: ConversationParams,
        list: List<ThemeConversationPreviewMenuAction>
    ) {
        communicatorThemesRouter?.openConversationPreview(params, list)
    }

    override fun showNewDialog(folderUuid: UUID) = safeContext {
        val activityIntent = requireActivity().intent
        val filesToShareList = ConversationUtils.getFilesToShare(activityIntent)
        val textToShare = ConversationUtils.getTextToShare(activityIntent)

        val newConversationIntent = themesRegistryDependency.getNewDialogConversationActivityIntent(
            folderUuid,
            filesToShareList,
            textToShare,
            true
        ).putExtra(IntentAction.Extra.NEED_TO_SHOW_KEYBOARD, true)
        communicatorThemesRouter?.openScreen(newConversationIntent) {
            themesRegistryDependency.getConversationFragment(newConversationIntent.extras!!)
        }
    }

    override fun showNewDialogToShare(participants: List<UUID>, text: String?, files: List<Uri>?) {
        showConversationDetailsScreen(
            ConversationDetailsParams(
                participantsUuids = participants.asArrayList(),
                textToShare = text,
                files = files?.asArrayList()
            )
        )
    }

    override fun showConsultationDetailsScreen(params: CRMConsultationParams, onCloseCallback: (() -> Unit)?) {
        communicatorThemesRouter?.showConsultationDetailsScreen(params, onCloseCallback)
    }
}