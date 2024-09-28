package ru.tensor.sbis.message_panel.decl

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import java.util.*

/**
 * Примитивная реализация для [MessageServiceWrapper], которую можно использовать, например, если механика отправки не
 * требуется.
 * Известные сценарии: ДЗЗ, Отзывы в SabyGet
 *
 * @see SimpleMessageResultHelper
 *
 * @author vv.chekurda
 */
open class SimpleMessageServiceWrapper<out MESSAGE_RESULT, out MESSAGE_SENT_RESULT, out DRAFT_RESULT> :
    MessageServiceWrapper<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> {

    override fun send(arguments: SendArguments): Single<out MESSAGE_SENT_RESULT> =
        Single.error(IllegalStateException("Unable to send $arguments. You need to implement MessageServiceWrapper.send() method"))

    override fun edit(arguments: EditArguments): Single<out MESSAGE_RESULT> =
        Single.error(IllegalStateException("Unable to apply edit result $arguments. You need to implement MessageServiceWrapper.edit() method"))

    override fun read(messageUuid: UUID, conversationUuid: UUID, documentUuid: UUID?): Single<out MESSAGE_RESULT> =
        Single.error(IllegalStateException("Unable to read object with id $messageUuid. You need to implement MessageServiceWrapper.read() method"))

    override fun readText(messageUuid: UUID, conversation: UUID): Single<MessageTextWithMentions> =
        Single.error(IllegalStateException("Unable to read text with id $messageUuid and $conversation. You need to implement MessageServiceWrapper.readText() method"))

    override fun saveDraft(draft: DraftArguments): Completable =
        Completable.error(IllegalStateException("Draft $draft was not saved. You need to implement MessageServiceWrapper.saveDraft() method"))

    override fun loadDraft(themeUuid: UUID, documentUuid: UUID?, clearDraft: Boolean): Single<out DRAFT_RESULT> =
        Single.error(IllegalStateException("Unable to load draft with id $themeUuid. You need to implement MessageServiceWrapper.loadDraft() method"))
}