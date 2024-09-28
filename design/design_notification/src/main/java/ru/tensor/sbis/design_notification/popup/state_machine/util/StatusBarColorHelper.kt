package ru.tensor.sbis.design_notification.popup.state_machine.util

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.toolbar.util.StatusBarHelper
import ru.tensor.sbis.design.utils.extentions.getActivity
import timber.log.Timber

/**
 * Управляет стилем иконок статусбара при показе/скрытии панели-информера.
 *
 * @author us.bessonov
 */
internal class StatusBarColorHelper {

    private var savedLightStatusBarMode: Boolean? = null

    /** @SelfDocumented */
    fun onShowPopup(view: View) = try {
        val activity = view.getActivity()
        savedLightStatusBarMode = StatusBarHelper.isStatusBarLightMode(activity)
        val color = (view.background as? ColorDrawable)?.color ?: Color.WHITE
        updateStatusBarMode(!useDarkStatusBarMode(color), activity)
    } catch (e: IllegalArgumentException) {
        Timber.e(e)
    }

    /** @SelfDocumented */
    fun onPopupHidden(view: View) {
        savedLightStatusBarMode?.let {
            updateStatusBarMode(it, view.getActivity())
        }
    }

    private fun updateStatusBarMode(isLight: Boolean, activity: Activity) {
        if (isLight) {
            StatusBarHelper.setLightMode(activity)
        } else {
            StatusBarHelper.setDarkMode(activity)
        }
    }

    private fun useDarkStatusBarMode(@ColorInt background: Int) = ColorUtils.calculateLuminance(background) < 0.5
}
