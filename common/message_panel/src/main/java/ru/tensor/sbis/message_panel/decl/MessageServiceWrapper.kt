package ru.tensor.sbis.message_panel.decl

import androidx.annotation.WorkerThread
import io.reactivex.Completable
import io.reactivex.Single
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import java.util.*

/**
 * Обёртка над микросервисом отправки сообщений
 *
 * @author vv.chekurda
 */
@WorkerThread
interface MessageServiceWrapper<out MESSAGE_RESULT, out MESSAGE_SENT_RESULT, out DRAFT_RESULT> {

    //region Message
    fun send(arguments: SendArguments): Single<out MESSAGE_SENT_RESULT>

    fun sendLink(conversationUuid: UUID, url: String): Completable =
        Completable.error(NotImplementedError())

    fun edit(arguments: EditArguments): Single<out MESSAGE_RESULT>

    fun read(messageUuid: UUID, conversationUuid: UUID, documentUuid: UUID?): Single<out MESSAGE_RESULT>

    fun readText(messageUuid: UUID, conversation: UUID): Single<MessageTextWithMentions>
    //endregion

    //region Draft
    fun saveDraft(draft: DraftArguments): Completable

    fun loadDraft(themeUuid: UUID, documentUuid: UUID?, clearDraft: Boolean = false): Single<out DRAFT_RESULT>
    //endregion

    //region user typing
    fun notifyUserTyping(conversationUuid: UUID): Completable = Completable.complete()
    //endregion

    /**
     * Начать редактирование сообщения.
     */
    fun beginEditMessage(editingMessage: UUID): Single<CommandStatus> =
        Single.just(CommandStatus(ErrorCode.SUCCESS, StringUtils.EMPTY))

    /**
     * Применить изменения к сообщению (текст передается в метод, вложения крепятся через addAttachments.
     */
    fun commitEditMessage(arguments: EditArguments): Single<out MESSAGE_RESULT> =
        Single.error(NotImplementedError("${this.javaClass.simpleName}.commitEditMessage"))

    /**
     * Отменить редактирование сообщения.
     */
    fun cancelEditMessage(editingMessage: UUID): Single<CommandStatus> =
        Single.just(CommandStatus(ErrorCode.SUCCESS, StringUtils.EMPTY))

    /**
     * Изменить эмоцию отправленного медиа сообщения.
     */
    fun editMediaMessageEmotion(messageUuid: UUID, emotionCode: Int): Completable =
        Completable.error(NotImplementedError("${this.javaClass.simpleName}.editMediaMessageEmotion"))

    fun setAnalyticsUsageName(name: String?) = Unit
}