package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain

import io.reactivex.Completable
import io.reactivex.Single
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.message_panel.contract.MessageServiceDependency
import ru.tensor.sbis.message_panel.decl.DraftArguments
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.EditArguments
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.decl.MessageServiceWrapper
import ru.tensor.sbis.message_panel.decl.SendArguments
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.persons.IPersonModel
import ru.tensor.sbis.persons.PersonModel
import java.util.UUID

/**
 * Заглушка зависимостей для использования панели сообщений без внутренней логики отправки сообщения, нужен только текст.
 *
 * @author vv.chekurda
 */
internal class ShareMessageServiceDependency : MessageServiceDependency<Any, Any, Any> {

    override val serviceWrapper: MessageServiceWrapper<Any, Any, Any> = object : MessageServiceWrapper<Any, Any, Any> {
        override fun send(arguments: SendArguments): Single<out Any> = Single.just(Any())
        override fun edit(arguments: EditArguments): Single<out Any> = Single.just(Any())
        override fun read(messageUuid: UUID, conversationUuid: UUID, documentUuid: UUID?): Single<out Any> =
            Single.just(Any())
        override fun readText(messageUuid: UUID, conversation: UUID): Single<MessageTextWithMentions> =
            Single.just(MessageTextWithMentions(StringUtils.EMPTY, StringUtils.EMPTY))
        override fun saveDraft(draft: DraftArguments): Completable = Completable.complete()
        override fun loadDraft(themeUuid: UUID, documentUuid: UUID?, clearDraft: Boolean): Single<out Any> =
            Single.just(Any())
    }

    override val messageResultHelper: MessageResultHelper<Any, Any> = object : MessageResultHelper<Any, Any> {
        override fun isResultError(message: Any): Boolean = false
        override fun isSentResultError(message: Any): Boolean = false
        override fun getSentMessageUuid(message: Any): UUID? = null
        override fun getResultError(message: Any): String = StringUtils.EMPTY
        override fun getSentResultError(message: Any): String = StringUtils.EMPTY
        override fun getSender(message: Any): IPersonModel = PersonModel()
    }

    override val draftResultHelper: DraftResultHelper<Any> = object : DraftResultHelper<Any> {
        override fun getId(draft: Any): UUID = UUID.randomUUID()
        override fun isEmpty(draft: Any): Boolean = true
        override fun getText(draft: Any): String = StringUtils.EMPTY
        override fun getRecipients(draft: Any): List<UUID> = emptyList()
        override fun getQuoteContent(draft: Any): QuoteContent? = null
    }
}