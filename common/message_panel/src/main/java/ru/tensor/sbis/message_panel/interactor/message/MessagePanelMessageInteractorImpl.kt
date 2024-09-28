package ru.tensor.sbis.message_panel.interactor.message

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.message_panel.decl.EditArguments
import ru.tensor.sbis.message_panel.decl.MessageServiceWrapper
import ru.tensor.sbis.message_panel.decl.SendArguments
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import java.util.UUID

/**
 * @author vv.chekurda
 * Создан 10/3/2019
 */
class MessagePanelMessageInteractorImpl<MESSAGE_RESULT, MESSAGE_SENT_RESULT>(
    private val serviceWrapper: MessageServiceWrapper<MESSAGE_RESULT, MESSAGE_SENT_RESULT, *>
) : BaseInteractor(), MessagePanelMessageInteractor<MESSAGE_RESULT, MESSAGE_SENT_RESULT> {

    override fun sendMessage(
        text: String?,
        attachments: List<FileInfo>,
        recipientUuids: List<UUID>?,
        documentUuid: UUID?,
        conversationUuid: UUID?,
        folderUuid: UUID?,
        signActions: SignActions?,
        quotedMessageUuid: UUID?,
        answeredMessageUuid: UUID?,
        metaData: String?
    ): Single<MESSAGE_SENT_RESULT> = serviceWrapper.send(
        SendArguments(
            text,
            attachments,
            recipientUuids.orEmpty(),
            documentUuid,
            conversationUuid,
            folderUuid,
            signActions,
            quotedMessageUuid,
            answeredMessageUuid,
            metaData
        )
    ).compose(getSingleBackgroundSchedulers())

    override fun sendLink(conversationUuid: UUID, url: String): Completable =
        serviceWrapper.sendLink(
            conversationUuid = conversationUuid,
            url = url
        ).compose(completableBackgroundSchedulers)

    override fun editMessage(editingMessage: UUID, newMessageText: String): Single<out MESSAGE_RESULT> =
        serviceWrapper.edit(EditArguments(editingMessage, newMessageText))
            .compose(getSingleBackgroundSchedulers())

    override fun getMessageByUuid(messageUuid: UUID, conversationUuid: UUID, documentUuid: UUID?): Single<MESSAGE_RESULT> =
        serviceWrapper.read(messageUuid, conversationUuid, documentUuid)
            .compose(getSingleBackgroundSchedulers())

    override fun getMessageText(messageUuid: UUID, conversationUuid: UUID): Single<MessageTextWithMentions> =
        serviceWrapper.readText(messageUuid, conversationUuid)
            .compose(getSingleBackgroundSchedulers())

    override fun notifyUserTyping(conversationUuid: UUID): Completable =
        serviceWrapper.notifyUserTyping(conversationUuid)
            .subscribeOn(Schedulers.io())

    override fun beginEditMessage(editingMessage: UUID): Single<CommandStatus> =
        serviceWrapper.beginEditMessage(editingMessage)
            .compose(getSingleBackgroundSchedulers())

    override fun commitEditMessage(
        editingMessage: UUID,
        newMessageText: String,
        serviceObject: String?
    ): Single<out MESSAGE_RESULT> =
        serviceWrapper.commitEditMessage(EditArguments(editingMessage, newMessageText, serviceObject))
            .compose(getSingleBackgroundSchedulers())

    override fun cancelEditMessage(editingMessage: UUID): Single<CommandStatus> =
        serviceWrapper.cancelEditMessage(editingMessage)
            .compose(getSingleBackgroundSchedulers())

    override fun editMediaMessageEmotion(messageUuid: UUID, emotionCode: Int): Completable =
        serviceWrapper.editMediaMessageEmotion(messageUuid, emotionCode)
            .compose(completableBackgroundSchedulers)

    override fun setAnalyticsUsageName(name: String?) {
        serviceWrapper.setAnalyticsUsageName(name)
    }
}