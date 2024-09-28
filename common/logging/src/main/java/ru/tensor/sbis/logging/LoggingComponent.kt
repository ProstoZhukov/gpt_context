package ru.tensor.sbis.logging

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.logging.domain.LogDeliveryService
import ru.tensor.sbis.logging.domain.LogPackageService
import ru.tensor.sbis.logging.log_packages.domain.LogDeliveryInteractor
import ru.tensor.sbis.logging.log_packages.domain.LogPackageInteractor
import ru.tensor.sbis.logging.settings.di.LogSettingsComponent
import ru.tensor.sbis.toolbox_decl.logging.ForceLogDeliveryScreenProvider
import ru.tensor.sbis.toolbox_decl.logging.LoggingFeature
import javax.inject.Singleton

/**
 * Компонент модуля логирования.
 *
 * @author av.krymov
 */
@Singleton
@Component(modules = [LoggingComponentModule::class])
interface LoggingComponent {
    fun getLoggingFeature(): LoggingFeature
    fun getForceLogDeliveryScreenProvider(): ForceLogDeliveryScreenProvider?
    fun logPackageService(): LogPackageService
    fun logDeliveryService(): LogDeliveryService

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun appContext(appContext: Context): Builder

        @BindsInstance
        fun forceLogDeliveryScreenProvider(forceLogDeliveryScreenProvider: ForceLogDeliveryScreenProvider?): Builder
        fun build(): LoggingComponent
    }

    fun logSettingsComponentBuilder(): LogSettingsComponent.Builder
    fun logPackageInteractor(): LogPackageInteractor
    fun logDeliveryInteractor(): LogDeliveryInteractor
}

@Module
internal class LoggingComponentModule {
    @Provides
    @Singleton
    fun provideLoggingFeature(): LoggingFeature {
        return LoggingFeatureImpl()
    }

    @Provides
    @Singleton
    fun provideLogPackageService(): LogPackageService {
        return LogPackageService()
    }

    @Provides
    @Singleton
    fun provideLogDeliveryService(): LogDeliveryService {
        return LogDeliveryService()
    }

    @Provides
    @Singleton
    fun provideLogDeliveryInteractor(
        logDeliveryService: LogDeliveryService
    ): LogDeliveryInteractor {
        return LogDeliveryInteractor(logDeliveryService)
    }

    @Provides
    @Singleton
    fun provideLogPackageInteractor(
        logPackageService: LogPackageService
    ): LogPackageInteractor {
        return LogPackageInteractor(logPackageService)
    }
}