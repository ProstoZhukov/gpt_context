package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.di

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.interactor.MessageInformationInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.interactor.MessageInformationInteractorImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation.MessageInformationPresenter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation.MessageInformationPresenterImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.data.MessageInformationModel
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract.ReadStatusListViewDependency
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusFocusChangeListener
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.*

/**
 * Di модуль экрана информации о сообщении
 *
 * @author vv.chekurda
 */
@Module
internal class MessageInformationModule {

    @Provides
    @MessageInformationScope
    fun provideMessageMapper(
        context: SbisThemedContext,
        messageInfo: MessageInformationModel
    ): MessageMapper =
        MessageMapper(
            context,
            messageInfo.isGroupDialog,
            messageInfo.isChannel
        )

    @Provides
    @Reusable
    fun provideMessageInformationInteractor(
        messageController: DependencyProvider<MessageController>,
        messageMapper: MessageMapper,
        messageControllerBinaryMapper: MessageControllerBinaryMapper
    ): MessageInformationInteractor =
        MessageInformationInteractorImpl(messageController, messageMapper, messageControllerBinaryMapper)

    @Provides
    internal fun provideMessageControllerBinaryMapper(): MessageControllerBinaryMapper =
        MessageControllerBinaryMapper()

    @Provides
    fun provideMessageInformationPresenter(
        interactor: MessageInformationInteractor,
        messageInfo: MessageInformationModel
    ): MessageInformationPresenter =
        MessageInformationPresenterImpl(interactor, messageInfo)

    @Provides
    @MessageInformationScope
    fun provideNavigationDelegate(
        dependency: CommunicatorSbisConversationDependency
    ): CommunicatorConversationRouter =
        dependency.getCommunicatorConversationRouter()

    @Provides
    @MessageInformationScope
    fun provideReadStatusListViewDependency(
        fragment: Fragment,
        messageInfo: MessageInformationModel,
        communicatorConversationRouterRouter: CommunicatorConversationRouter,
        focusChangeListener: ReadStatusFocusChangeListener
    ): ReadStatusListViewDependency =
        object : ReadStatusListViewDependency {
            override val fragment: Fragment = fragment
            override val messageUuid: UUID = messageInfo.messageUuid
            override val isGroupConversation: Boolean = messageInfo.isGroupDialog
            override val communicatorConversationRouter: CommunicatorConversationRouter = communicatorConversationRouterRouter
            override val focusChangeListener: ReadStatusFocusChangeListener = focusChangeListener
        }
}