package ru.tensor.sbis.common.util

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Добавляет в [Bundle] метку о необходимости навигации на фрагменте.
 */
fun addNavigationArg(bundle: Bundle, needNavigation: Boolean): Bundle {
    bundle.putBoolean(ARG_NEED_FRAGMENT_NAVIGATION, needNavigation)
    return bundle
}

fun addShouldHideToolbarArg(bundle: Bundle, shouldHideToolbar: Boolean): Bundle {
    bundle.putBoolean(ARG_SHOULD_HIDE_TOOLBAR, shouldHideToolbar)
    return bundle
}

/**
 * Добавляет во [Fragment] метку о необходимости навигации.
 */
fun <T : Fragment> addNavigationArg(fragment: T, needNavigation: Boolean): T {
    fragment.arguments = (fragment.arguments ?: Bundle()).apply {
        putBoolean(ARG_NEED_FRAGMENT_NAVIGATION, needNavigation)
    }
    return fragment
}

/**
 * Произведет переданное действие, если навигация на фрагменте отключена.
 */
fun doIfNavigationDisabled(fragment: Fragment, action: () -> Unit) {
    if (isNavigationEnabled(fragment).not()) action()
}

/**
 * Включена ли навигация на фрагменте.
 */
fun isNavigationEnabled(fragment: Fragment) = fragment.requireArguments().getBoolean(ARG_NEED_FRAGMENT_NAVIGATION, true)

/**
 * Требуется ли скрыть Toolbar на фрагменте.
 */
fun shouldHideToolbar(fragment: Fragment) = fragment.requireArguments().getBoolean(ARG_SHOULD_HIDE_TOOLBAR, false)

private const val ARG_NEED_FRAGMENT_NAVIGATION = "ARG_NEED_FRAGMENT_NAVIGATION"
private const val ARG_SHOULD_HIDE_TOOLBAR = "ARG_SHOULD_HIDE_TOOLBAR"