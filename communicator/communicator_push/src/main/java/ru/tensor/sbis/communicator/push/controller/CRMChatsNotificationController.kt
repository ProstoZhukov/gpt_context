package ru.tensor.sbis.communicator.push.controller

import android.app.PendingIntent
import android.content.Context
import ru.tensor.sbis.communicator.common.push.MessagesPushAction
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.push.SubscribeOnNotification
import ru.tensor.sbis.communicator.common.push.UnsubscribeFromNotification
import ru.tensor.sbis.communicator.push.CRMContentCategory
import ru.tensor.sbis.communicator.push.model.MessagePushModel
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenCRMConversationDeepLinkAction
import ru.tensor.sbis.pushnotification.PushContentCategory
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider
import ru.tensor.sbis.pushnotification.notification.PushNotification
import ru.tensor.sbis.pushnotification.repository.model.PushCloudAction
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Реализация контроллера, для управления подписками и реакцией на пуш нотификации чатов оператора.
 *
 * @author da.zhukov.
 */
internal class CRMChatsNotificationController(
    context: Context,
    messagesPushManager: MessagesPushManager
) : BaseMessageNotificationController(context, messagesPushManager) {

    override fun handelActions(messagesPushAction: MessagesPushAction) {
        val conversationUuidIsEmpty = messagesPushAction.conversationUuid == null

        fun needUnsubscribeFromOneChat() =
            messagesPushAction is UnsubscribeFromNotification && !conversationUuidIsEmpty

        fun needRestoreSubscriptionToChat() =
            messagesPushAction is SubscribeOnNotification && !conversationUuidIsEmpty

        fun needUnsubscribeFromChatsNotifications() =
            messagesPushAction is UnsubscribeFromNotification && conversationUuidIsEmpty

        fun needSubscribeToChatsNotifications() =
            messagesPushAction is SubscribeOnNotification && conversationUuidIsEmpty

        when {
            needUnsubscribeFromOneChat() -> {
                currentConversationUuid = messagesPushAction.conversationUuid
                PushNotificationComponentProvider.get(context)
                    .getPushCenter()
                    .cancel(PushType.OPERATORS_CONSULTATION_MESSAGE, PushCancelContract.createDialogParams(currentConversationUuid))
            }

            needRestoreSubscriptionToChat() -> {
                if (messagesPushAction.conversationUuid == currentConversationUuid) {
                    currentConversationUuid = null
                } else { Unit }
            }

            needUnsubscribeFromChatsNotifications() -> {
                setShowNewMessagePushNotification(PushType.OPERATORS_CONSULTATION_MESSAGE, false)
            }

            needSubscribeToChatsNotifications() -> {
                setShowNewMessagePushNotification(PushType.OPERATORS_CONSULTATION_MESSAGE, true)
            }

            else -> Unit
        }
    }

    override fun needToShow(data: MessagePushModel): Boolean {
        return super.needToShow(data)
                && (data.isSabygetOperatorsMessage || data.isOperatorsConsultationMessage || data.isCrmRateMessage)
    }

    /** @SelfDocumented */
    override fun determineCloudAction(message: PushNotificationMessage): PushCloudAction {
        if (message.subType == MESSAGE_UPDATE_SUBTYPE_FLAG && message.type == PushType.OPERATORS_CONSULTATION_MESSAGE) {
            return PushCloudAction.UPDATE
        }
        return super.determineCloudAction(message)
    }

    override fun getContentCategory(): PushContentCategory {
        return CRMContentCategory()
    }

    /** @SelfDocumented */
    override fun createIntentForSingle(data: MessagePushModel, requestCode: Int): PendingIntent? {
        return with(data) {
            when {
                isOperatorsConsultationMessage
                        || isSabygetOperatorsMessage
                        || isCrmRateMessage -> {
                            createCRMChatIntent(this, requestCode)
                        }
                else -> null
            }
        }
    }

    override fun addActionToNotification(data: MessagePushModel, requestCode: Int, notification: PushNotification) = Unit

    private fun createCRMChatIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val isCRMContentCategory = model.isThemeIsChat &&
                (model.isOperatorsConsultationMessage || model.isSabygetOperatorsMessage || model.isCrmRateMessage)

        val contentCategory = if (isCRMContentCategory) CRMContentCategory() else getContentCategory()
        val mainIntent = pushIntentHelper.createMainActivityIntent(contentCategory).apply {
            putExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION, with(model) {
                OpenCRMConversationDeepLinkAction(dialogUuid)
            })
        }
        return pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, mainIntent)
    }
}