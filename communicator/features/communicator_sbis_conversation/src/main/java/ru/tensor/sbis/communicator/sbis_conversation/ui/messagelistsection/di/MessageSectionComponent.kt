package ru.tensor.sbis.communicator.sbis_conversation.ui.messagelistsection.di

import android.content.Context
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessagesListAdapter
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.di.singleton.CommunicatorSbisConversationSingletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import javax.inject.Named

/**
 * Di компонент секции сообщений
 *
 * @author vv.chekurda
 */
@MessageSectionScope
@Component(modules = [MessageSectionModule::class], dependencies = [CommunicatorSbisConversationSingletonComponent::class])
internal interface MessageSectionComponent {

    val conversationMessagesPresenter: ConversationMessagesContract.Presenter<*>
    val conversationToolbarPresenter: ConversationToolbarContract.Presenter<*>
    val conversationMessagePanelPresenter: ConversationMessagePanelContract.Presenter<*>
    val adapter: MessagesListAdapter
    val dateViewUpdater: ListDateViewUpdater
    val communicatorActivityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(@Named(MESSAGE_SECTION_CONTEXT) context: Context): Builder

        @BindsInstance
        fun conversationOpenData(@Named("CoreConversationInfo") coreConversationInfo: CoreConversationInfo): Builder

        @BindsInstance
        fun viewModel(conversationViewModel: ConversationViewModel): Builder

        @BindsInstance
        fun viewModelStoreOwner(viewModelStoreOwner: ViewModelStoreOwner): Builder

        fun sbisConversationSingletonComponent(component: CommunicatorSbisConversationSingletonComponent): Builder

        fun build(): MessageSectionComponent
    }
}

internal const val MESSAGE_SECTION_CONTEXT = "message_section_context"