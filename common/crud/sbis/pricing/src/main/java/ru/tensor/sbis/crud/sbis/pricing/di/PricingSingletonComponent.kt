package ru.tensor.sbis.crud.sbis.pricing.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.crud.sbis.pricing.PricingCrudPlugin
import ru.tensor.sbis.crud.sbis.pricing.di.repository.AvailablePriceListComponent
import ru.tensor.sbis.crud.sbis.pricing.di.repository.AvailablePriceListModule
import ru.tensor.sbis.crud.sbis.pricing.di.repository.LinkedPriceListComponent
import ru.tensor.sbis.crud.sbis.pricing.di.repository.LinkedPriceListModule
import ru.tensor.sbis.plugin_struct.feature.Feature

@Component(
    modules = [PricingSingletonModule::class,
        AvailablePriceListModule::class,
        LinkedPriceListModule::class]
)
interface PricingSingletonComponent :
    AvailablePriceListComponent,
    LinkedPriceListComponent,
    Feature {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder

        fun build(): PricingSingletonComponent
    }

    interface Holder {
        val pricingSingletonComponent: PricingSingletonComponent
    }

    companion object {
        fun getInstance(context: Context): PricingSingletonComponent {
            val application = context.applicationContext as Application
            return if (application is Holder) {
                application.pricingSingletonComponent
            } else {
                PricingCrudPlugin.pricingSingletonComponent
            }
        }
    }
}