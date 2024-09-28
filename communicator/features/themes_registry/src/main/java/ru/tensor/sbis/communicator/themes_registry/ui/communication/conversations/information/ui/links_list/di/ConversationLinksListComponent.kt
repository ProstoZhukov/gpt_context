package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListParams
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListController
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListView

/**
 * Di-компонент экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
@ConversationLinksListScope
@Component(
    dependencies = [CommonSingletonComponent::class],
    modules = [ConversationLinksListModule::class]
)
internal interface ConversationLinksListComponent {

    fun injector(): Injector

    val viewFactory: ConversationLinksListView.Factory

    @Component.Factory
    interface Factory {
        fun create(
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance params: ConversationLinksListParams,
            @BindsInstance viewModelStoreOwner: ViewModelStoreOwner,
            @BindsInstance scope: LifecycleCoroutineScope,
        ): ConversationLinksListComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(
            fragment: Fragment,
            viewFactory: ConversationLinksListView.Factory
        ): ConversationLinksListController
    }
}
