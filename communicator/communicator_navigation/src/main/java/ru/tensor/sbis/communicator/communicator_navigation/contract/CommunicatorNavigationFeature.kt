package ru.tensor.sbis.communicator.communicator_navigation.contract

import ru.tensor.sbis.communicator.common.navigation.contract.*

/**
 * API модуля навигации коммуникатора, описывающий предоставляемый модулем функционал
 * @see [CommunicatorRouter.Provider]
 * @see [CommunicatorHostRouter.Provider]
 * @see [CommunicatorConversationRouter.Provider]
 * @see [CommunicatorThemesRouter.Provider]
 * @see [CommunicatorDialogInformationRouter.Provider]
 *
 * @author da.zhukov
 */
interface CommunicatorNavigationFeature :
    CommunicatorRouter.Provider,
    CommunicatorHostRouter.Provider,
    CommunicatorConversationRouter.Provider,
    CommunicatorThemesRouter.Provider,
    CommunicatorDialogInformationRouter.Provider