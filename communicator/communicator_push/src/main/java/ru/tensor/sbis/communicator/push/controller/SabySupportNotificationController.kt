package ru.tensor.sbis.communicator.push.controller

import android.app.PendingIntent
import android.content.Context
import ru.tensor.sbis.communicator.common.push.MessagesPushAction
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.push.SubscribeOnNotification
import ru.tensor.sbis.communicator.common.push.UnsubscribeFromNotification
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SupportChannelListFragmentFactory
import ru.tensor.sbis.communicator.push.SupportClientConversationCategory
import ru.tensor.sbis.communicator.push.model.MessagePushModel
import ru.tensor.sbis.pushnotification.PushContentCategory
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.SettingsPushContentCategory
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider
import ru.tensor.sbis.pushnotification.repository.model.PushCloudAction
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Реализация контроллера, для управления подписками и реакцией на пуш нотификации чатов техподдержки.
 *
 * @author da.zhukov.
 */
internal class SabySupportNotificationController(
    context: Context, messagesPushManager: MessagesPushManager
) : BaseMessageNotificationController(context, messagesPushManager) {

    override fun handelActions(messagesPushAction: MessagesPushAction) {
        val conversationUuidIsEmpty = messagesPushAction.conversationUuid == null

        fun needUnsubscribeFromOneChat() =
            messagesPushAction is UnsubscribeFromNotification && !conversationUuidIsEmpty

        fun needRestoreSubscriptionToChat() =
            messagesPushAction is SubscribeOnNotification && !conversationUuidIsEmpty

        when {
            needUnsubscribeFromOneChat() -> {
                currentConversationUuid = messagesPushAction.conversationUuid
                PushNotificationComponentProvider.get(context)
                    .getPushCenter()
                    .cancel(
                        PushType.NEW_CHAT_MESSAGE,
                        PushCancelContract.createDialogParams(currentConversationUuid)
                    )
            }

            needRestoreSubscriptionToChat() -> {
                if (messagesPushAction.conversationUuid == currentConversationUuid) {
                    currentConversationUuid = null
                } else {
                    Unit
                }
            }
        }
    }

    override fun needToShow(data: MessagePushModel): Boolean {
        return super.needToShow(data)
                && (data.isSupport || data.isSabySupport)
    }

    /** @SelfDocumented */
    override fun determineCloudAction(message: PushNotificationMessage): PushCloudAction {
        if (message.subType == MESSAGE_UPDATE_SUBTYPE_FLAG
            && (message.type == PushType.NEW_MESSAGE || message.type == PushType.NEW_CHAT_MESSAGE)) {
            return PushCloudAction.UPDATE
        }
        return super.determineCloudAction(message)
    }

    /** @SelfDocumented */
    override fun createIntentForSingle(data: MessagePushModel, requestCode: Int): PendingIntent? {
        return with(data) {
            when {
                isSupport && communicatorPushDependency.supportChannelListFragmentFactory != null -> createSupportConversation(
                    this,
                    requestCode
                )
                isSabySupport && communicatorPushDependency.supportChannelListFragmentFactory != null -> createSabySupportConversation(
                    communicatorPushDependency.supportChannelListFragmentFactory!!,
                    this,
                    requestCode
                )
                else -> null
            }
        }
    }

    override fun getContentCategory(): PushContentCategory {
        return SupportClientConversationCategory()
    }

    /**
     * Создать PendingIntent для открытия раздела "Служба поддержки" и навигации в нем
     */
    private fun createSupportConversation(model: MessagePushModel, requestCode: Int): PendingIntent {
        val intent = with(model) {
            communicatorPushDependency.supportChannelListFragmentFactory?.getOpenSupportConversationIntent(
                dialogUuid,
                conversationTitle
            )
        }
        return pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, intent)
    }

    /**
     * Создать PendingIntent для открытия переписки в карточке (например, из пуша)
     */
    private fun createSabySupportConversation(supportChannelListFragmentFactory: SupportChannelListFragmentFactory, model: MessagePushModel, requestCode: Int): PendingIntent {
        val intent = with(model) {
            supportChannelListFragmentFactory.getOpenSabySupportConversationIntent(
                dialogUuid,
                conversationTitle
            )
        }
        val contentCategory = if (model.isSabySupport) {
            SettingsPushContentCategory()
        } else {
            getContentCategory()
        }
        return pushIntentHelper.createIntentWithBackStack(intent, contentCategory, requestCode)
    }
}