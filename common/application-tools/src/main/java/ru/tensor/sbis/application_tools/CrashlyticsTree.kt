package ru.tensor.sbis.application_tools

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * @author du.bykov
 *
 * Логирование краша в консоль.
 */
class CrashlyticsTree : Timber.Tree() {

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        if (priority != Log.ERROR && priority != Log.WARN) return

        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCustomKey(
            CRASHLYTICS_KEY_PRIORITY,
            priority
        )
        tag?.let {
            crashlytics.setCustomKey(
                CRASHLYTICS_KEY_TAG,
                it
            )
        }

        crashlytics.setCustomKey(
            CRASHLYTICS_KEY_MESSAGE,
            message
        )

        if (throwable == null) {
            crashlytics.log(message)
        } else {
            crashlytics.recordException(throwable)
        }
    }
}

private const val CRASHLYTICS_KEY_PRIORITY = "priority"
private const val CRASHLYTICS_KEY_TAG = "tag"
private const val CRASHLYTICS_KEY_MESSAGE = "message"