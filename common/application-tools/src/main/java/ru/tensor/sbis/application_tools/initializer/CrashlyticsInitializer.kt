package ru.tensor.sbis.application_tools.initializer

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.tensor.sbis.application_tools.CrashlyticsTree
import ru.tensor.sbis.common.util.SESSION_ID
import timber.log.Timber

/**
 * @author du.bykov
 *
 * Инициализатор утилиты FirebaseCrashlytics.
 */
class CrashlyticsInitializer : () -> Unit {

    override fun invoke() {
        FirebaseCrashlytics.getInstance().setCustomKey(
            CRASHLYTICS_KEY_SESSION,
            SESSION_ID.toString()
        )
        Timber.plant(CrashlyticsTree())
    }
}

private const val CRASHLYTICS_KEY_SESSION = "session"
