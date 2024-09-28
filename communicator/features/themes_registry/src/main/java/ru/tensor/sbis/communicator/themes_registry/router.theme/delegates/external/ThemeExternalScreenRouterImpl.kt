package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.external

import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListComponentConfig
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.navigation.data.CommunicatorArticleDiscussionParams
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.BaseThemeRouterDelegate
import ru.tensor.sbis.communicator.themes_registry.router.theme.isSingleIncoming
import ru.tensor.sbis.info_decl.notification.view.NotificationListViewConfiguration
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import java.util.*

/**
 * Реализация роутера экранов внешних зависимостей
 * @see [ThemeExternalScreenRouter]
 *
 * @author vv.chekurda
 */
internal class ThemeExternalScreenRouterImpl :
    BaseThemeRouterDelegate(),
    ThemeExternalScreenRouter {

    private val rootViewId: Int
        get() = R.id.communicator_dialog_list_root_view

    override fun showNewsDetails(
        documentUuid: String?,
        messageUuid: UUID?,
        dialogUuid: UUID?,
        isReplay: Boolean,
        showComments: Boolean
    ) {
        communicatorThemesRouter?.showNewsDetails(
            !isTablet,
            documentUuid,
            messageUuid,
            dialogUuid,
            isReplay,
            showComments
        )
    }

    override fun showNewsDetails(conversationModel: ConversationModel) {
        conversationModel.run {
            val documentUuid = UUIDUtils.toString(documentUuid)
            val relevantMessageUuid = socnetServiceObject?.relevantMessageUUID
            val messageUuid: UUID? = relevantMessageUuid?.takeIf {
                isSingleIncoming && isSocnetEvent
            } ?: messageUuid

            val dialogUuid = if (isSingleIncoming) uuid else null
            val isReply = isSingleIncoming && !isSocnetEvent
            val showComments = isSocnetEvent && relevantMessageUuid == null

            showNewsDetails(
                documentUuid,
                messageUuid,
                dialogUuid,
                isReply,
                showComments
            )
        }
    }

    override fun showArticleDiscussion(params: CommunicatorArticleDiscussionParams) {
        communicatorThemesRouter?.showArticleDiscussion(params)
    }

    override fun showViolationDetails(documentUuid: UUID?) {
        communicatorThemesRouter?.showViolationDetails(documentUuid)
    }

    override fun showFolder(attachmentListComponentConfig: DefAttachmentListComponentConfig) {
        val context = requireContext()
        context.startActivity(
            themesRegistryDependency.newAttachmentListViewerIntent(context, attachmentListComponentConfig)
        )
    }

    override fun showViewerSlider(sliderArgs: ViewerSliderArgs) = safeContext {
        startActivity(
            themesRegistryDependency
                .createViewerSliderIntent(
                    requireContext(),
                    sliderArgs
                )
        )
    }

    override fun showPhoneVerification() {
        communicatorThemesRouter?.showVerificationFragment(rootViewId)
    }

    override fun showNotificationListScreen(
        conversationUuid: UUID,
        toolbarTitle: String,
        photoUrl: String,
        configuration: NotificationListViewConfiguration
    ) {
        communicatorThemesRouter?.showNotificationListScreen(
            registryContainerId = rootViewId,
            conversationUuid,
            toolbarTitle,
            photoUrl,
            configuration
        )
    }
}