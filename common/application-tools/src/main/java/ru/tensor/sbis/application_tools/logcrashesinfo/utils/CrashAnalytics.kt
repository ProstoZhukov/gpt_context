package ru.tensor.sbis.application_tools.logcrashesinfo.utils

import android.app.Application
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.Crash
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.CrashViewModel
import ru.tensor.sbis.application_tools.logsender.launchLogsDumpSendingProcessIfNeeded
import java.util.concurrent.TimeoutException

/**
 * @author du.bykov
 *
 * Сборка и отладка крашей через Thread.setDefaultUncaughtExceptionHandler, краш отправляется в отдельном процессе
 * см. LogsDumpSender.launchLogsDumpSendingProcessIfNeeded.
 */
class CrashAnalytics {

    companion object {

        @JvmStatic
        fun init(
            application: Application,
            crashAnalyticsHelper: CrashAnalyticsHelper,
            crashReporter: CrashReporterViaNotification,
            pushesNeeded: Boolean
        ) {
            this.application = application
            this.crashAnalyticsHelper = crashAnalyticsHelper
            this.crashReporter = crashReporter
            this.pushesNeeded = pushesNeeded
            initUncaughtExceptionHandler()
        }

        private var pushesNeeded: Boolean = false
        private lateinit var crashAnalyticsHelper: CrashAnalyticsHelper
        private lateinit var crashReporter: CrashReporterViaNotification
        private lateinit var application: Application

        @JvmStatic
        val allCrashes: List<Crash>
            get() = crashAnalyticsHelper.crashes

        private fun initUncaughtExceptionHandler() {
            val handler = Thread.getDefaultUncaughtExceptionHandler()

            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                analyzeAndReportCrash(throwable)
                if (throwable is TimeoutException && thread.name == daemon) {
                    // ignore
                } else {
                    launchLogsDumpSendingProcessIfNeeded(application)
                    handler.uncaughtException(
                        thread,
                        throwable
                    )
                }
            }
        }

        private fun analyzeAndReportCrash(throwable: Throwable) {
            val crashAnalyzer = CrashAnalyzer(throwable)
            val crash = crashAnalyzer.analysis
            crashAnalyticsHelper.insertCrash(crash)
            if (pushesNeeded) {
                crashReporter.report(CrashViewModel(crash))
            }
        }

        /**
         * Имя демон таймера виртуальной машины, для проверки зависания финализатора объекта при проходе очереди ссылок во время
         * сборки мусора. Таким образом сборка мусора фейлится [TimeoutException] если тот тратит времени больше чем MAX_FINALIZATION_MILLIS (примерно 10 сек).
         * Предположительно такое возможно например если приложение работает в фоне и система переходит в состояние сна более чем на MAX_FINALIZATION_MILLIS
         * посреди запуска GC.
         *
         * https://android.googlesource.com/platform/libcore/+/a7752f4d22097346dd7849b92b9f36d0a0a7a8f3/libdvm/src/main/java/java/lang/Daemons.java#160
         */
        private const val daemon = "FinalizerWatchdogDaemon"
    }
}
