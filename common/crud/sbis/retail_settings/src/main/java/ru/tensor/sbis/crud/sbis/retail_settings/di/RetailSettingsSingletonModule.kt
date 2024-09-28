package ru.tensor.sbis.crud.sbis.retail_settings.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.retail_settings.generated.RetailSettingsService

@Module
class RetailSettingsSingletonModule {

    @Provides
    internal fun provideDevicesSettings(): DependencyProvider<RetailSettingsService> =
            DependencyProvider.create {
                RetailSettingsService.instance()
            }
}