package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListParams
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListInteractorImpl
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
/**
 * Di-модуль экрана добавления ссылки.
 *
 * @author dv.baranov
 */
@Module
internal class LinkAdditionModule {

    @Provides
    @LinkAdditionScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @LinkAdditionScope
    fun provideConversationLinksListInteractor(
        params: ConversationLinksListParams
    ): ConversationLinksListInteractor = ConversationLinksListInteractorImpl(params.themeUUID)
}