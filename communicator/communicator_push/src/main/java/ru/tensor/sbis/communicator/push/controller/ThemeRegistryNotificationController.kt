package ru.tensor.sbis.communicator.push.controller

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.CommunicatorPushPlugin.customizationOptions
import ru.tensor.sbis.communicator.common.push.MessagesPushAction
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.push.SubscribeOnNotification
import ru.tensor.sbis.communicator.common.push.ThemeRegistryPushAction
import ru.tensor.sbis.communicator.common.push.ThemeSubscribeFromNotification
import ru.tensor.sbis.communicator.common.push.ThemeUnsubscribeFromNotification
import ru.tensor.sbis.communicator.common.push.UnsubscribeFromNotification
import ru.tensor.sbis.communicator.declaration.communicator_support_channel_list.SupportChannelListFragmentFactory
import ru.tensor.sbis.communicator.push.ChannelContentCategory
import ru.tensor.sbis.communicator.push.MessageContentCategory
import ru.tensor.sbis.communicator.push.model.MessagePushModel
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenArticleDiscussionDeeplinkAction
import ru.tensor.sbis.deeplink.OpenConversationDeeplinkAction
import ru.tensor.sbis.deeplink.OpenNewsDeepLinkAction
import ru.tensor.sbis.deeplink.OpenProfileDeeplinkAction
import ru.tensor.sbis.deeplink.OpenWebViewDeeplinkAction
import ru.tensor.sbis.deeplink.SerializableDeeplinkAction
import ru.tensor.sbis.pushnotification.PushContentCategory
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.SettingsPushContentCategory
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider
import ru.tensor.sbis.pushnotification.repository.model.PushCloudAction
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import java.util.UUID

/**
 * Реализация контроллера, для управления подписками и реакцией на пуш нотификации диалогов и чатов.
 *
 * @author da.zhukov.
 */
internal class ThemeRegistryNotificationController(
    context: Context,
    messagesPushManager: MessagesPushManager
) : BaseMessageNotificationController(context, messagesPushManager) {

    private var isChatListShown = false

    override fun handelActions(messagesPushAction: MessagesPushAction) {
        val conversationUuidIsEmpty = messagesPushAction.conversationUuid == null
        val isChannelAction = (messagesPushAction as? ThemeRegistryPushAction)?.isChannel == true

        fun needUnsubscribeFromOneConversation() =
            messagesPushAction is UnsubscribeFromNotification && !conversationUuidIsEmpty

        fun needRestoreSubscriptionToConversation() =
            messagesPushAction is SubscribeOnNotification && !conversationUuidIsEmpty

        fun needUnsubscribeFromDialogsNotifications() =
            messagesPushAction is ThemeUnsubscribeFromNotification && conversationUuidIsEmpty && !isChannelAction

        fun needSubscribeToDialogsNotifications() =
            messagesPushAction is ThemeSubscribeFromNotification && conversationUuidIsEmpty && !isChannelAction

        fun needUnsubscribeFromChannelsNotifications() =
            messagesPushAction is ThemeUnsubscribeFromNotification && conversationUuidIsEmpty && isChannelAction

        fun needSubscribeToChannelsNotifications() =
            messagesPushAction is ThemeSubscribeFromNotification && conversationUuidIsEmpty && isChannelAction

        when {
            needUnsubscribeFromOneConversation() -> {
                currentConversationUuid = messagesPushAction.conversationUuid
                val params = PushCancelContract.createDialogParams(currentConversationUuid)
                PushNotificationComponentProvider.get(context)
                    .getPushCenter().apply {
                        cancel(PushType.NEW_MESSAGE, params)
                        cancel(PushType.NEW_CHAT_MESSAGE, params)
                    }
            }

            needRestoreSubscriptionToConversation() -> {
                if (messagesPushAction.conversationUuid == currentConversationUuid) {
                    currentConversationUuid = null
                } else {
                    Unit
                }
            }

            needUnsubscribeFromDialogsNotifications() -> {
                setShowNewMessagePushNotification(PushType.NEW_MESSAGE, false)
                setShowNewNoticeDialogsPush(false)
            }

            needSubscribeToDialogsNotifications() -> {
                setShowNewMessagePushNotification(PushType.NEW_MESSAGE, true)
                setShowNewNoticeDialogsPush(true)
            }

            needUnsubscribeFromChannelsNotifications() -> {
                isChatListShown = true
                setShowNewMessagePushNotification(PushType.NEW_CHAT_MESSAGE, false)
            }

            needSubscribeToChannelsNotifications() -> {
                isChatListShown = false
                setShowNewMessagePushNotification(PushType.NEW_CHAT_MESSAGE, true)
            }

            else -> Unit
        }
    }

    override fun needToShow(data: MessagePushModel): Boolean {
        return (super.needToShow(data)
                && isHaveToShowPushFromChatList(data)
                && data.messageUuid != null
                && !data.isSabygetOperatorsMessage
                && !data.isSupport
                && !data.isSabySupport
                && !data.isOperatorsConsultationMessage)
                && ((customizationOptions.needToReceiveChatMessagesPushes && data.isThemeIsChat) || (customizationOptions.needToReceiveDialogMessagesPushes && !data.isThemeIsChat))
                || (super.needToShow(data) && customizationOptions.needToReceiveSupportChatMessagesPushes)
                || (super.needToShow(data) && customizationOptions.needToReceiveSabySupportChatMessagesPushes)
    }

    /** @SelfDocumented */
    override fun determineCloudAction(message: PushNotificationMessage): PushCloudAction {
        if (message.subType == MESSAGE_UPDATE_SUBTYPE_FLAG
            && (message.type == PushType.NEW_MESSAGE || message.type == PushType.NEW_CHAT_MESSAGE)) {
            return PushCloudAction.UPDATE
        }
        return super.determineCloudAction(message)
    }

    override fun getContentCategory(): PushContentCategory {
        return MessageContentCategory()
    }

    /** @SelfDocumented */
    override fun createIntentForSingle(data: MessagePushModel, requestCode: Int): PendingIntent? {
        return with(data) {
            when {
                isArticleDiscussionMessage -> createArticleDiscussionIntent(this, requestCode)
                isViolation -> createViolationIntent(this, requestCode)
                isComment -> createNewsIntent(this, requestCode)
                isSubscription -> createProfileIntent(this, requestCode)
                isAcceptedApplicationGroup -> createWebViewIntent(this, requestCode)
                isDiscussionMentioning -> createWebViewIntent(this, requestCode)
                isSupport && communicatorPushDependency.supportChannelListFragmentFactory != null -> createSupportConversation(
                    this,
                    requestCode
                )
                isSabySupport && communicatorPushDependency.supportChannelListFragmentFactory != null -> createSabySupportConversation(
                    communicatorPushDependency.supportChannelListFragmentFactory!!,
                    this,
                    requestCode
                )
                else -> createChatIntent(data, requestCode)
            }
        }
    }

    /** Не показывать адресное сообщение из канала находясь в реестре каналов  */
    private fun isHaveToShowPushFromChatList(model: MessagePushModel): Boolean = !isChatListShown || !model.isThemeIsChat

    private fun createWebViewIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val action = with(model) { OpenWebViewDeeplinkAction(dialogUuid, messageUuid, documentName, documentUrl) }
        val intent = createMessagesDeeplinkActionIntent(action)
        return pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, intent)
    }

    private fun createMessagesDeeplinkActionIntent(deeplinkAction: SerializableDeeplinkAction): Intent {
        return pushIntentHelper.createMainActivityIntent(getContentCategory()).apply {
            putExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION, deeplinkAction)
        }
    }

    private fun createArticleDiscussionIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val action = with(model) {
            OpenArticleDiscussionDeeplinkAction(
                UUID.fromString(documentUuid),
                dialogUuid,
                messageUuid,
                documentUrl,
                documentName,
                isSocnetEvent
            )
        }
        val intent = createMessagesDeeplinkActionIntent(action)
        return pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, intent)
    }

    private fun createViolationIntent(model: MessagePushModel, requestCode: Int): PendingIntent? =
        communicatorPushDependency.violationActivityProvider?.let {
            val intent = it.getViolationDetailsIntent(UUIDUtils.fromString(model.documentUuid), null)
            pushIntentHelper.createIntentWithBackStack(intent, getContentCategory(), requestCode)
        }

    private fun createProfileIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val action = with(model) { OpenProfileDeeplinkAction(dialogUuid, messageUuid, sender!!.uuid) }
        val intent = createMessagesDeeplinkActionIntent(action)
        return pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, intent)
    }

    private fun createNewsIntent(model: MessagePushModel, requestCode: Int): PendingIntent? {
        return if (model.isSocnetEvent) {
            val mainIntent = pushIntentHelper.createMainActivityIntent(getContentCategory()).apply {
                putExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION, with(model) {
                    OpenNewsDeepLinkAction(documentUuid, messageUuid)
                })
            }
            pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, mainIntent)
        } else {
            val intent = communicatorPushDependency.newsActivityProvider?.getNewsReplyCommentIntent(
                model.documentUuid,
                model.messageUuid,
                model.dialogUuid
            ) ?: pushIntentHelper.createMainActivityIntent(getContentCategory()).apply {
                putExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION, with(model) {
                    OpenWebViewDeeplinkAction(
                        dialogUuid,
                        messageUuid,
                        documentName,
                        documentUrl
                    )
                })
            }
            pushIntentHelper.createIntentWithBackStack(intent, getContentCategory(), requestCode)
        }
    }

    /** @SelfDocumented */
    private fun createChatIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val contentCategory = if (model.isThemeIsChat && customizationOptions.appHasChatNavigationMenuItem) ChannelContentCategory() else getContentCategory()
        val mainIntent = pushIntentHelper.createMainActivityIntent(contentCategory).apply {
            putExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION, with(model) {
                val recipients = sender?.let { arrayListOf(it.uuid) } ?: arrayListOf()
                OpenConversationDeeplinkAction(
                    dialogUuid,
                    messageUuid,
                    recipients,
                    isThemeIsChat,
                    conversationTitle,
                    sender?.photoId,
                    membersCount > 2
                )
            } )
        }
        return pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, mainIntent)
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