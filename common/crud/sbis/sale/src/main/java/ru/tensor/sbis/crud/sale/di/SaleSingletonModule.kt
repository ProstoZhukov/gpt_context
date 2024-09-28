package ru.tensor.sbis.crud.sale.di

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.sale.mobile.generated.SaleMobileService

@Module
class SaleSingletonModule {

    @Provides
    internal fun provideSaleMobileService(): DependencyProvider<SaleMobileService> = DependencyProvider.create {
        SaleMobileService.instance()
    }
}