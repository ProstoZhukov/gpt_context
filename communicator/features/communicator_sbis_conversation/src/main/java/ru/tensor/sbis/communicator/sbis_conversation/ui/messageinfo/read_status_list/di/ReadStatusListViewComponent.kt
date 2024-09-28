package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorConversationRouter
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.sbis_conversation.di.singleton.CommunicatorSbisConversationSingletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.navigation.ReadStatusListRouter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.ReadStatusListViewModel
import java.util.*
import javax.inject.Scope

/**
 * Di компонент view списка статусов прочитанности сообщения.
 *
 * @author vv.chekurda
 */
@ReadStatusListViewScope
@Component(
    dependencies = [CommunicatorSbisConversationSingletonComponent::class],
    modules = [ReadStatusListViewModule::class]
)
internal interface ReadStatusListViewComponent {

    val fragment: Fragment

    val readStatusListVm: ReadStatusListViewModel

    val router: ReadStatusListRouter

    val activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer

    @Component.Factory
    interface Factory {
        fun create(
            singleton: CommunicatorSbisConversationSingletonComponent,
            @BindsInstance fragment: Fragment,
            @BindsInstance messageUuid: UUID,
            @BindsInstance communicatorConversationRouter: CommunicatorConversationRouter
        ): ReadStatusListViewComponent
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ReadStatusListViewScope