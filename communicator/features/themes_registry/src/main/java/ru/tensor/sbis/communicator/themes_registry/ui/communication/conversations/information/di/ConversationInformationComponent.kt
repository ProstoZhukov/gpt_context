package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationController
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationView

/**
 * Di-компонент экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
@ConversationInformationScope
@Component(
    dependencies = [CommunicatorCommonComponent::class],
    modules = [ConversationInformationModule::class]
)
internal interface ConversationInformationComponent {

    fun injector(): Injector

    val viewFactory: ConversationInformationView.Factory

    @Component.Factory
    interface Factory {
        fun create(
            commonSingletonComponent: CommunicatorCommonComponent,
            @BindsInstance viewFactory: ConversationInformationView.Factory,
            @BindsInstance conversationInformationData: ConversationInformationData,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner
        ): ConversationInformationComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): ConversationInformationController
    }
}
