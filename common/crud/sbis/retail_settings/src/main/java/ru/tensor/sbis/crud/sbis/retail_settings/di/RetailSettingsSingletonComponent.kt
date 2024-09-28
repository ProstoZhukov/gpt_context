package ru.tensor.sbis.crud.sbis.retail_settings.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.payment_settings.di.PaymentSettingsModule
import ru.tensor.sbis.crud.sbis.retail_settings.RetailSettingsCrudPlugin
import ru.tensor.sbis.crud.sbis.retail_settings.di.repository.RetailSettingsComponent
import ru.tensor.sbis.crud.sbis.retail_settings.di.repository.RetailSettingsModule
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.retail_settings.generated.RetailSettingsService

@Component(modules = [RetailSettingsSingletonModule::class,
    RetailSettingsModule::class, PaymentSettingsModule::class])
interface RetailSettingsSingletonComponent :
        RetailSettingsComponent, Feature {

    fun getControllerProvider(): DependencyProvider<RetailSettingsService>

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        fun build(): RetailSettingsSingletonComponent
    }

    interface Holder {
        val retailSettingsSingletonComponent: RetailSettingsSingletonComponent
    }

    companion object {
        fun getInstance(context: Context): RetailSettingsSingletonComponent {
            val application = context.applicationContext as Application
            return if (application is Holder) {
                application.retailSettingsSingletonComponent
            } else {
                RetailSettingsCrudPlugin.retailSettingsSingletonComponent
            }
        }
    }
}