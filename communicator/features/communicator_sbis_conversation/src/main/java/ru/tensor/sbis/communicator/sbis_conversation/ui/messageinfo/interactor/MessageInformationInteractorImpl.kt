package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.interactor

import io.reactivex.Observable
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import java.util.UUID

/**
 * Реализация интерактора информации о сообщении
 *
 * @param messageControllerDependencyProvider контроллер сообщений
 * @param messageMapper                       маппер сообщений
 */
internal class MessageInformationInteractorImpl(
    private val messageControllerDependencyProvider: DependencyProvider<MessageController>,
    private val messageMapper: MessageMapper,
    private val messageControllerBinaryMapper: MessageControllerBinaryMapper
) : BaseInteractor(), MessageInformationInteractor {

    override fun getMessage(messageUuid: UUID): Observable<ConversationMessage> =
        Observable.fromCallable { messageControllerDependencyProvider.get().read(messageUuid) }
            .map { messageControllerBinaryMapper.map(it) }
            .map {
                messageMapper.apply(it)
            }
            .compose(getObservableBackgroundSchedulers())

    override fun clearReferences() {
        messageMapper.clearReferences()
    }
}