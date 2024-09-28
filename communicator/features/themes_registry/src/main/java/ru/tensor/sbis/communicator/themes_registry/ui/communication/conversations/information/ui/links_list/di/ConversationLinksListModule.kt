package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.di

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.LinkCollectionProvider
import ru.tensor.sbis.communicator.generated.LinkFilter
import ru.tensor.sbis.communicator.themes_registry.databinding.CommunicatorFragmentConversationLinksListBinding
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListCollectionWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListCollectionWrapperImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListFilterHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListParams
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.mappers.ConversationLinksListMapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListInteractorImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListComponentFactory
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListRouter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListRouterImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListView
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.ConversationLinksListViewImpl
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.helpers.LinkItemLongClickHandler
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
/**
 * Di-модуль экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
@Module
internal class ConversationLinksListModule {

    @Provides
    @ConversationLinksListScope
    fun provideViewFactory(
        listComponentFactory: ConversationLinksListComponentFactory,
        linkItemLongClickHandler: LinkItemLongClickHandler
    ): ConversationLinksListView.Factory {
        return ConversationLinksListView.Factory {
            ConversationLinksListViewImpl(
                CommunicatorFragmentConversationLinksListBinding.bind(it),
                listComponentFactory,
                linkItemLongClickHandler
            )
        }
    }

    @Provides
    @ConversationLinksListScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @ConversationLinksListScope
    fun provideConversationLinksListComponentFactory(
        viewModelStoreOwner: ViewModelStoreOwner,
        wrapper: ConversationLinksListCollectionWrapper,
        mapper: ConversationLinksListMapper,
    ): ConversationLinksListComponentFactory = ConversationLinksListComponentFactory(
        viewModelStoreOwner,
        wrapper,
        mapper,
    )

    @Provides
    @ConversationLinksListScope
    fun provideConversationLinksListRouter(): ConversationLinksListRouter = ConversationLinksListRouterImpl()

    @Provides
    @ConversationLinksListScope
    fun provideConversationLinksListCollectionWrapper(
        linkCollectionProvider: DependencyProvider<LinkCollectionProvider>,
        filterHolder: ConversationLinksListFilterHolder
    ): ConversationLinksListCollectionWrapper = ConversationLinksListCollectionWrapperImpl(
        linkCollectionProvider,
        filterHolder
    )

    @Provides
    @ConversationLinksListScope
    fun provideLinkCollectionProvider(): DependencyProvider<LinkCollectionProvider> =
        DependencyProvider.create(LinkCollectionProvider::instance)

    @Provides
    @ConversationLinksListScope
    fun provideFilterHolder(
        params: ConversationLinksListParams
    ): ConversationLinksListFilterHolder = ConversationLinksListFilterHolder(
        LinkFilter(
            null,
            params.themeUUID,
            null,
            true
        ),
    )

    @Provides
    @ConversationLinksListScope
    fun provideConversationLinksListMapper(
        context: Context,
        actionHandler: LinkItemLongClickHandler
    ): ConversationLinksListMapper = ConversationLinksListMapper(context, actionHandler)

    @Provides
    @ConversationLinksListScope
    fun provideLinkItemClickActionHandler(
        scope: LifecycleCoroutineScope,
    ): LinkItemLongClickHandler = LinkItemLongClickHandler(scope)

    @Provides
    @ConversationLinksListScope
    fun provideConversationLinksListInteractor(
        params: ConversationLinksListParams
    ): ConversationLinksListInteractor = ConversationLinksListInteractorImpl(params.themeUUID)
}
