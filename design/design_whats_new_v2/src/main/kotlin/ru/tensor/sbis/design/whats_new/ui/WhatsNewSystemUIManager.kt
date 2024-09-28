package ru.tensor.sbis.design.whats_new.ui

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.findParentFragment
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.utils.extentions.requestApplyInsetsWhenAttached

/**
 * Класс для управления полноэкранным режимом и режим отображения статус бара.
 *
 * @author ps.smirnyh
 */
internal class WhatsNewSystemUIManager {

    private var parentStatusBarColor: Int? = null

    /** Включить прозрачный статус бар или полноэкранный режим. */
    fun init(fragment: Fragment) {
        if (DeviceConfigurationUtils.isTablet(fragment.requireContext())) {
            fragment.findParentFragment<DialogFragment>()?.let(::setFullScreen)
        } else {
            initStatusBar(fragment)
        }
    }

    /** Вернуть изначальное значение цвета статус бара. */
    fun reset(fragment: Fragment) {
        parentStatusBarColor?.let {
            fragment.requireActivity().window?.statusBarColor = it
            setContrastStatusBarColorIcon(fragment.requireActivity().window, it)
        }
    }

    /*
    Используем deprecated методы потому что есть проблемы начиная с 30 api,
    что если через windowInsetController выставляем светлый режим статус бара,
    то меняется цвет кнопок навигации на светлый.
    https://dev.sbis.ru/opendoc.html?guid=f18cc0fd-694d-4bba-9bcc-b4371bc43bed&client=3
     */
    @Suppress("DEPRECATION")
    private fun setContrastStatusBarColorIcon(window: Window, color: Int) {
        val isLight = ColorUtils.calculateLuminance(color) >= 0.5
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            with(window.decorView) {
                systemUiVisibility = if (isLight) {
                    systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }
    }

    private fun setFullScreen(fragment: DialogFragment) = with(fragment) {
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    dialog?.window?.let {
                        WindowCompat.setDecorFitsSystemWindows(it, false)
                        ViewCompat.setOnApplyWindowInsetsListener(it.decorView) { v, insets ->
                            val b = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                            v.updatePadding(bottom = b.bottom)
                            WindowInsetsCompat.CONSUMED
                        }
                        it.decorView.requestApplyInsetsWhenAttached()
                    }
                    lifecycle.removeObserver(this)
                }
            }
        })
    }

    private fun initStatusBar(fragment: Fragment) {
        fragment.requireActivity().window?.let {
            parentStatusBarColor = it.statusBarColor
            it.statusBarColor = (fragment.requireView().background as? ColorDrawable)?.color
                ?: BackgroundColor.DEFAULT.getValue(fragment.requireContext())
            setContrastStatusBarColorIcon(it, it.statusBarColor)
        }
    }
}