package ru.tensor.sbis.base_app_components

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.CallSuper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import io.reactivex.internal.schedulers.RxThreadFactory
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.auth_content.AuthContentPlugin
import ru.tensor.sbis.base_app_components.initializers.AsyncInitializer
import ru.tensor.sbis.base_app_components.navx.NavxInitializerFactory
import ru.tensor.sbis.base_app_components.util.getProcessName
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.common.util.getProcessName
import ru.tensor.sbis.common_firebase_trace.firebaseTracer
import ru.tensor.sbis.controller_utils.MemoryWarningCallback
import ru.tensor.sbis.controller_utils.PlatformInitializer
import ru.tensor.sbis.controller_utils.loading.StartupExposer
import ru.tensor.sbis.core_saby_app.CoreSabyApp
import ru.tensor.sbis.design.change_theme.util.createThemeAppContext
import ru.tensor.sbis.design.sbis_text_view.utils.SbisTextViewObtainHelper
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant.UserActivityListener
import ru.tensor.sbis.entrypoint_guard.init.AppInitStateHolder.InitStatus
import ru.tensor.sbis.entrypoint_guard.init.AppInitializer
import ru.tensor.sbis.language.LanguageFeatureImpl
import ru.tensor.sbis.platform.generated.AppTypeEnum
import ru.tensor.sbis.plugin_manager.PluginManager
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.toolbox_decl.language.LanguageProvider
import java.util.concurrent.Executors
import ru.tensor.sbis.design.R as RD
import ru.tensor.sbis.language.LanguageFeatureImpl.Companion as Lang

/**
 * Базовый класс [Application] для приложений тензор, содерджит все необходимые механизмы для инициализации контроллера
 * и локализации.
 * @param useSystemLocale - применяется локаль устройства.
 * @param initializerFactory - фабрика для создания [AppInitializer]
 *
 * @author du.bykov
 */
abstract class BaseSbisApplication(
    private val initializerFactory: AppInitializerFactory<*>?,
    private val useSystemLocale: Boolean = false
) : Application(), EntryPointGuard.EntryPoint {

    @Deprecated("obsolete, please switch to primary class constructor")
    constructor() : this(
        useSystemLocale = false,
        initializerFactory = null
    )

    @Deprecated("obsolete, please switch to primary class constructor")
    constructor(useSystemLocale: Boolean) : this(
        useSystemLocale = useSystemLocale,
        initializerFactory = null
    )

    init {
        with(EntryPointGuard) {
            setBaseContextPatcher { context ->
                if (useSystemLocale) return@setBaseContextPatcher context
                context
                    ?.let(LanguageFeatureImpl::getInteractor)
                    ?.updateResources(context)
            }
        }
    }

    /**
     * Для baseContext обязательно должна использоваться реализация ContextImpl, однако так просто
     * ссылку менять нельзя, поэтому переопределяем getResources и используем в нём этот контекст
     * для получения локализованных ресурсов.
     */
    private lateinit var localizableContext: Context

    /**
     * Scheduler для IO, чтобы избежать их чрезмерного создания, которое может приводить к
     * ошибке OutOfMemory (по умолчанию ограничения нет)
     *  Размер пула = 48 [IO_RX_THREAD_POOL_SIZE]
     *  Префикс = "SbisRxCachedThreadScheduler" [IO_RX_THREAD_PREFIX]
     */
    private val ioScheduler by lazy {
        Executors
            .newFixedThreadPool(IO_RX_THREAD_POOL_SIZE, RxThreadFactory(IO_RX_THREAD_PREFIX))
            .let(Schedulers::from)
    }

    abstract val debugTools: DebugTools
    abstract val baseSabyApp: CoreSabyApp
    abstract val pluginManager: PluginManager

    /**
     * Ключ для работы аппметрики. При необходимости инициализации аппметрики необходимо его переопределить,
     * если в приложении аппметрика иницииализируется своими особыми механизмами, то прежде всего стоит подумать,
     * насколько это необходимо и рассмотреть возможность перехода на общую логику.
     */
    protected open val yandexAppMetricaApiKey: String? = null

    /**
     * Является ли приложение запущенным в основном процессе.
     */
    protected val isMainProcess by lazy {
        packageName == getProcessName()
    }

    /**
     * В приложении осуществляется инициализация контроллера в фоновом потоке, активити открывается пустая и ждет
     * колбека, который сообщит о результате инициализации контроллера.
     */
    @Deprecated("obsolete, please define an initializer by passing [AppInitializerFactory] to primary constructor of the class")
    protected open val asyncInit: Boolean = false

    /**
     * Список идентификаторов доступных в приложении пунктов навигации, в том числе представленных вкладками верхней
     * навигационной панели.
     */
    protected open val availableNavigationItems: List<NavxId>? = null

    @Deprecated("obsolete, please define an initializer by passing [AppInitializerFactory] to primary constructor of the class")
    protected open fun createAsyncInitializer(
        initPlatform: () -> Unit,
        initPluginSystem: () -> Unit,
        onInitComplete: () -> Unit
    ): AppInitializer<*> = AsyncInitializer(
        controllerAction = initPlatform,
        uiAction = {
            initPluginSystem.invoke()
            onInitComplete.invoke()
        },
        withUiStatus = initStartupExposer
    )

    /**
     * Нужно ли инициализировать работу [StartupExposer].
     */
    protected var initStartupExposer: Boolean = true

    override final fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (useSystemLocale) {
            LanguageFeatureImpl.getInteractor(base).updateResourcesSilently(base)
        }
        EntryPointGuard.baseContextPatcher.invoke(base)?.let {
            localizableContext = it
        }

        if (isSharedSettingsProcess) return
    }

    override fun getAssets(): AssetManager = localizableContext.assets

    override fun getResources(): Resources = localizableContext.resources

    override final fun onConfigurationChanged(config: Configuration) {
        val newConfig = Configuration(config)
        if (!isSharedSettingsProcess && isAppInitialized()) {
            localizableContext = getLocalizableContext(
                baseContext,
                newConfig
            ) // Получили другие метрики и прочее, но сохраняем нашу локаль.
        }
        SbisTextViewObtainHelper.onConfigurationChanged(config)
        ThemeTokensProvider.onConfigurationChanged(config)
        super.onConfigurationChanged(newConfig)
    }

    abstract fun getLocalizableContext(
        context: Context,
        config: Configuration
    ): Context

    /** Произошла пользовательская активность на компоненте [activity]. */
    protected open fun onUserInteraction(activity: ComponentActivity) = Unit

    /** Вызывается в [onCreate], если процесс запущен НЕ для ContentProvider'a авторизации. */
    @CallSuper
    protected open fun onCreateInMainProcess() {
        initializeApp(AppTypeEnum.GENERIC_APPLICATION)
    }

    @CallSuper
    protected open fun onCreateInNonMainProcess() {
        val type = if (isPushNotificationProcess) {
            /*
            * FIXME: Поменять тип на AppTypeEnum.NOTIFICATION_EXTENSION
            * https://online.sbis.ru/opendoc.html?guid=4ce3a966-85fe-49c2-b9a4-7327e4a33f42&client=3
            */
            AppTypeEnum.GENERIC_APPLICATION
        } else {
            AppTypeEnum.GENERIC_APPLICATION
        }
        initializeApp(type)
    }

    private fun initializeApp(type: AppTypeEnum) {
        AppLifecycleTracker.start(this)
        SbisTextViewObtainHelper.init(resources.configuration)
        ThemeTokensProvider.init(resources.configuration)
        subscribeToActivityCallbacks()

        runBeforeInitActions()
        with(EntryPointGuard) {
            val factory = initializerFactory ?: createProxyInitializerFactory()

            initializeApp(
                factory.create(
                    initStartupExposer = initStartupExposer,
                    controllerAction = { initPlatform(type) },
                    doInitPluginSystem = ::doInitPluginSystem,
                    doAfterInitPluginSystem = {
                        doAfterInitPluginSystem()
                        runAfterInitActions()
                    }
                ).also { initializer = it }
            )
        }
    }

    private fun runBeforeInitActions() {
        LanguageProvider.setFinder { LanguageFeatureImpl(this) }
        Lang.setSupportedLocales()
        setupRxConfiguration()
    }

    private fun runAfterInitActions() {
        registerComponentCallbacks(MemoryWarningCallback())
    }

    private fun initPlatform(type: AppTypeEnum) = PlatformInitializer.init(
        application = this,
        tracerDelegate = firebaseTracer(),
        availableNavigationFilterInitializer = NavxInitializerFactory.create(
            this,
            availableNavigationItems
        ),
        type = type
    )

    private fun doInitPluginSystem() {
        baseSabyApp.doInit(
            this, pluginManager,
            getThemedAppContext()
        )
    }

    private fun doAfterInitPluginSystem() {
        if (useSystemLocale.not()) {
            localizableContext = Lang.actualizeLocaleAndUpdateResources(baseContext)
        }
        baseSabyApp.doAfterInit(pluginManager)
    }

    private fun subscribeToActivityCallbacks() = with(EntryPointGuard.activityAssistant) {
        addUserActivityListener(object : UserActivityListener {
            override fun onUserInteraction(activity: ComponentActivity) {
                this@BaseSbisApplication.onUserInteraction(activity)
            }
        })
    }

    /**
     * Функция для поставки темизированного [Application.getApplicationContext].
     * Нужно реализовать на уровне каждого приложения, чтобы иметь возможность получить темизированный контекст
     * приложения в плагинной системе [BasePlugin.themedAppContext].
     *
     * В базовой реализации передает контекст с дефолтной темой.
     */
    open fun getThemedAppContext(): Context = applicationContext.createThemeAppContext(
        RD.style.DefaultLightTheme,
        RD.style.BaseAppTheme
    )

    override final fun onCreate() {
        if (isMainProcess) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("Before App super.onCreate")
        }

        super.onCreate()

        if (isSharedSettingsProcess) return

        AppConfig.init(this)
        debugTools.init()
        if (useSystemLocale.not()) {
            localizableContext = LanguageFeatureImpl.getInteractor(this).updateResources(baseContext)
        }

        yandexAppMetricaApiKey?.let { apiKey ->
            if (!isMetricaProcess()) {
                val metricaConfig = AppMetricaConfig.newConfigBuilder(apiKey).build()
                AppMetrica.activate(this, metricaConfig)
                AppMetrica.enableActivityAutoTracking(this)
            }
        }

        /**
         * Инициализации АппМетрики конфликтует с инициализацией Firebase Performance Monitoring
         * Поэтому должны выполняться два условия:
         * 1) В Application#onCreate() необходимо вызвать FirebaseApp.initializeApp(this) строго до активации AppMetrica SDK
         * 2) FirebaseApp.initializeApp(this) необходимо вызывать и в процессе АппМетрики тоже
         * https://appmetrica.yandex.ru/docs/ru/troubleshooting/update-firebase
         */
        if (isMetricaProcess()) return

        if (isMainProcess) {
            onCreateInMainProcess()
        } else {
            onCreateInNonMainProcess()
        }
    }

    /**
     * Ограничивает число потоков в пуле для IO, чтобы избежать их чрезмерного создания, которое может приводить к
     * ошибке OutOfMemory (по умолчанию ограничения нет)
     */
    private fun setupRxConfiguration() {
        RxJavaPlugins.setIoSchedulerHandler { ioScheduler }
    }

    /** Если процесс поднят для ContentProvider'a авторизации, стоит по минимуму нагружать Application */
    private val isSharedSettingsProcess by lazy {
        AuthContentPlugin.isSharedSettingsProcess(this)
    }

    private val isPushNotificationProcess: Boolean
        get() = getProcessName()?.contains(PUSH_NOTIFICATION_PROCESS_NAME) ?: false

    private var initializer: AppInitializer<*>? = null

    private fun isAppInitialized() =
        initializer?.let { it.initStatus.value == InitStatus.InitCompleted } ?: false

    private fun isMetricaProcess(): Boolean {
        val processName = getProcessName(this)
        return processName != null && processName.endsWith("Metrica")
    }

    private fun createProxyInitializerFactory(): AppInitializerFactory<*> {
        val legacyAsyncInitializerFactory = AppInitializerFactory { _,
                                                                    controllerAction,
                                                                    doInitPluginSystem,
                                                                    doAfterInitPluginSystem ->
            createAsyncInitializer(
                initPlatform = controllerAction,
                initPluginSystem = doInitPluginSystem,
                onInitComplete = doAfterInitPluginSystem
            )
        }

        return AppInitializerFactory { initStartupExposer,
                                       controllerAction,
                                       doInit,
                                       doAfterInit ->
            val factory = when (asyncInit) {
                true -> legacyAsyncInitializerFactory
                false -> AppInitializerFactory.SequenceBlocking()
            }

            return@AppInitializerFactory factory.create(
                initStartupExposer = initStartupExposer,
                controllerAction = controllerAction,
                doInitPluginSystem = doInit,
                doAfterInitPluginSystem = doAfterInit
            )
        }
    }

    companion object {

        /**
         * Взято значение в 75% от используемого в OkHttp/Retrofit (64), поскольку оно кажется избыточным
         */
        private const val IO_RX_THREAD_POOL_SIZE = 48
        private const val IO_RX_THREAD_PREFIX = "SbisRxCachedThreadScheduler"

        private const val DEBUG_POSTFIX = ".debug"
        private const val PUSH_NOTIFICATION_PROCESS_NAME = ":push_cloud_messaging_process"
    }
}
