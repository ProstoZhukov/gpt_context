package ru.tensor.sbis.communicator.common.navigation.contract

import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Роутер для навигации экрана информации о диалоге
 *
 * @author da.zhukov
 */
interface CommunicatorDialogInformationRouter: CommunicatorHostRouter,
    DeeplinkActionNode,
    CommunicatorRouterLinkInWebView {

    /**
     * Показать профиль сотрудника
     *
     * @param uuid идентификатор сотрудника
     */
    fun showProfile(uuid: UUID)

    /**
     * Показать экран переписки
     * @param personUuid идентификатор персоны для открытия переписки (диалога/чата)
     */
    fun startNewConversation(personUuid: UUID)

    /**
     * Показать задачу
     *
     * @param documentUuid строковый литерал идентификатора документа
     */
    fun showTask(documentUuid: String?)

    /**
     * Начать видеовызов
     *
     * @param profileUuid uuid идентификатор сотрудника
     */
    fun startVideoCall(profileUuid: UUID)

    /**
     * Поставщик роутера [CommunicatorDialogInformationRouter]
     */
    interface Provider : Feature {

        /** @SelfDocumented */
        fun getCommunicatorDialogInformationRouter(): CommunicatorDialogInformationRouter
    }
}
