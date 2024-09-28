package ru.tensor.sbis.business.common.ui.fragment

import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.common.util.AdjustResizeHelper.KeyboardEventListener
import ru.tensor.sbis.verification_decl.summary_screen.ResetToSummaryScreen
import timber.log.Timber

/**
 * Метод-расширение для применения полноэкранный режим системного UI
 * @return true если опции системных флагов были применены
 */
fun Fragment.toFullscreen(): Boolean {
    activity?.let {
        it.window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        return true
    }
    return false
}

/**
 * Метод-расширение для безопасной инъекции во фрагмент
 * Предотвращает падение тестов
 */
fun Fragment.safeInject() = try {
    AndroidSupportInjection.inject(this)
} catch (e: Exception) {
    Timber.e("Running for tests: $e")
}

/**
 * Метод-расширение для безопасной инъекции во фрагмент
 * Предотвращает падение тестов
 */
fun Fragment.safeInject(inject: () -> Unit) = try {
    inject.invoke()
} catch (e: Exception) {
    Timber.e("Running for tests: $e")
}

// region keyboard show/hide
/**
 * Обработка открытия клавиатуры. Обработка делегируется вложенным фрагментам.
 * Вызывать в методе [KeyboardEventListener.onKeyboardOpenMeasure]
 *
 * @param keyboardHeight высота клавиатуры
 * @return true если событие было обработано
 */
fun Fragment.handleKeyboardOpenMeasure(keyboardHeight: Int): Boolean =
    runOnKeyboardListenerFragment {
        onKeyboardOpenMeasure(keyboardHeight)
    }

/**
 * Обработка закрытия клавиатуры. Обработка делегируется вложенным фрагментам.
 * Вызывать в методе [KeyboardEventListener.onKeyboardCloseMeasure]
 *
 * @param keyboardHeight высота клавиатуры
 * @return true если событие было обработано
 */
fun Fragment.handleKeyboardCloseMeasure(keyboardHeight: Int): Boolean =
    runOnKeyboardListenerFragment {
        onKeyboardCloseMeasure(keyboardHeight)
    }

/**
 * Запустить функцию на вложенном фрагменте реализующем [KeyboardEventListener]
 *
 * @return true если событие было обработано
 */
private fun Fragment.runOnKeyboardListenerFragment(action: KeyboardEventListener.() -> Boolean): Boolean {
    @IdRes val id = R.id.container
    if (!isAdded) return false
    val lastFragment = childFragmentManager.findFragmentById(id)
    if (lastFragment is KeyboardEventListener) {
        return lastFragment.action()
    }
    return false
}
// endregion keyboard show/hide

/**
 * Сброс на разводящую раздела.
 */
fun Fragment.resetToSummaryScreen() {
    parentFragmentManager.fragments.let {
        for (fragment in it) {
            if (fragment is ResetToSummaryScreen) {
                fragment.resetToSummaryScreen()
            }
            fragment.parentFragment?.resetToSummaryScreen()
        }
    }
}
