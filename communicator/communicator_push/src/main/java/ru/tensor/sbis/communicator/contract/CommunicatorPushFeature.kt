package ru.tensor.sbis.communicator.contract

import ru.tensor.sbis.communicator.common.push.CRMChatPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communicator.common.push.CommunicatorPushSubscriberProvider

/**
 * Интерфейс, описывающий API модуля communicator_push
 * @see CRMChatPushSubscriberProvider
 * @see MessagesPushManagerProvider
 *
 * @author vv.chekurda
 */
interface CommunicatorPushFeature :
    CommunicatorPushSubscriberProvider,
    CRMChatPushSubscriberProvider,
    MessagesPushManagerProvider