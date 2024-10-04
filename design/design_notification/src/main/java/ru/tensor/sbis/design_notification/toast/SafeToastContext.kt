package ru.tensor.sbis.design_notification.toast

import android.content.Context
import android.content.ContextWrapper
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import ru.tensor.sbis.design_notification.toast.SafeToastContext.WindowManagerWrapper

/**
 * Позволяет использовать [WindowManagerWrapper] в качестве [Context.WINDOW_SERVICE] для обработки
 * исключения [WindowManager.BadTokenException] при добавлении View к окну.
 *
 * @author us.bessonov
 */
internal class SafeToastContext(base: Context) : ContextWrapper(base) {

    override fun getApplicationContext(): Context = ApplicationContextWrapper(baseContext.applicationContext)

    private class ApplicationContextWrapper(base: Context) : ContextWrapper(base) {
        override fun getSystemService(name: String): Any = if (WINDOW_SERVICE == name) {
            WindowManagerWrapper(baseContext.getSystemService(name) as WindowManager)
        } else {
            super.getSystemService(name)
        }
    }

    private class WindowManagerWrapper(private val base: WindowManager) : WindowManager {

        @Deprecated("Use Context.getDisplay() instead")
        override fun getDefaultDisplay(): Display {
            return base.defaultDisplay
        }

        override fun removeViewImmediate(view: View) {
            base.removeViewImmediate(view)
        }

        override fun addView(view: View, params: ViewGroup.LayoutParams) {
            try {
                base.addView(view, params)
            } catch (ignored: Throwable) {
            }
        }

        override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
            base.updateViewLayout(view, params)
        }

        override fun removeView(view: View) {
            base.removeView(view)
        }
    }
}