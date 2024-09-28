package ru.tensor.sbis.communicator.common.navigation.contract

import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Делегат общей навигации модуля коммуникатор.
 *
 * @author vv.chekurda
 */
interface CommunicatorRouter :
    CommunicatorConversationRouter,
    CommunicatorThemesRouter,
    CommunicatorHostRouter,
    DeeplinkActionNode,
    CommunicatorRouterLinkInWebView,
    CommunicatorDialogInformationRouter {

    /**
     * Поставщик навигационного делегата [CommunicatorRouter].
     */
    interface Provider : Feature {

        /** @SelfDocumented */
        fun getCommunicatorRouter(): CommunicatorRouter
    }
}
