package ru.tensor.sbis.localfeaturetoggle.presentation.di

import android.content.Context
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService

/**
 * @author mb.kruglova
 */
@Module
internal class LocalFeatureToggleModule {
    @Provides
    @LocalFeatureToggleScope
    fun provideStoreFactory(): StoreFactory {
        return DefaultStoreFactory()
    }

    @Provides
    @LocalFeatureToggleScope
    fun provideLocalFeatureToggleService(context: Context): LocalFeatureToggleService {
        return LocalFeatureToggleService(context)
    }
}