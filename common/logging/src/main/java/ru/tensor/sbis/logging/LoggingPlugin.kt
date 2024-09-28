package ru.tensor.sbis.logging

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.logging.controller_logger.ControllerLogDeliveryProvider
import ru.tensor.sbis.logging.controller_logger.LogDeliveryTree
import ru.tensor.sbis.logging.domain.LogDeliveryService
import ru.tensor.sbis.logging.force_log_delivery.ForceLogDeliveryActivity
import ru.tensor.sbis.logging.force_log_delivery.ForceLogDeliverySplashScreenActivity
import ru.tensor.sbis.logging.force_log_delivery.tryToSetForceLogDeliveryLauncherEnabled
import ru.tensor.sbis.logging.loggingswitch.LoggingSwitchBroadcastReceiver
import ru.tensor.sbis.logging.loggingswitch.SWITCH_LOGGING_INTENT_ACTION
import ru.tensor.sbis.logging.screen_tracker.ScreenTracker
import ru.tensor.sbis.logging.screen_tracker.log_providers.DefaultScreenTrackerLogProvider
import ru.tensor.sbis.logging.screen_tracker.log_providers.MemoryLogProvider
import ru.tensor.sbis.logging.screen_tracker.log_providers.ScreenTrackerLogProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.logging.ForceLogDeliveryScreenProvider
import ru.tensor.sbis.toolbox_decl.logging.LoggingFeature
import ru.tensor.sbis.toolbox_decl.logging.LoggingFragmentProvider
import timber.log.Timber

/**
 * Плагин для управления логированием.
 *
 * @author av.krymov
 */

object LoggingPlugin : BasePlugin<LoggingPlugin.CustomizationOptions>() {
    internal val loggingFeature: LoggingFeature = LoggingFeatureImpl()
    internal lateinit var loggingComponent: LoggingComponent

    private lateinit var commonSingletonComponent: FeatureProvider<CommonSingletonComponent>
    private var forceLogDeliveryScreenProvider: FeatureProvider<ForceLogDeliveryScreenProvider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(LoggingFragmentProvider::class.java) { loggingFeature.getLoggingFragmentProvider() },
        FeatureWrapper(LoggingFeature::class.java) { loggingFeature },
        FeatureWrapper(LogDeliveryService::class.java) { loggingComponent.logDeliveryService() }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponent = it }
        .optional(ForceLogDeliveryScreenProvider::class.java) { forceLogDeliveryScreenProvider = it }
        .build()

    override val customizationOptions = CustomizationOptions()

    override fun initialize() {
        loggingComponent =
            LoggingComponentInitializer(forceLogDeliveryScreenProvider?.get()).init(commonSingletonComponent.get())

        // Во время инициализации, чтобы логи из настройки других плагинов в [doAfterInitialize] уже попадали в логгер контроллера.
        Timber.plant(LogDeliveryTree(ControllerLogDeliveryProvider()))
    }

    override fun doAfterInitialize() {
        application.tryToSetForceLogDeliveryLauncherEnabled(customizationOptions.enableForceLogDeliveryLauncher)
        initScreenTracker()
        initLoggingSwitchBroadcastReceiver()
    }

    private fun initScreenTracker() {
        val logProviders = mutableListOf<ScreenTrackerLogProvider>(DefaultScreenTrackerLogProvider)
        if (customizationOptions.enableMemoryLogWhileScreenTracking) logProviders.add(MemoryLogProvider(application))
        ScreenTracker(application, logProviders)
    }

    // Для регистрации не используем ContextCompat.registerReceiver.
    // Применение ContextCompat приводит к падению тестов плагинной системы,
    // т.к. требуется замокать статический метод.
    // Добавление моков в более чем 25 приложений не выглядит целесообразным,
    // тест располагается на уровне application.
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initLoggingSwitchBroadcastReceiver() {
        val receiver = LoggingSwitchBroadcastReceiver(
            loggingComponent.logDeliveryInteractor(),
            loggingComponent.logPackageInteractor()
        )
        val intentFilter = IntentFilter(SWITCH_LOGGING_INTENT_ACTION)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU &&
            application.applicationInfo.targetSdkVersion > Build.VERSION_CODES.TIRAMISU
        ) {
            application.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            application.registerReceiver(receiver, intentFilter)
        }
    }

    class CustomizationOptions internal constructor() {

        /**
         * Флаг, отвечающий за доступность лаунчера для принудительной отправки логов (по умолчанию лаунчер должен быть отключен).
         * Для корректной работы лаунчера нужно добавить в манифест приложения две Activity:
         * [ForceLogDeliverySplashScreenActivity] и [ForceLogDeliveryActivity], при этом нужно указать ForceLogDeliverySplashScreenActivity как запускаемую
         * (указать для неё <intent-filter> c <category android:name="android.intent.category.LAUNCHER" />).
         * Пример: https://git.sbis.ru/mobileworkspace/apps/droid/retail/-/merge_requests/24083/diffs?commit_id=d3aa8493ec5f65f0fda72ae2afaa02ce0c869f12
         */
        var enableForceLogDeliveryLauncher: Boolean = false


        /**
         * Флаг, отвечающий за логирование свободного места на диске при переходах между экранами
         */
        var enableMemoryLogWhileScreenTracking: Boolean = false
    }
}