package ru.tensor.sbis.communicator.push.controller

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.collections.Predicate
import ru.tensor.sbis.communicator.QuickReplyActivity
import ru.tensor.sbis.communicator.common.push.MessagesPushAction
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.di.CommunicatorPushComponent
import ru.tensor.sbis.communicator.push.model.MessagePushModel
import ru.tensor.sbis.communicator.receiver.DeleteMessageReceiver
import ru.tensor.sbis.communicator.receiver.DeleteMessageReceiver.Companion.DELETE_PUSH_ARTICLE_DISCUSSION
import ru.tensor.sbis.communicator.receiver.DeleteMessageReceiver.Companion.DELETE_PUSH_DIALOG_UUID_KEY
import ru.tensor.sbis.communicator.receiver.DeleteMessageReceiver.Companion.DELETE_PUSH_IS_COMMENT
import ru.tensor.sbis.communicator.receiver.DeleteMessageReceiver.Companion.DELETE_PUSH_MESSAGE_UUID_KEY
import ru.tensor.sbis.communicator.receiver.QuickReplyReceiver
import ru.tensor.sbis.communicator.receiver.ReadMessageReceiver
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.contract.PushCancelContract
import ru.tensor.sbis.pushnotification.controller.base.GroupedNotificationController
import ru.tensor.sbis.pushnotification.controller.base.strategy.PushCancelStrategy
import ru.tensor.sbis.pushnotification.controller.base.strategy.PushUpdateStrategy
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider
import ru.tensor.sbis.pushnotification.notification.PushNotification
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import timber.log.Timber
import java.util.UUID
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

internal const val MESSAGE_UPDATE_SUBTYPE_FLAG = 1536
/** Максимальное количество строк контента в мульти-пуше с несколькими сообщениями по одному диалогу/чату */
private const val MULTI_PUSH_MAX_LINES_COUNT = 3

/**
 * Базовая реализация контроллера, для управления подписками и реакцией на пуш нотификации.
 *
 * @author da.zhukov.
 */
internal abstract class BaseMessageNotificationController(
        context: Context,
        messagesPushManager: MessagesPushManager
) : GroupedNotificationController<MessagePushModel>(
    context,
    MessagePushDataFactory(context),
    CancelStrategy(),
    UpdateStrategy()
) {
    private var isShowNewMessagePushNotification = true
    protected var currentConversationUuid: UUID? = null
    private var oldMessageUUID = UUIDUtils.NIL_UUID
    protected val communicatorPushDependency by lazy {
        CommunicatorPushComponent.getInstance(context).dependency
    }

    init {
        messagesPushManager.getObservable()
            .doOnNext (::handelActions)
            .doOnError { error: Throwable? ->
                Timber.e(
                    error,
                    "Error on %s push manager subscription",
                    BaseMessageNotificationController::class.java.simpleName
                )
            }
            .subscribe()
    }

    protected abstract fun handelActions(messagesPushAction: MessagesPushAction)

    protected fun setShowNewMessagePushNotification(pushType: PushType, show: Boolean) {
        isShowNewMessagePushNotification = show
        if (!show) {
            PushNotificationComponentProvider.get(context)
                .getPushCenter()
                .cancelAll(pushType)
        }
    }

    protected fun setShowNewNoticeDialogsPush(show: Boolean) {
        communicatorPushDependency.dialogNotificationPushDelegate?.let {
            if (show) {
                it.enablePushes()
            } else {
                it.cleanAllPushTypes()
                it.disablePushes()
            }
        }
    }

    /** @SelfDocumented */
    override fun needToShow(data: MessagePushModel): Boolean {
        if (data.isAuthorBlocked) {
            return false
        }
        if (data.dialogUuid == null) {
            return false
        }
        return (isShowNewMessagePushNotification // don't show in dialog list
                && data.dialogUuid != currentConversationUuid) // don't show in current conversation
    }

    /** @SelfDocumented */
    override fun getPluralsTitleRes(): Int {
        return RCommunicatorDesign.plurals.communicator_new_message
    }

    /** @SelfDocumented */
    override fun createSingleNotification(data: MessagePushModel, requestCode: Int): PushNotification {
        // Build view
        val notification = pushBuildingHelper.createSbisNotification(data)
        notification.builder
            .setSubtitle(data)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data.message.message))

        // Set intent
        notification.builder.setContentIntent(createIntentForSingle(data, requestCode))
        oldMessageUUID = data.messageUuid
        addActionToNotification(data, requestCode, notification)

        return notification
    }

    protected open fun addActionToNotification(data: MessagePushModel, requestCode: Int, notification: PushNotification) {
        val subtypes = data.subtypes
        if (subtypes != null && (subtypes.contains(MessagePushModel.ServiceType.HAS_TEXT) ||
                    subtypes.contains(MessagePushModel.ServiceType.HAS_ONE_ATTACHMENT) ||
                    subtypes.contains(MessagePushModel.ServiceType.HAS_SEVERAL_ATTACHMENTS) ||
                    data.messageUuid == oldMessageUUID)) {
            val replyAction = buildQuickReplyAction(data, requestCode)
            notification.builder.addAction(replyAction)
        }
        if (data.isNeedShowRemoveDialogAction) {
            val deleteAction = buildDeleteMessageAction(data, requestCode)
            notification.builder.addAction(deleteAction)
        }
        val readAction = buildReadMessageAction(data, requestCode)
        notification.builder.addAction(readAction)
    }

    /** @SelfDocumented */
    override fun createGroupedNotification(dataList: List<MessagePushModel>, requestCode: Int): PushNotification? {
        if (dataList.isNotEmpty()) {
            val notification = pushBuildingHelper.createSbisNotification(dataList.last())

            if (isTheSameDialog(dataList)) {
                customizeAsMultiMessageNotification(notification, dataList)
                notification.builder.addAction(buildQuickReplyAction(dataList.last(), requestCode))
                if (dataList.last().isNeedShowRemoveDialogAction) notification.builder.addAction(buildDeleteMessageAction(dataList.last(), requestCode))
                notification.builder.addAction(buildReadMessageAction(dataList.last(), requestCode))
            } else {
                customizeAsGroupedNotification(notification, dataList)
            }

            notification.builder.setContentIntent(
                when {
                    isTheSameDialog(dataList) -> {
                        if (isConsultation(dataList)) {
                            createIntentForSingle(dataList.last(), requestCode)
                        } else {
                            createIntentForSingle(dataList.first(), requestCode)
                        }
                    }
                    else                      -> createIntentForGrouped(dataList, requestCode)
                }
            )
            return notification
        }
        return null
    }

    private fun NotificationCompat.Builder.setSubtitle(model: MessagePushModel): NotificationCompat.Builder = apply {
        if (model.isThemeIsChat || model.isMessageFromTheTask) {
            setSubText(model.conversationSubtitle)
        }
    }

    private fun isTheSameDialog(items: List<MessagePushModel>): Boolean =
        items.isNotEmpty() && items.mapTo(HashSet()) { it.dialogUuid }.size == 1

    private fun isConsultation(items: List<MessagePushModel>): Boolean =
        items.first().isSupport

    private fun customizeAsMultiMessageNotification(
        notification: PushNotification,
        messageModels: List<MessagePushModel>
    ) {
        val inboxStyle = NotificationCompat.InboxStyle()
        val messagesCount = messageModels.size
        var hiddenMessagesCount = messagesCount
        val lastModel = messageModels.last()
        getNotificationContentWithSenders(messageModels)
            .take(MULTI_PUSH_MAX_LINES_COUNT)
            .forEach {
                inboxStyle.addLine(it)
                hiddenMessagesCount--
            }

        inboxStyle.setBigContentTitle(
            if (lastModel.isThemeIsChat && hiddenMessagesCount > 0) {
                lastModel.conversationTitle
            } else {
                context.getString(RCommunicatorDesign.string.communicator_push_dialog_grup_title)
            }
        )

        val summaryText = when {
            hiddenMessagesCount == 1 -> context.getString(RCommunicatorDesign.string.communicator_push_dialog_unread_summary_one_message)
            hiddenMessagesCount > 1  -> context.getString(RCommunicatorDesign.string.communicator_push_dialog_unread_summary_messages, hiddenMessagesCount)
            lastModel.isThemeIsChat  -> lastModel.conversationTitle
            else                     -> StringUtils.EMPTY
        }
        inboxStyle.setSummaryText(summaryText)

        notification.builder
            .setStyle(inboxStyle)
            .setNumber(messagesCount)
    }

    private fun customizeAsGroupedNotification(notification: PushNotification, items: List<MessagePushModel>) {
        val content = getNotificationContentWithSenders(items).asReversed()
        val inboxStyle = NotificationCompat.InboxStyle()
        val counter = items.size

        content.forEach {
            inboxStyle.addLine(it)
        }
        val title = "$counter ${context.resources.getQuantityString(getPluralsTitleRes(), counter)}"
        inboxStyle.setBigContentTitle(title)

        notification.builder
            .setStyle(inboxStyle)
            .setNumber(items.size)
            .setContentTitle(title)
            .setContentText(content.first())
    }

    private fun getNotificationContentWithSenders(items: List<MessagePushModel>): List<String> {
        val relevantMessagesOfSenders = LinkedHashMap<UUID, MessagePushModel>()
        val messagesCount = HashMap<UUID, Int>()

        items.forEach { messageModel ->
            messageModel.sender?.let {
                val senderUuid = it.uuid
                if (relevantMessagesOfSenders.containsKey(senderUuid)) {
                    val count: Int = messagesCount[senderUuid] ?: 1
                    messagesCount[senderUuid] = count.inc()
                }
                relevantMessagesOfSenders[senderUuid] = messageModel
            }
        }

        return relevantMessagesOfSenders.mapTo(ArrayList()) {
            it.value.getMessageWithSender(messagesCount[it.key])
        }
    }

    /** @SelfDocumented */
    private fun buildQuickReplyAction(model: MessagePushModel, requestCode: Int): NotificationCompat.Action {
        val pendingIntent = buildQuickReplyIntent(model, requestCode)
        val actionLabel = context.getString(RCommunicatorDesign.string.communicator_push_notification_action_reply)
        val builder = NotificationCompat.Action.Builder(
            RCommunicatorDesign.drawable.communicator_icon_send_disabled,
            actionLabel,
            pendingIntent
        )
        if (hasNativeQuickReply()) {
            val hint = if (model.isComment) context.getString(RCommunicatorDesign.string.communicator_quick_reply_comment_hint) else context.getString(
                RCommon.string.common_enter_message
            )
            val remoteInput = RemoteInput.Builder(QUICK_REPLY_RECEIVER_REPLY_MESSAGE_KEY).setLabel(hint).build()
            builder.addRemoteInput(remoteInput)
        }
        return builder.build()
    }

    private fun buildQuickReplyIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val commonBundle = Bundle()
        commonBundle.putSerializable(QUICK_REPLY_DIALOG_UUID_KEY, model.dialogUuid)
        commonBundle.putSerializable(QUICK_REPLY_MESSAGE_UUID_KEY, model.messageUuid)
        commonBundle.putBoolean(QUICK_REPLY_IS_COMMENT_KEY, model.isComment)
        commonBundle.putSerializable(QUICK_REPLY_RECIPIENT_PERSON_MODEL_KEY, model.sender)
        return if (hasNativeQuickReply()) { // Build action for quick reply from PushNotification
            val intent = Intent(context, QuickReplyReceiver::class.java).apply {
                putExtras(commonBundle)
            }
            pushIntentHelper.getUpdateCurrentBroadcastMutable(requestCode, intent)
        } else { // Build action for quick reply from QuickReplyActivity
            val intent = Intent(context, QuickReplyActivity::class.java).apply {
                setPackage(BuildConfig.MAIN_APP_ID)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtras(commonBundle)
                putExtra(QUICK_REPLY_ACTIVITY_TARGET_MESSAGE_KEY, model.message.message)
            }
            pushIntentHelper.getUpdateCurrentActivityImmutable(requestCode, intent)
        }
    }

    /** @SelfDocumented */
    private fun buildDeleteMessageAction(model: MessagePushModel, requestCode: Int): NotificationCompat.Action {
        val pendingIntent = buildDeleteMessageIntent(model, requestCode)
        val actionLabel = context.getString(RCommunicatorDesign.string.communicator_push_notification_action_delete)
        val builder = NotificationCompat.Action.Builder(
            RCommunicatorDesign.drawable.communicator_ic_remove,
            actionLabel,
            pendingIntent
        )
        return builder.build()
    }

    private fun buildDeleteMessageIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val commonBundle = Bundle().apply {
            putSerializable(DELETE_PUSH_DIALOG_UUID_KEY, model.dialogUuid)
            putSerializable(DELETE_PUSH_MESSAGE_UUID_KEY, model.messageUuid)
            putBoolean(DELETE_PUSH_IS_COMMENT, model.isComment)
            putBoolean(DELETE_PUSH_ARTICLE_DISCUSSION, model.isArticleDiscussionMessage)
        }
        val intent = Intent(context, DeleteMessageReceiver::class.java)
        intent.putExtras(commonBundle)
        return pushIntentHelper.getUpdateCurrentBroadcastMutable(requestCode, intent)
    }

    private fun buildReadMessageAction(model: MessagePushModel, requestCode: Int): NotificationCompat.Action {
        val pendingIntent = buildReadMessageIntent(model, requestCode)
        val actionLabel = context.getString(RCommunicatorDesign.string.communicator_push_notification_action_read)
        val builder = NotificationCompat.Action.Builder(
            RCommunicatorDesign.drawable.communicator_icon_mark_read,
            actionLabel,
            pendingIntent
        )
        return builder.build()
    }

    private fun buildReadMessageIntent(model: MessagePushModel, requestCode: Int): PendingIntent {
        val commonBundle = Bundle()
        commonBundle.putSerializable(ReadMessageReceiver.PUSH_DIALOG_UUID_KEY, model.dialogUuid)
        commonBundle.putSerializable(ReadMessageReceiver.READ_PUSH_MESSAGE_UUID_KEY, model.messageUuid)
        val intent = Intent(context, ReadMessageReceiver::class.java)
        intent.putExtras(commonBundle)
        return pushIntentHelper.getUpdateCurrentBroadcastMutable(requestCode, intent)
    }

    private class CancelStrategy : PushCancelStrategy<MessagePushModel> {
        companion object {
            /**
             * Outer cancel logic
             */
            private const val KEY_IS_BATCH = "isBatch"
            private const val KEY_IS_ALL = "isAll"
            private const val KEY_DIALOG_IDS = "dlgIds"
            private const val KEY_DIALOG_ID = "dlgId"
        }

        override fun getOuterCancelMatcher(cancelMessage: PushNotificationMessage): Predicate<MessagePushModel>? {
            val options = MessagePushModel.ServiceType.from(cancelMessage.subType)
            val read = options.contains(MessagePushModel.ServiceType.MESSAGE_WAS_READ)
            val delete = options.contains(MessagePushModel.ServiceType.MESSAGE_WAS_DELETED)
            if (read || delete) {
                val removeTimestamp = cancelMessage.sendTime
                val isAll = cancelMessage.data.optBoolean(KEY_IS_ALL, false)
                if (isAll) {
                    // Remove all
                    return removeAllMatcher(removeTimestamp)
                }
                if (cancelMessage.data.has(KEY_IS_BATCH)) {
                    val isBatch = cancelMessage.data.optBoolean(KEY_IS_BATCH)
                    return if (isBatch) {
                        // Remove batch of notifications
                        removeBatchMatcher(removeTimestamp, cancelMessage.data)
                    } else {
                        // Remove single notification
                        removeSingleMatcher(removeTimestamp, cancelMessage.data)
                    }
                }
            }
            return null
        }

        /**
         * Удаляем все сообщения, которые были опубликованы раньше
         * указанной даты удаления.
         *
         * @param removeTimestamp - дата удаления
         * @return условие удаления
         */
        private fun removeAllMatcher(removeTimestamp: Long): Predicate<MessagePushModel> {
            return predicate { model: MessagePushModel -> model.message.sendTime <= removeTimestamp }
        }

        /**
         * Удаляем сообщения в указанных диалогах, которые были
         * опубликованы раньше указанной даты удаления.
         *
         * @param removeTimestamp - дата удаления
         * @param data            - данные об удалении
         * @return условие удаления
         */
        private fun removeBatchMatcher(removeTimestamp: Long, data: JSONObject): Predicate<MessagePushModel>? {
            val dialogArray = data.optJSONArray(KEY_DIALOG_IDS)
            return if (dialogArray != null) {
                predicate { model: MessagePushModel ->
                    var i = 0
                    while (i < dialogArray.length()) {
                        if (UUIDUtils.equals(dialogArray.optString(i), model.dialogUuid) && model.message.sendTime <= removeTimestamp)
                            return@predicate true
                        i++
                    }
                    false
                }
            } else null
        }

        /**
         * Удаляем все сообщения по указанному диалогу, которые были
         * опубликованы раньше указанной даты удаления.
         *
         * @param removeTimestamp - дата удаления
         * @param data            - данные об удалении
         * @return условие удаления
         */
        private fun removeSingleMatcher(removeTimestamp: Long, data: JSONObject): Predicate<MessagePushModel> {
            val dialogUuid = data.optString(KEY_DIALOG_ID)
            return predicate { model: MessagePushModel -> UUIDUtils.equals(model.dialogUuid, dialogUuid) && model.message.sendTime <= removeTimestamp }
        }

        /**
         * Inner cancel logic
         */
        override fun getInnerCancelMatcher(cancelParams: Bundle): Predicate<MessagePushModel> {
            val dialogUuid = PushCancelContract.getDialogUuid(cancelParams)
            return predicate { model: MessagePushModel -> UUIDUtils.equals(model.dialogUuid, dialogUuid) }
        }

    }

    private class UpdateStrategy : PushUpdateStrategy<MessagePushModel> {
        override fun getUpdateMatcher(updateData: MessagePushModel): Predicate<MessagePushModel> {
            return object : Predicate<MessagePushModel> {
                override fun apply(t: MessagePushModel): Boolean {
                    val notificationUuid = t.messageUuid
                    val updateUuid = updateData.messageUuid
                    return UUIDUtils.equals(notificationUuid, updateUuid)
                }
            }
        }
    }

    companion object {
        internal const val QUICK_REPLY_ACTIVITY_TARGET_MESSAGE_KEY = "TargetMessage"
        internal const val QUICK_REPLY_DIALOG_UUID_KEY = "DialogUuid"
        internal const val QUICK_REPLY_MESSAGE_UUID_KEY = "MessageUuid"
        internal const val QUICK_REPLY_IS_COMMENT_KEY = "IsComment"
        internal const val QUICK_REPLY_RECIPIENT_PERSON_MODEL_KEY = "PersonModel"
        internal const val QUICK_REPLY_RECEIVER_REPLY_MESSAGE_KEY = "ReplyMessage"

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
        private fun hasNativeQuickReply(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        }
    }
}

private inline fun <T> predicate(crossinline function: (T) -> Boolean): Predicate<T> = object : Predicate<T> {
    override fun apply(t: T): Boolean = function(t)
}