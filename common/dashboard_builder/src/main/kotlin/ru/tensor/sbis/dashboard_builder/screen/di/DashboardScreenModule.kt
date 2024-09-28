package ru.tensor.sbis.dashboard_builder.screen.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.dashboard_builder.BuildConfig
import ru.tensor.sbis.dashboard_builder.config.DashboardConfiguration
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * @author am.boldinov
 */
@Module
internal class DashboardScreenModule {

    @Provides
    @DashboardScreenScope
    fun provideStoreFactory(): StoreFactory {
        return if (BuildConfig.DEBUG) {
            LoggingStoreFactory(AndroidStoreFactory.timeTravel())
        } else {
            AndroidStoreFactory.default()
        }
    }

    @Provides
    @DashboardScreenScope
    fun provideConfiguration(themedContext: SbisThemedContext): DashboardConfiguration {
        return DashboardConfiguration.create(themedContext)
    }
}