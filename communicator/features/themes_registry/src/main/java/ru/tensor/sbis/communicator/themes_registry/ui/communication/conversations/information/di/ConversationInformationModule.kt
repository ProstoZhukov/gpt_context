package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.di

import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryPlugin.filesPickerFactoryProvider
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationRouter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.ConversationInformationRouterImpl
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPicker
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider

/**
 * Di-модуль экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
@Module
internal class ConversationInformationModule {

    @Provides
    @ConversationInformationScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @ConversationInformationScope
    fun provideRecipientSelectionManager(): RecipientSelectionResultManager {
        return themesRegistryDependency.getRecipientSelectionResultManager()
    }

    @Provides
    @ConversationInformationScope
    fun providePersonCardProvider(): PersonCardProvider = themesRegistryDependency

    @Provides
    @ConversationInformationScope
    fun provideConversationInformationRouter(
        recipientSelectionResultManager: RecipientSelectionResultManager,
        conversationInformationData: ConversationInformationData,
        personCardProvider: PersonCardProvider,
        filesPicker: SbisFilesPicker
    ): ConversationInformationRouter =
        ConversationInformationRouterImpl(
            recipientSelectionResultManager,
            conversationInformationData,
            personCardProvider,
            filesPicker
        )

    @Provides
    @ConversationInformationScope
    fun provideSbisFilesPicker(viewModelStoreOwner: ViewModelStoreOwner): SbisFilesPicker =
        filesPickerFactoryProvider.get().createSbisFilesPicker(viewModelStoreOwner)
}