package ru.tensor.sbis.common.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.BuildConfig
import timber.log.Timber

/**
 * @author sa.nikitin
 */

inline fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T =
    this.apply {
        arguments = Bundle().apply(argsBuilder)
    }

inline fun <T : Fragment> T.addArgs(argsBuilder: Bundle.() -> Unit): T =
    this.apply {
        arguments = (arguments ?: Bundle()).apply(argsBuilder)
    }

inline fun <reified T> Fragment.getParentFragmentAs(): T? {
    if (parentFragment is T) {
        return parentFragment as T
    }
    return null
}

inline fun <reified T> Fragment.getTargetFragmentAs(): T? {
    if (targetFragment is T) {
        return targetFragment as T
    }
    return null
}

inline fun <reified T> Fragment.getActivityAs(): T? {
    if (activity is T) {
        return activity as T
    }
    return null
}

inline fun <reified T> Fragment.getParentAs(): T? {
    return getParentFragmentAs<T>() ?: getActivityAs()
}

inline fun <reified T> Fragment.requireParentAs(): T {
    return getParentAs<T>()
        ?: throw IllegalStateException("Parent fragment or activity should implement ${T::class.java.canonicalName}.")
}

inline fun <reified T> Fragment.getTargetOrParentFragment(): T? {
    return getTargetFragmentAs<T>() ?: getParentFragmentAs<T>()
}

/**
 * Расширение для получения фрагмента из childFragmentManager типа T
 *
 * @param containerId - айди контенера, где искать фрагмент
 */
inline fun <reified T> Fragment.getChildFragmentAs(containerId: Int): T? = try {
    childFragmentManager.findFragmentById(containerId) as? T
} catch (e: IllegalStateException) {
    Timber.e(e)
    null
}

/**
 * Ищет по иерархии фрагментов родительский фрагмент типа [T] содержащий this фрагмент
 *
 * @return найденный родительский фрагмент или null
 */
inline fun <reified T> Fragment.findParentFragment(): T? {
    var parent = parentFragment
    while (parent !is T && parent != null) {
        parent = parent.parentFragment
    }
    return parent as? T
}

/* Перенесено из FragmentEx.kt */
inline fun <reified T> Fragment.getApplicationAs(): T? {
    val application = requireActivity().application
    if (BuildConfig.DEBUG) {
        return application as T
    }

    return if (application is T) {
        application
    } else {
        val className = T::class.java.canonicalName
        Timber.e("Попытка приведения Application класса к $className, которым он не является")
        null
    }
}