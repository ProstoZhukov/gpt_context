package ru.tensor.sbis.application_tools.initializer

import com.github.anrwatchdog.ANRWatchDog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.tensor.sbis.application_tools.StackTraceLogger
import timber.log.Timber

/**
 * @author du.bykov
 *
 * Инициализатор утилит ANRWatchDog, StackTraceLogger.
 */
class AnrTimberInitializer(
    private val enableCrashlytics: Boolean
) : () -> Unit {

    override fun invoke() {
        initTimber()
        initANRWatchDog()
    }

    private fun initANRWatchDog() {
        val anrListener = if (enableCrashlytics) {
            ANRWatchDog.ANRListener {
                FirebaseCrashlytics.getInstance()
                    .recordException(it)
            }
        } else {
            ANRWatchDog.ANRListener { Timber.e(it) }
        }
        ANRWatchDog()
            .setReportMainThreadOnly()
            .setANRListener(anrListener)
            .start()
    }

    private fun initTimber() {
        Timber.plant(StackTraceLogger())
    }
}