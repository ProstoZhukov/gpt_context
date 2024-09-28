package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory

/**
 * @author dv.baranov
 */

/** @SelfDocumented */
@Module
internal class RateModule {

    @Provides
    @RateScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }
}