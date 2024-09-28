package ru.tensor.sbis.application_tools.initializer

import android.app.Application
import com.google.firebase.FirebaseApp
import io.reactivex.exceptions.CompositeException
import io.reactivex.plugins.RxJavaPlugins
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashAnalytics
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashAnalyticsHelper
import ru.tensor.sbis.application_tools.logcrashesinfo.utils.CrashReporterViaNotification
import timber.log.Timber

/**
 * @author du.bykov
 *
 * Инициализатор утилит CrashAnalytics и RxJavaPlugins.
 */
class CrashAnalyticsInitializer(
    private val application: Application,
    private val enableLocalDebugUtils: Boolean,
    private val stopApplicationOnRxCrash: Boolean
) : () -> Unit {

    override fun invoke() {
        initRxJavaDebug()
        FirebaseApp.initializeApp(application)
        initCrashAnalytics()
    }

    private fun initCrashAnalytics() {
        CrashAnalytics.init(
            application,
            CrashAnalyticsHelper(application),
            CrashReporterViaNotification(application),
            enableLocalDebugUtils
        )
    }

    private fun initRxJavaDebug() {
        if (!stopApplicationOnRxCrash)
            initRxErrorHandling()
    }

    private fun initRxErrorHandling() {
        RxJavaPlugins.setErrorHandler { throwable ->
            getExceptions(throwable).forEach {
                Timber.e(it)
            }
        }
    }

    private fun getExceptions(throwable: Throwable): List<Throwable> {
        return (throwable as? CompositeException)?.exceptions
            ?: listOf(throwable)
    }
}