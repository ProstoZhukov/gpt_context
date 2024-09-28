package ru.tensor.sbis.design.util

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat

/** @SelfDocumented */
val Window.isFullscreen: Boolean
    get() = (attributes.flags and FLAG_FULLSCREEN) != 0

/** @SelfDocumented */
fun Window.showStatusBar(shouldShow: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        showStatusBarImpl30(shouldShow)
    } else {
        showStatusBarImplPre30(shouldShow)
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private fun Window.showStatusBarImpl30(shouldShow: Boolean) {
    if (shouldShow) {
        insetsController?.show(WindowInsets.Type.statusBars())
    } else {
        insetsController?.hide(WindowInsets.Type.statusBars())
    }
}

/**
 * Для API < 30 [WindowInsetsControllerCompat] использует метод [View.setSystemUiVisibility], что плохо работает
 * с диалоговыми окнами: при показе диалога статус-бар сначала появляется, потом исчезает, а после закрытия диалога
 * появляется снова, несмотря на то, что Activity ранее его скрывала.
 * Поэтому используется [FLAG_FULLSCREEN] в [WindowManager.LayoutParams].
 */
private fun Window.showStatusBarImplPre30(shouldShow: Boolean) {
    if (shouldShow) {
        clearFlags(FLAG_FULLSCREEN)
    } else {
        setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
    }
}