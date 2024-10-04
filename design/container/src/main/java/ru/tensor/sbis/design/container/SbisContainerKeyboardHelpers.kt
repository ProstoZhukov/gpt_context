package ru.tensor.sbis.design.container

import android.app.Activity
import android.view.View
import ru.tensor.sbis.common.util.AdjustResizeHelper.AdjustResizeHelperHost
import kotlin.properties.ReadOnlyProperty

/**
 * Метод для создания класса отслеживающего открытие/закрытие клавиатуры [AdjustResizeHelperHost].
 *
 * @author ps.smirnyh
 */
internal inline fun keyboardHelper(crossinline openAction: (Int) -> Unit, crossinline closeAction: (Int) -> Unit) =
    ReadOnlyProperty<SbisContainerImpl, AdjustResizeHelperHost> { thisRef, _ ->
        object : AdjustResizeHelperHost {
            override fun onKeyboardOpenMeasure(keyboardHeight: Int) {
                openAction(keyboardHeight)
            }

            override fun onKeyboardCloseMeasure(keyboardHeight: Int) {
                closeAction(keyboardHeight)
            }

            override fun getActivity(): Activity {
                return thisRef.requireActivity()
            }

            override fun getContentView(): View {
                return thisRef.requireView()
            }
        }
    }