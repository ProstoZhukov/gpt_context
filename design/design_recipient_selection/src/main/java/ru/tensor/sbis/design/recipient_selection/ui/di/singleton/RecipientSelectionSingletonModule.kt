package ru.tensor.sbis.design.recipient_selection.ui.di.singleton

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.design.recipient_selection.domain.result_manager.RecipientSelectionResultManagerImpl

/**
 * DI-модуль singleton компонента выбора получателей.
 *
 * @author vv.chekurda
 */
@Module
internal class RecipientSelectionSingletonModule {

    @Provides
    @PerApp
    fun provideRecipientSelectionResultManagerImpl(): RecipientSelectionResultManagerImpl =
        RecipientSelectionResultManagerImpl()

    @Provides
    @PerApp
    fun provideRecipientSelectionManager(
        manager: RecipientSelectionResultManagerImpl
    ): RecipientSelectionResultManager = manager

    @Provides
    @PerApp
    fun provideRecipientSelectionResultDelegate(
        manager: RecipientSelectionResultManagerImpl
    ): RecipientSelectionResultDelegate = manager
}