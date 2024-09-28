package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.Observable
import org.json.JSONObject
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.message_panel.view.mentions.MentionData
import java.util.*

/**
 * Модель данных сообщения
 *
 * @author vv.chekurda
 * @since 7/16/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelData {
    val conversationUuid: Observable<RxContainer<UUID?>>
    val document: Observable<RxContainer<UUID?>>
    val quotedMessageUuid: Observable<RxContainer<UUID>>
    val answeredMessageUuid: Observable<RxContainer<UUID>>
    val folderUuid: Observable<RxContainer<UUID>>
    val messageUuid: Observable<RxContainer<UUID>>
    val messageMetaData: Observable<RxContainer<String?>>
    val showQuickReplyButton: Observable<Boolean>
    val mentionsData: Observable<List<MentionData>>
    val draftMentions: Observable<MessageTextWithMentions>

    val messageText: Observable<RxContainer<String>>

    fun setConversationUuid(conversationUuid: UUID?)
    fun setDocumentUuid(documentUuid: UUID?)
    fun setQuotedMessageUuid(quotedMessageUuid: UUID?)
    fun setAnsweredMessageUuid(answeredMessageUuid: UUID?)
    fun setFolderUuid(folderUuid: UUID?)
    fun setMessageUuid(messageUuid: UUID?)
    fun setRecipientsHintEnabled(isEnabled: Boolean)
    fun requireCheckAllMembers(isRequired: Boolean)

    fun setMessageText(text: String?)
    fun setMessageText(text: RxContainer<String>)
    fun concatMessageText(text: String)
    fun setMessageMetaData(metaData: String?)
    fun setQuickReplyButtonVisible(isVisible: Boolean)
    fun setMentionsData(mentions: List<MentionData>)
    fun getModifiedMessageMetaData(data: JSONObject?): String?
    fun getMentionsObject(): JSONObject?
    fun setDraftMentions(messageTextWithMentions: MessageTextWithMentions)
}