package ru.tensor.sbis.application_tools

import android.app.Application
import android.content.Context
import android.content.Intent
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.application_tools.initializer.AnalyticsInitializer
import ru.tensor.sbis.application_tools.initializer.CrashAnalyticsInitializer
import ru.tensor.sbis.application_tools.initializer.CrashlyticsInitializer
import ru.tensor.sbis.application_tools.initializer.LocalDebugUtilsInitializer
import ru.tensor.sbis.application_tools.logsender.checkAndSendLogsDump
import ru.tensor.sbis.application_tools.logsender.getProcessName


/**
 * В методе Application::onCreate() должен быть вызван метод @see DebugTools#init()
 * Пример:
 * override fun onCreate() {
 * super.onCreate()
 * debugTools.init(this)
 * ...
 * }
 */

/**
 * @property application Само текущее приложение.
 * @property enableLocalDebugUtils Если флаг выставлен в true, то будут включены отладочные утилиты для отслеживания утечек памяти и ANR,
 * а так же локальный логгер.
 * Для release сборки, как правило, должен быть выставлен в false.
 * @property stopApplicationOnRxCrash Если флаг выставлен в true, то необработанная ошибка в RxJava будет крашить приложения немедленно,
 * если false - краш будет отправляться в глобальный хук RxJava и логироваться в локальный лог для локальной сборки,
 * а для ci сборку будет еще отправлен в консоль Firebase.
 * Устанавливать флаг в true можно, например, когда необходимо локализовать место краша. В остальных случаях, он должен быть выставлен в false.
 * @property appId Идентификатор приложения, вида ru.tensor.sbis.retail.debug, используется для вывода отладочной информации.
 * @property versionName Строковое представление версии приложения, используется для вывода отладочной информации.
 * @property versionCode Целочисленный код версии приложения, используется для вывода отладочной информации.
 * @property localDebugUtilsInitializer Рекомендуется всегда использовать значение по умолчанию. Инициализатор локальных утилит для отладки - вывод пушей с крашами, логирование и прочее
 * @property crashAnalyticsInitializer Рекомендуется всегда использовать значение по умолчанию. Инициализатор утилит для репорта крашей
 * @constructor
 *
 * @param enableLeakCanary Если флаг выставлен в true, то будет включен LeakCanary.
 * @param enableCrashlytics Если флаг выставлен в true, то будет включена отправка ошибок в Crashlytics (по умолчанию
 * отключена для локальных сборок)
 * @param enableStrictMode Если флаг выставлен в true, то будет включен StrictMode.
 */
class DebugTools(
    private val application: Application,
    private val enableLocalDebugUtils: Boolean,
    private val stopApplicationOnRxCrash: Boolean,
    private val appId: String,
    private val versionName: String,
    @Deprecated("Параметр больше не используется", ReplaceWith(""))
    private val flavor: String = "",
    private val versionCode: Int,
    enableLeakCanary: Boolean = true,
    private val enableCrashlytics: Boolean = BuildConfig.CI_BUILD,
    private val enableStrictMode: Boolean = BuildConfig.DEBUG && !BuildConfig.CI_BUILD, //TODO https://online.sbis.ru/opendoc.html?guid=33f68703-562b-4623-8cd7-288bec0e2ac1&client=3 отложено для CI
    private val publicBuild: Boolean = true,
    private val localDebugUtilsInitializer: LocalDebugUtilsInitializer = LocalDebugUtilsInitializer(
        application = application,
        enableLeakCanary = enableLeakCanary,
        enableCrashlytics = enableCrashlytics,
        enableStrictMode = enableStrictMode
    ),
    private val crashAnalyticsInitializer: CrashAnalyticsInitializer = CrashAnalyticsInitializer(
        application,
        enableLocalDebugUtils,
        stopApplicationOnRxCrash
    ),
    private val crashlyticsInitializer: CrashlyticsInitializer = CrashlyticsInitializer(),
    private val analyticsInitializer: AnalyticsInitializer = AnalyticsInitializer(application),
    private val processNameProvider: (Context) -> String? = ::getProcessName
) {

    /**
     * Инициализация утилит логирования, аналитики и прочего. Метод необходимо вызвать единожды,
     * в методе [Application.onCreate].
     */
    fun init() {
        if (enableLocalDebugUtils) localDebugUtilsInitializer()
        crashAnalyticsInitializer()
        if (enableCrashlytics) crashlyticsInitializer()
        if (publicBuild) analyticsInitializer()

        checkAndSendLogsDump(application, processNameProvider)
    }

    companion object {
        private const val AUTOTEST_LAUNCH_CATEGORY = "SBIS_AUTOTEST_LAUNCH"

        /**
         * Флаг запуска приложения в режиме автотестов (с параметром SBIS_AUTOTEST_LAUNCH)
         */
        val isAutoTestLaunch: Boolean
            get() = AutotestLaunchConfigurationHolder.isAutotestsLaunch

        /**
         * Обновляет флаг [isAutoTestLaunch] и параметры конфигурации для автотестов на основе [intent].
         *
         * @return актуальное значение флага запуска в режиме автотестов.
         */
        fun updateIsAutoTestLaunch(intent: Intent): Boolean = with(AutotestLaunchConfigurationHolder) {
            intent.categories?.let {
                isAutotestsLaunch = AUTOTEST_LAUNCH_CATEGORY in it
            }
            restoreActiveNavigationItem = intent.getBooleanExtra(
                IntentAction.Extra.RESTORE_ACTIVE_NAVIGATION_ITEM,
                restoreActiveNavigationItem
            )
            showAccordionOnAutotestsLaunch = intent.getBooleanExtra(
                IntentAction.Extra.SHOW_ACCORDION_ON_AUTOTESTS_LAUNCH,
                showAccordionOnAutotestsLaunch
            )
            return isAutoTestLaunch
        }
    }
}