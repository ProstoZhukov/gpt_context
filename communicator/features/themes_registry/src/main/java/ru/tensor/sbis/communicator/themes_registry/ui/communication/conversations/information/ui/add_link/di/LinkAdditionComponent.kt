package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.ui.LinkAdditionController
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.ui.LinkAdditionView
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListParams

/**
 * Di-компонент экрана добавления ссылки.
 *
 * @author dv.baranov
 */
@LinkAdditionScope
@Component(
    dependencies = [CommonSingletonComponent::class],
    modules = [LinkAdditionModule::class]
)
internal interface LinkAdditionComponent {

    fun injector(): Injector

    val viewFactory: LinkAdditionView.Factory

    @Component.Factory
    interface Factory {
        fun create(
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance params: ConversationLinksListParams,
            @BindsInstance viewFactory: LinkAdditionView.Factory,
        ): LinkAdditionComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): LinkAdditionController
    }
}