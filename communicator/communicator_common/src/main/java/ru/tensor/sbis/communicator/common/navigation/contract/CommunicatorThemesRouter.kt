package ru.tensor.sbis.communicator.common.navigation.contract

import androidx.annotation.IdRes
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationParams
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.common.navigation.data.CommunicatorArticleDiscussionParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.*
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.info_decl.notification.view.NotificationListViewConfiguration
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Роутер для навигации реестров диалогов и чатов.
 *
 * @author da.zhukov
 */
interface CommunicatorThemesRouter :
    CommunicatorHostRouter,
    DeeplinkActionNode,
    CommunicatorRouterLinkInWebView {

    /**
     * Показать экран переписки.
     * @param params параметры для открытия переписки (диалога/чата).
     * @param onCloseCallback колбэк о закрытии фрагмента.
     */
    fun showConversationDetailsScreen(
        params: ConversationDetailsParams,
        onCloseCallback: (() -> Unit)? = null
    )

    fun openConversationPreview(params: ConversationParams, list: List<ThemeConversationPreviewMenuAction>)

    /**
     * Показать экран переписки по консультации.
     * @param params параметры для открытия переписки (консультации).
     * @param onCloseCallback колбэк о закрытии фрагмента.
     */
    fun showConsultationDetailsScreen(params: CRMConsultationParams, onCloseCallback: (() -> Unit)?)

    /**
     * Показать карточку новости.
     *
     * @param onNewScreen  true, если открыть на новом экране, в ином случае на планшете - details контейнер.
     * @param documentUuid строковый литерал идентификатора документа.
     * @param messageUuid  идентификатор сообщения.
     * @param dialogUuid   идентификатор диалога.
     * @param isReplay     true, если требуется ответ на комментарий.
     * @param showComments true, если требуется открыть карточку на области комментариев.
     */
    fun showNewsDetails(
        onNewScreen: Boolean,
        documentUuid: String?,
        messageUuid: UUID?,
        dialogUuid: UUID?,
        isReplay: Boolean,
        showComments: Boolean
    )

    /**
     * Показать обсуждение статьи.
     *
     * @param params параметры для открытия обсуждения статьи.
     */
    fun showArticleDiscussion(params: CommunicatorArticleDiscussionParams)

    /**
     * Показать профиль сотрудника.
     *
     * @param uuid идентификатор сотрудника.
     */
    fun showProfile(uuid: UUID)

    /**
     * Показать карточку нарушения.
     *
     * @param documentUuid идентификатор документа.
     */
    fun showViolationDetails(documentUuid: UUID?)

    /**
     * Показать экран подтверждения номера телефона.
     *
     * @param registryContainerId контейнер для открытия фрагмента.
     */
    fun showVerificationFragment(@IdRes registryContainerId: Int)

    /**
     * Показать экран со списком уведомлений.
     */
    fun showNotificationListScreen(
        registryContainerId: Int,
        conversationUuid: UUID,
        toolbarTitle: String,
        photoUrl: String,
        configuration: NotificationListViewConfiguration
    )

    /**
     * Поменять выбранный элемент в навигации приложения.
     *
     * @param registryType тип реестра, на который нужно переключиться.
     */
    fun changeNavigationSelectedItem(registryType: CommunicatorRegistryType)

    /**
     * Поставщик роутера [CommunicatorThemesRouter].
     */
    interface Provider : Feature {

        /** @SelfDocumented */
        fun getCommunicatorThemesRouter(): CommunicatorThemesRouter
    }
}