package ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.data.ConversationDetailsParams
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageThreadActionListener
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationDataDispatcher
import ru.tensor.sbis.design.message_view.model.getThreadData
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.persons.util.formatName
import java.util.UUID

/**
 * Делегат реестра сообщений для работы с тредами.
 *
 * @author da.zhukov
 */
internal class ConversationThreadActionDelegate(
    private val coreConversationInfo: CoreConversationInfo,
    private val interactor: ConversationInteractor,
    private val dataDispatcher: ConversationDataDispatcher
) :
    ConversationMessagesBaseDelegate(),
    MessageThreadActionListener {

    override fun onThreadMessageClicked(data: ConversationMessage) {
        view?.forceHideKeyboard()
        val threadData = requireNotNull(data.viewData.getThreadData())

        val isGroupConversation = threadData.recipientCount > 1
        val title = threadData.title
            ?: threadData.recipients.joinToString {
                it.name.formatName(
                    if (isGroupConversation) PersonNameTemplate.SURNAME_N
                    else PersonNameTemplate.SURNAME_NAME
                )
            }
        val viewData = threadData.recipients.map {
            PersonData(it.uuid, it.photoUrl, it.initialsStubData as? InitialsStubData)
        }

        val params = ConversationDetailsParams(
            dialogUuid = threadData.dialogUuid,
            messageUuid = threadData.relevantMessageUuid,
            fromParentThread = true,
            title = title,
            viewData = viewData,
            isGroupConversation = threadData.recipientCount > 1
        )
        router?.showConversation(params)
    }

    override fun onThreadCreationServiceClicked(data: ConversationMessage) {
        val threadInfo = requireNotNull(data.message?.threadInfo)
        view?.forceHideKeyboard()
        if (coreConversationInfo.fromParentThread) {
            BackStackMessageHighlights.highlightThread(threadInfo.parentConversationMessageUuid)
            router?.exit()
        } else {
            val params = ConversationDetailsParams(
                dialogUuid = threadInfo.parentConversationUuid,
                messageUuid = threadInfo.parentConversationMessageUuid,
                highlightMessage = true
            )
            router?.showConversation(params)
        }
    }

    /** @SelfDocumented */
    fun showThreadCreation(message: Message) {
        interactor.getMessageRecipients(message)
            .subscribe { recipients ->
                if (recipients.isNotEmpty()) {
                    showThreadCreation(
                        message.uuid,
                        recipients.map { it.uuid!! }.asArrayList(),
                        recipients
                    )
                } else {
                    dataDispatcher.sendConversationEvent(ConversationEvent.SELECT_THREAD_PARTICIPANTS)
                }
            }
            .storeIn(compositeDisposable)
    }

    /** @SelfDocumented */
    fun showThreadCreation(
        messageUuid: UUID,
        participantsUuids: List<UUID>,
        viewData: List<PersonData> = emptyList(),
    ) {
        val params = ConversationDetailsParams(
            threadCreationInfo = ThreadInfo(
                parentConversationUuid = coreConversationInfo.conversationUuid!!,
                parentConversationMessageUuid = messageUuid,
                isChat = coreConversationInfo.isChat,
            ),
            participantsUuids = participantsUuids.asArrayList(),
            viewData = viewData.take(10),
            fromParentThread = true,
            needToShowKeyboard = true
        )
        router?.showConversation(params)
    }
}

/** @SelfDocumented */
internal object BackStackMessageHighlights {

    private val threadRootMessage = MutableSharedFlow<UUID>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /** @SelfDocumented */
    val highlightThread: Flow<UUID> = threadRootMessage

    /** @SelfDocumented */
    fun highlightThread(rootMessageUuid: UUID) {
        threadRootMessage.tryEmit(rootMessageUuid)
    }
}