package ru.tensor.sbis.crud.payment_settings.di

import dagger.Module
import dagger.Provides
import ru.tensor.devices.settings.generated.SalesPointFacade
import ru.tensor.sbis.common.data.DependencyProvider

/**@SelfDocumented*/
@Module
class PaymentSettingsModule {

    /**@SelfDocumented*/
    @Provides
    internal fun provideController(): DependencyProvider<SalesPointFacade> {
        return DependencyProvider.create { SalesPointFacade.instance() }
    }
}