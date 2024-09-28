package ru.tensor.sbis.communicator.common.push

import android.content.Context
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.pushnotification.ComplexPushSubscriber
import ru.tensor.sbis.pushnotification.controller.notification.base.PushIntentHelper

/** @SelfDocumented */
interface CommunicatorPushSubscriberProvider : Feature {
    /** @SelfDocumented */
    fun getCommunicatorPushSubscriber(
        context: Context,
        messagesPushManager: MessagesPushManager,
    ) : ComplexPushSubscriber

    /** @SelfDocumented */
    fun getSabySupportSubscriber(
        context: Context,
        messagesPushManager: MessagesPushManager,
    ) : ComplexPushSubscriber
}

interface CRMChatPushSubscriberProvider : Feature {
    /** @SelfDocumented */
    fun getCRMChatPushSubscriber(
        context: Context,
        messagesPushManager: MessagesPushManager,
    ) : ComplexPushSubscriber
}