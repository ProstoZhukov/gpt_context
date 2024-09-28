package ru.tensor.sbis.crud.devices.settings.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.devices.settings.generated.DevicesSettings
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.devices.settings.DevicesSettingsCrudPlugin
import ru.tensor.sbis.crud.devices.settings.di.repository.*
import ru.tensor.sbis.plugin_struct.feature.Feature

/**@SelfDocumented*/
@Component(modules = [DevicesSettingsSingletonModule::class,
    DeviceTypeModule::class,
    WorkplaceModule::class,
    DeviceModule::class,
    SalesPointModule::class])
interface DevicesSettingsSingletonComponent :
        DeviceTypeComponent,
        WorkplaceComponent,
        DeviceComponent,
        SalesPointComponent, Feature {

    /**@SelfDocumented*/
    fun getControllerProvider(): DependencyProvider<DevicesSettings>

    /**@SelfDocumented*/
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        fun build(): DevicesSettingsSingletonComponent
    }

    /**@SelfDocumented*/
    interface Holder {
        val devicesSettingsSingletonComponent: DevicesSettingsSingletonComponent
    }

    companion object {
        fun getInstance(context: Context): DevicesSettingsSingletonComponent {
            val application = context.applicationContext as Application
            return if (application is Holder) {
                application.devicesSettingsSingletonComponent
            } else {
                DevicesSettingsCrudPlugin.devicesSettingsSingletonComponent
            }
        }
    }
}