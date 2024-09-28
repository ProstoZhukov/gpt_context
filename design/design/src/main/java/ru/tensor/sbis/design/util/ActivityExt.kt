package ru.tensor.sbis.design.util

import android.app.Activity
import android.os.Build
import ru.tensor.sbis.design.R

/**
 * Признак необходимости показывать статус-бар.
 */
val Activity.shouldShowStatusBar: Boolean
    get() = when {
        window.isFullscreen -> false
        resources.getBoolean(R.bool.is_tablet) -> true
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode -> true
        else -> !resources.getBoolean(R.bool.is_landscape)
    }

/**
 * Проверяет, нужно ли показать статус-бар.
 * @see shouldShowStatusBar
 */
fun Activity.checkStatusBarShouldBeShown() {
    window.showStatusBar(shouldShowStatusBar)
}