package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.di

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.sbis_conversation.contract.CommunicatorSbisConversationDependency
import ru.tensor.sbis.communicator.sbis_conversation.di.singleton.CommunicatorSbisConversationSingletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.data.MessageInformationModel
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation.MessageInformationPresenter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.contract.ReadStatusListViewDependency
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusFocusChangeListener

/**
 * Di компонент экрана информации о сообщении.
 *
 * @author vv.chekurda
 */
@MessageInformationScope
@Component(
    modules = [MessageInformationModule::class],
    dependencies = [CommunicatorSbisConversationSingletonComponent::class]
)
internal interface MessageInformationComponent {

    val messageInformationPresenter: MessageInformationPresenter
    val messageControllerDependencyProvider: DependencyProvider<MessageController>
    val context: Context
    val conversationToolbarEventManager: ConversationToolbarEventManager
    val communicatorSbisConversationDependency: CommunicatorSbisConversationDependency
    val readStatusListViewDependency: ReadStatusListViewDependency
    val communicatorConversationRouter: CommunicatorConversationRouter
    val communicatorActivityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: Fragment,
            @BindsInstance focusChangeListener: ReadStatusFocusChangeListener,
            @BindsInstance messageInfo: MessageInformationModel,
            component: CommunicatorSbisConversationSingletonComponent
        ): MessageInformationComponent
    }
}