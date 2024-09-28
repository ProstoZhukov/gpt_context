package ru.tensor.sbis.base_components.util

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.user_activity_track.activity.UserActivityTrackable
import ru.tensor.sbis.verification_decl.auth.AuthAware
import ru.tensor.sbis.verification_decl.auth.AuthAware.CheckAuthStrategy
import ru.tensor.sbis.verification_decl.auth.AuthAware.CheckAuthStrategy.CheckWithForceJumpToLogin

/**
 * Утилиты, выделенные из BaseActivity.
 */
fun ComponentActivity.updateFullScreen() {
    if (!DeviceConfigurationUtils.isTablet(this) && DeviceConfigurationUtils.isLandscape(this) &&
        (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !isInMultiWindowMode)
    ) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

/** @SelfDocumented */
fun ComponentActivity.disableAutofillServiceApi29() {
    // На основании ошибки https://dev.sbis.ru/opendoc.html?guid=1d050450-341f-446c-971f-4360bf2d0968&client=3
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
        window.decorView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }
}

/** @SelfDocumented */
inline fun <reified T> trackableActivity(
    screenName: String = T::class.java.name
): UserActivityTrackable {
    return object : UserActivityTrackable {
        override val isTrackActivityEnabled: Boolean = true

        override val screenName: String = screenName
    }
}

/** @SelfDocumented */
fun authAware(
    strategy: CheckAuthStrategy = CheckWithForceJumpToLogin
): AuthAware {
    return object : AuthAware {
        override val checkAuthStrategy: CheckAuthStrategy = strategy
    }
}