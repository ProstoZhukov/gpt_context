package ru.tensor.sbis.application_tools.initializer

import android.os.Build
import android.os.StrictMode
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.concurrent.Executors

/**
 * @author as.chadov
 *
 * Инициализатор утилит StrictMode.
 * Подробнее см. https://developer.android.com/reference/android/os/StrictMode
 *
 * @param enableCrashlytics true если отправлять отчет в Crashlytics.
 */
class StrictModeInitializer(
    private val enableCrashlytics: Boolean = false
) : () -> Unit {

    private var penaltyListener: StrictModeListener? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            penaltyListener = StrictModeListener(enableCrashlytics)
        }
    }

    override fun invoke() {
        StrictMode.setThreadPolicy(buildThreadPolicy())
        StrictMode.setVmPolicy(buildVmPolicy())
    }

    /** Политика StrictMode применяемая к потоку где была установлена (здесь главному потоку). */
    private fun buildThreadPolicy(): StrictMode.ThreadPolicy =
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            // для детекции вызвать StrictMode.noteSlowCall("method_name")
            .detectCustomSlowCalls()
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) detectUnbufferedIo()
            }
            .penaltyLog()
            .apply {
                penaltyListener?.setPenaltyListener(this)
            }
            .build()

    /** Политика StrictMode применяемая к процессу VM т.е. всем потокам приложения. */
    private fun buildVmPolicy(): StrictMode.VmPolicy =
        StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .apply {
                penaltyListener?.setPenaltyListener(this)
            }
            .build()
}

// Во избежание java.lang.NoClassDefFoundError: Failed resolution of: Landroid/os/StrictMode$OnThreadViolationListener
private class StrictModeListener(private val enableCrashlytics: Boolean) {
    fun setPenaltyListener(builder: StrictMode.VmPolicy.Builder) = builder.apply {
        if (enableCrashlytics && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            penaltyListener(
                Executors.newSingleThreadExecutor()
            ) { FirebaseCrashlytics.getInstance().recordException(it) }
        }
    }

    fun setPenaltyListener(builder: StrictMode.ThreadPolicy.Builder) = builder.apply {
        if (enableCrashlytics && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            penaltyListener(
                Executors.newSingleThreadExecutor()
            ) { FirebaseCrashlytics.getInstance().recordException(it) }
        }
    }
}