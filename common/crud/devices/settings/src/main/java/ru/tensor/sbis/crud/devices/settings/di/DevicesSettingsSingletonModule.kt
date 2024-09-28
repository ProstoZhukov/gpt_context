package ru.tensor.sbis.crud.devices.settings.di

import dagger.Module
import dagger.Provides
import ru.tensor.devices.settings.generated.DevicesSettings
import ru.tensor.sbis.common.data.DependencyProvider

/**@SelfDocumented*/
@Module
internal class DevicesSettingsSingletonModule {

    /**@SelfDocumented*/
    @Provides
    internal fun provideDevicesSettingsDependencyProvider(): DependencyProvider<DevicesSettings> =
        DependencyProvider.create {
            DevicesSettings.instance()
        }
}