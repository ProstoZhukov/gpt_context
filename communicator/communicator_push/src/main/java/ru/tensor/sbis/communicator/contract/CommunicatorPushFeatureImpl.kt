package ru.tensor.sbis.communicator.contract

import android.content.Context
import ru.tensor.sbis.communicator.CommunicatorPushPlugin
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.push.CommunicatorCRMPushSubscriber
import ru.tensor.sbis.communicator.push.CommunicatorPushSubscriber
import ru.tensor.sbis.communicator.push.MessagesPushManagerImpl
import ru.tensor.sbis.communicator.push.controller.CRMChatsNotificationController
import ru.tensor.sbis.communicator.push.controller.ThemeRegistryNotificationController
import ru.tensor.sbis.pushnotification.ComplexPushSubscriber
import ru.tensor.sbis.pushnotification.PushType

/**
 * Реализация фичи модуля пушей сообщений
 * @see CommunicatorPushFeature
 *
 * @author vv.chekurda
 */
class CommunicatorPushFeatureImpl : CommunicatorPushFeature {

    override val messagesPushManager: MessagesPushManager by lazy { MessagesPushManagerImpl() }

    override fun getCommunicatorPushSubscriber(
        context: Context,
        messagesPushManager: MessagesPushManager,
    ): ComplexPushSubscriber =
        CommunicatorPushSubscriber(ThemeRegistryNotificationController(context, messagesPushManager),
            mutableListOf<PushType>().apply {
                if (CommunicatorPushPlugin.customizationOptions.needToReceiveDialogMessagesPushes) {
                    add(PushType.NEW_MESSAGE)
                }
                if (CommunicatorPushPlugin.customizationOptions.needToReceiveChatMessagesPushes ||
                    CommunicatorPushPlugin.customizationOptions.needToReceiveSupportChatMessagesPushes ||
                    CommunicatorPushPlugin.customizationOptions.needToReceiveSabySupportChatMessagesPushes) {
                    add(PushType.NEW_CHAT_MESSAGE)
                }
            }
        )

    override fun getCRMChatPushSubscriber(
        context: Context,
        messagesPushManager: MessagesPushManager,
    ): ComplexPushSubscriber =
        CommunicatorCRMPushSubscriber(CRMChatsNotificationController(context, messagesPushManager))

    override fun getSabySupportSubscriber(
        context: Context,
        messagesPushManager: MessagesPushManager,
    ): ComplexPushSubscriber =
        CommunicatorPushSubscriber(ThemeRegistryNotificationController(context, messagesPushManager),
            mutableListOf<PushType>().apply {
                if (CommunicatorPushPlugin.customizationOptions.needToReceiveSupportChatMessagesPushes) {
                    add(PushType.NEW_CHAT_MESSAGE)
                }
                if (CommunicatorPushPlugin.customizationOptions.needToReceiveSabySupportChatMessagesPushes) {
                    add(PushType.NEW_CHAT_MESSAGE)
                }
            }
        )
}