package ru.tensor.sbis.recipient_selection.profile.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.recipient_selection.profile.mapper.ContactItemMapper
import ru.tensor.sbis.recipient_selection.profile.mapper.FolderAndGroupItemMapper
import ru.tensor.sbis.recipient_selection.profile.mapper.ProfileAndContactItemMapper
import javax.inject.Named

internal const val CONTACT_SELECTION_RESULT_MANAGER_FOR_REPOST = "CONTACT_SELECTION_RESULT_MANAGER_FOR_REPOST"

/**
 * Базовый модуль - поставщик менеджера и мапперов для выбора получателей в сообщениях,
 * видеозвонках, репостах
 */
@Module
internal class RecipientSelectionSingletonModule {

    @Provides
    @PerApp
    fun provideRecipientSelectionManager(): RecipientSelectionResultManager =
        RecipientSelectionResultManager()

    @Provides
    @PerApp
    @Named(CONTACT_SELECTION_RESULT_MANAGER_FOR_REPOST)
    fun provideContactsSelectionResultManagerForRepost(): RecipientSelectionResultManager =
        RecipientSelectionResultManager()

    @Provides
    @PerApp
    fun provideContactItemMapper(context: Context): ContactItemMapper =
        ContactItemMapper(context)

    @Provides
    @PerApp
    fun provideProfileAndContactItemMapper(context: Context): ProfileAndContactItemMapper =
        ProfileAndContactItemMapper(context)

    @Provides
    @PerApp
    fun provideFolderAndGroupItemMapper(context: Context): FolderAndGroupItemMapper =
        FolderAndGroupItemMapper(context)
}