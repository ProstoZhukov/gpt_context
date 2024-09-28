package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.contract.ChatCreationPresenter
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.presenter.ChatCreationPresenterImpl
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency

/**
 * DI модуль экрана создания нового чата
 *
 * @author vv.chekurda
 */
@Module
internal interface ChatCreationModule {

    @Suppress("unused")
    @Binds
    @ChatCreationScope
    fun chatCreationPresenter(presenter: ChatCreationPresenterImpl): ChatCreationPresenter

    @Module
    companion object {

        @JvmStatic
        @Provides
        @ChatCreationScope
        fun provideRecipientSelectionManager(): RecipientSelectionResultManager {
            return themesRegistryDependency.getRecipientSelectionResultManager()
        }
    }
}
