@file:JvmName("KeyboardAwareExtension")
@file:Suppress("unused")

package ru.tensor.sbis.base_components.keyboard

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.AdjustResizeHelper

/**
 * Маркерный интерфейс,
 * позволяющий не только проинформировать об возможности отслеживать клавиатуру,
 * но и предоставить методы для более удобного использования в [Activity] и [Fragment].
 *
 * @author kv.martyshenko
 */
interface KeyboardAware

// region KeyboardDetector Factories
/**
 * Функция, позволяющая создать [KeyboardDetector] из [Activity].
 *
 * @param rootView корневая [View].
 * @param delegate ответственный за обработку скрытия/появления.
 * @param heightRecalculate функция для пересчета высоты. Если отсутствует, то высота прокидывается без модификации.
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@CheckResult
fun <T> T.keyboardDetector(
    rootView: View,
    delegate: KeyboardDetector.Delegate,
    heightRecalculate: ((Int) -> Int)? = null
): KeyboardDetector where T: Activity, T: KeyboardAware {
    return keyboardDetector({ rootView }, delegate, heightRecalculate)
}

/**
 * Функция, позволяющая создать [KeyboardDetector] из [Activity].
 *
 * @param rootViewProvider поставщик корневой [View].
 * @param delegate ответственный за обработку скрытия/появления.
 * @param heightRecalculate функция для пересчета высоты. Если отсутствует, то высота прокидывается без модификации.
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@JvmName("keyboardDetectorFromViewProvider")
@CheckResult
fun <T> T.keyboardDetector(
    rootViewProvider: () -> View,
    delegate: KeyboardDetector.Delegate,
    heightRecalculate: ((Int) -> Int)? = null
): KeyboardDetector where T: Activity, T: KeyboardAware {
    return KeyboardDetector(this, rootViewProvider, delegate, heightRecalculate)
}

/**
 * Функция, позволяющая создать [KeyboardDetector] из [Fragment].
 *
 * @param rootView корневая [View].
 * @param delegate ответственный за обработку скрытия/появления.
 * @param heightRecalculate функция для пересчета высоты. Если отсутствует, то высота прокидывается без модификации.
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@CheckResult
fun <T> T.keyboardDetector(
    rootView: View,
    delegate: KeyboardDetector.Delegate,
    heightRecalculate: ((Int) -> Int)? = null
): KeyboardDetector where T: Fragment {
    return keyboardDetector({ rootView }, delegate, heightRecalculate)
}

/**
 * Функция, позволяющая создать [KeyboardDetector] из [Fragment].
 *
 * @param rootViewProvider поставщик корневой [View].
 * @param delegate ответственный за обработку скрытия/появления.
 * @param heightRecalculate функция для пересчета высоты. Если отсутствует, то высота прокидывается без модификации.
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@CheckResult
fun <T> T.keyboardDetector(
    rootViewProvider: () -> View,
    delegate: KeyboardDetector.Delegate,
    heightRecalculate: ((Int) -> Int)? = null
): KeyboardDetector where T: Fragment {
    return KeyboardDetector(requireActivity(), rootViewProvider, delegate, heightRecalculate)
}
// endregion

// region Delegate Factories
/**
 * Функция, позволяющая создать [KeyboardDetector.Delegate], изменяющий высоту указанной [View].
 *
 * @param view целевая [View].
 * @param expand функция увеличивающая высоту.
 * @param collapse функция уменьшающая высоту.
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@CheckResult
fun <T: KeyboardAware, V: View> T.createViewHeightResizer(
    view: V,
    expand: (view: V, height: Int) -> Unit = { v, _ -> v.changeHeight(ViewGroup.LayoutParams.MATCH_PARENT) },
    collapse: (view: V, height: Int) -> Unit = { v, h -> v.changeHeight(h) }
): KeyboardDetector.Delegate {
    return createViewHeightResizer(viewProvider = { view }, expand, collapse)
}

/**
 * Функция, позволяющая создать [KeyboardDetector.Delegate], изменяющий высоту указанной [View].
 *
 * @param viewProvider поставщик [View].
 * @param expand функция увеличивающая высоту.
 * @param collapse функция уменьшающая высоту.
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@JvmName("createViewHeightResizerFromViewProvider")
@CheckResult
fun <T: KeyboardAware, V: View> T.createViewHeightResizer(
    viewProvider: () -> V,
    expand: (view: V, height: Int) -> Unit = { v, _ -> v.changeHeight(ViewGroup.LayoutParams.MATCH_PARENT) },
    collapse: (view: V, height: Int) -> Unit = { v, h -> v.changeHeight(h) }
): KeyboardDetector.Delegate {
    return ViewHeightResizer(viewProvider, expand, collapse)
}

/**
 * Функция, позволяющая создать [KeyboardDetector.Delegate] из [FragmentActivity], оповещающий дочерний фрагмент,
 * наследуемый от [AdjustResizeHelper.KeyboardEventListener].
 *
 * @param containerId идентификатор контейнера.
 * @param fallbackDelegate делегат, которому будет перенаправлен вызов,
 * если в контейнере нет фрагмента или если фрагмент не соответствует [AdjustResizeHelper.KeyboardEventListener].
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@CheckResult
fun <T> T.createDispatcherToNestedFragment(
    @IdRes containerId: Int,
    fallbackDelegate: KeyboardDetector.Delegate? = null
): KeyboardDetector.Delegate where T: FragmentActivity, T: KeyboardAware {
    return createDispatcherToNestedFragment(supportFragmentManager, containerId, fallbackDelegate = fallbackDelegate)
}

/**
 * Функция, позволяющая создать [KeyboardDetector.Delegate] из [Fragment], оповещающий дочерний фрагмент,
 * наследуемый от [AdjustResizeHelper.KeyboardEventListener].
 *
 * @param containerId идентификатор контейнера.
 * @param fallbackDelegate делегат, которому будет перенаправлен вызов,
 * если в контейнере нет фрагмента или если фрагмент не соответствует [AdjustResizeHelper.KeyboardEventListener].
 *
 * @author kv.martyshenko
 */
@JvmOverloads
@CheckResult
fun <T> T.createDispatcherToNestedFragment(
    @IdRes containerId: Int,
    fallbackDelegate: KeyboardDetector.Delegate? = null
): KeyboardDetector.Delegate where T: Fragment, T: KeyboardAware {
    return createDispatcherToNestedFragment(childFragmentManager, containerId, fallbackDelegate = fallbackDelegate)
}

/**
 * Функция, позволяющая создать [KeyboardDetector.Delegate] из [FragmentActivity], оповещающий дочерний фрагмент,
 * наследуемый от [AdjustResizeHelper.KeyboardEventListener].
 *
 * @param fragmentProvider поставщик дочернего фрагмента по спец. условию.
 * @param fallbackDelegate делегат, которому будет перенаправлен вызов,
 * если в контейнере нет фрагмента или если фрагмент не соответствует [AdjustResizeHelper.KeyboardEventListener].
 *
 * @author as.chadov
 */
@JvmOverloads
@CheckResult
fun <T> T.createDispatcherToNestedFragment(
    fragmentProvider: (FragmentManager) -> Fragment?,
    fallbackDelegate: KeyboardDetector.Delegate? = null
): KeyboardDetector.Delegate where T : FragmentActivity, T : KeyboardAware =
    createDispatcherToNestedFragment(supportFragmentManager, 0, fragmentProvider, fallbackDelegate)

/**
 * Функция, позволяющая создать [KeyboardDetector.Delegate],
 * оповещающий [primaryDelegate] и, если тот не обработал, перенаправить в [fallbackDelegate].
 *
 * @param primaryDelegate основной делегат.
 * @param fallbackDelegate запасной делегат.
 *
 * @author kv.martyshenko
 */
@CheckResult
fun <T: KeyboardAware> T.createDelegateWithFallback(
    primaryDelegate: KeyboardDetector.Delegate,
    fallbackDelegate: KeyboardDetector.Delegate,
): KeyboardDetector.Delegate {
    return PrimaryOrFallbackDelegate(primaryDelegate, fallbackDelegate)
}

/**
 * Функция, позволяющая создать мульти [KeyboardDetector.Delegate],
 * оповещающий всех вложенных.
 *
 * @param delegates вложенные делегаты.
 *
 * @author kv.martyshenko
 */
@CheckResult
fun <T: KeyboardAware> T.createCompoundDelegate(
    delegates: List<KeyboardDetector.Delegate>
): KeyboardDetector.Delegate {
    return CompoundDelegate(delegates)
}
// endregion

private fun createDispatcherToNestedFragment(
    fragmentManager: FragmentManager,
    @IdRes containerId: Int,
    fragmentProvider: ((FragmentManager) -> Fragment?)? = null,
    fallbackDelegate: KeyboardDetector.Delegate? = null
): KeyboardDetector.Delegate {
    val fragmentDelegate = if (fragmentProvider == null) {
        DispatcherToNestedFragment(fragmentManager, containerId)
    } else {
        DispatcherToProvidedFragment(fragmentManager, fragmentProvider)
    }
    return fallbackDelegate?.let { PrimaryOrFallbackDelegate(fragmentDelegate, it) } ?: fragmentDelegate
}

private fun View.changeHeight(height: Int) {
    val lParams = this.layoutParams
    if (lParams.height != height) {
        lParams.height = height
        layoutParams = lParams
    }
}