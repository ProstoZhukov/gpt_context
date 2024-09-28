package ru.tensor.sbis.base_components.keyboard

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.AdjustResizeHelper

/**
 * Делегат, позволяющий изменять высоту указанной [View] при появлении/скрытии клавиатуры.
 *
 * @param viewProvider поставщик [View]
 * @param expand функция увеличивающая высоту [View]
 * @param collapse функция уменьшающая высоту [View]
 *
 * @author kv.martyshenko
 */
internal class ViewHeightResizer<V: View>(
    private val viewProvider: () -> V,
    private val expand: (view: V, height: Int) -> Unit,
    private val collapse: (view: V, height: Int) -> Unit
) : KeyboardDetector.Delegate {

    private var previousKeyboardHeight: Int = 0

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        val view = viewProvider()
        val height: Int = view.height - (keyboardHeight - previousKeyboardHeight)
        collapse(viewProvider(), height)
        previousKeyboardHeight = keyboardHeight
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        expand(viewProvider(), keyboardHeight)
        previousKeyboardHeight = 0
        return true
    }

}

/**
 * Делегат, передающий события появлении/скрытии клавиатуры дочернему фрагменту.
 * Дочерний фрагмент будет найден по идентификатору контейнера [containerId]
 *
 * @param fragmentManager
 * @param containerId
 *
 * @author kv.martyshenko
 */
internal class DispatcherToNestedFragment(
    private val fragmentManager: FragmentManager,
    @IdRes private val containerId: Int
) : DispatcherToProvidedFragment(
    fragmentManager, { fragmentManager.findFragmentById(containerId) }
)

/**
 * Делегат, передающий события появлении/скрытии клавиатуры дочернему фрагменту.
 * Дочерний фрагмент будет найден через колбек-поставщик [fragmentProvider]
 *
 * @param fragmentManager
 * @param fragmentProvider колбек вызывающийся на [fragmentManager] для поиска дочернего фрагмента по спец. условию
 *
 * @author as.chadov
 */
internal open class DispatcherToProvidedFragment(
    private val fragmentManager: FragmentManager,
    private val fragmentProvider: (FragmentManager) -> Fragment?
) : KeyboardDetector.Delegate {

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean =
        fragmentManager.dispatchKeyboardEventToFragment(
            fragmentProvider,
            keyboardHeight,
            AdjustResizeHelper.KeyboardEventListener::onKeyboardOpenMeasure
        )

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean =
        fragmentManager.dispatchKeyboardEventToFragment(
            fragmentProvider,
            keyboardHeight,
            AdjustResizeHelper.KeyboardEventListener::onKeyboardCloseMeasure
        )

    private inline fun FragmentManager.dispatchKeyboardEventToFragment(
        findFragment: (FragmentManager) -> Fragment?,
        keyboardHeight: Int,
        action: AdjustResizeHelper.KeyboardEventListener.(Int) -> Boolean
    ): Boolean = findFragment(this)
        ?.let { it as? AdjustResizeHelper.KeyboardEventListener }
        ?.let { it.action(keyboardHeight) }
        ?: false
}

/**
 * Делегат, доставляющий события запасному, если основной не смог обработать появление/скрытие клавиатуры.
 *
 * @param primaryDelegate основной
 * @param fallbackDelegate запасной
 *
 * @author kv.martyshenko
 */
internal class PrimaryOrFallbackDelegate(
    private val primaryDelegate: KeyboardDetector.Delegate,
    private val fallbackDelegate: KeyboardDetector.Delegate
) : KeyboardDetector.Delegate {

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        return dispatchKeyboardEvent(keyboardHeight, KeyboardDetector.Delegate::onKeyboardOpenMeasure)
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        return dispatchKeyboardEvent(keyboardHeight, KeyboardDetector.Delegate::onKeyboardCloseMeasure)
    }

    private inline fun dispatchKeyboardEvent(
        keyboardHeight: Int,
        action: KeyboardDetector.Delegate.(Int) -> Boolean
    ): Boolean {
        return if(primaryDelegate.action(keyboardHeight)) true else fallbackDelegate.action(keyboardHeight)
    }

}

/**
 * Делегат, агрегирующий несколько делегатов.
 *
 * @param delegates
 *
 * @author kv.martyshenko
 */
internal class CompoundDelegate(
    val delegates: List<KeyboardDetector.Delegate>
) : KeyboardDetector.Delegate {

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        delegates.forEach { it.onKeyboardOpenMeasure(keyboardHeight) }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        delegates.forEach { it.onKeyboardCloseMeasure(keyboardHeight) }
        return true
    }

}