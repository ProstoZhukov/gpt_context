package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.external

import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListComponentConfig
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.navigation.data.CommunicatorArticleDiscussionParams
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import ru.tensor.sbis.info_decl.notification.view.NotificationListViewConfiguration
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import java.util.*

/**
 * Роутер экранов внешних зависимостей реестра диалогов
 *
 * @author vv.chekurda
 */
internal interface ThemeExternalScreenRouter :
    ThemeRouterInitializer {

    /**
     * Показать карточку новости
     *
     * @param documentUuid строковый литерал идентификатора документа
     * @param messageUuid  идентификатор сообщения
     * @param dialogUuid   идентификатор диалога
     * @param isReplay     true, если требуется ответ на комментарий
     * @param showComments true, если требуется открыть карточку на области комментариев
     */
    fun showNewsDetails(
        documentUuid: String?,
        messageUuid: UUID?,
        dialogUuid: UUID?,
        isReplay: Boolean,
        showComments: Boolean
    )

    /**
     * Показать карточку новости
     *
     * @param conversationModel модель переписки
     */
    fun showNewsDetails(conversationModel: ConversationModel)

    /**
     * Показать обсуждение статьи
     *
     * @param params параметры для открытия обсуждения статьи
     */
    fun showArticleDiscussion(params: CommunicatorArticleDiscussionParams)

    /**
     * Показать карточку нарушения
     *
     * @param documentUuid идентификатор документа
     */
    fun showViolationDetails(documentUuid: UUID?)

    /**
     * Показать папку диска
     */
    fun showFolder(attachmentListComponentConfig: DefAttachmentListComponentConfig)

    /**
     * Показать просмотрщик вложений
     *
     * @param sliderArgs аргументы просмотрщика
     */
    fun showViewerSlider(sliderArgs: ViewerSliderArgs)

    /**
     * Показать экран подтверждения номера телефона
     */
    fun showPhoneVerification()

    /**
     * Показать экран со списком уведомлений.
     */
    fun showNotificationListScreen(
        conversationUuid: UUID,
        toolbarTitle: String,
        photoUrl: String,
        configuration: NotificationListViewConfiguration = NotificationListViewConfiguration.defaultConfiguration()
    )
}