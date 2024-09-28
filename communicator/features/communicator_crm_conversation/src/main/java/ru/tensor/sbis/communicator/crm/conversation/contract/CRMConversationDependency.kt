package ru.tensor.sbis.communicator.crm.conversation.contract

import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerFeature
import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonDependency
import ru.tensor.sbis.communicator.declaration.crm.CRMHostRouter
import ru.tensor.sbis.communication_decl.crm.CRMChatListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.message_view.contact.MessageViewComponentsFactory
import ru.tensor.sbis.review.decl.ReviewFeature
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Интерфейс внешних зависимостей модуля communicator_crm_conversation.
 *
 * @author da.zhukov
 */
interface CRMConversationDependency :
    CommunicatorCommonDependency,
    ViewerSliderIntentFactory,
    LoginInterface,
    CRMConversationFragmentFactory {

    /** Фабрика для создания вспомогательных компонентов для ячейки сообщения */
    val messageViewComponentsFactory: MessageViewComponentsFactory

    /** Фабрика для создания данных аудиосообщения. */
    val audioMessageViewDataFactory: AudioMessageViewDataFactory?

    /** Фича функционала "пожаловаться". */
    val complainFragmentFeature: ComplainDialogFragmentFeature?

    /** Роутер для реестра чатов CRM.*/
    val crmHostRouterFeatureProvider: CRMHostRouter.Provider?

    /** Фича функционала реестра чатов CRM, для отображения в шторке. */
    val crmChatListFragmentFactory: CRMChatListFragmentFactory?

    /** Утилита для отправки аналитики. */
    val analyticsUtilProvider: AnalyticsUtil.Provider?

    /** Фича оценки приложения. */
    val reviewFeature: ReviewFeature?

    /** Фича проигрывателя медиа сообщений. */
    val mediaPlayerFeature: MediaPlayerFeature?

    /** Фича проверки интернет соединения. */
    val networkUtilsFeature: NetworkUtils?
}