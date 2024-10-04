/**
 * Расширения для отображения `Toast`.
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design_notification.toast

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design_notification.SbisPopupNotification

/**
 * Отображает [Toast] с текстом, определённом в строковом ресурсе.
 */
fun Fragment.showToast(@StringRes message: Int, duration: Int = Toast.LENGTH_LONG): Toast? =
    context?.let { SbisPopupNotification.pushToast(it, message, duration) }

/**
 * Отображает [Toast] с указанным текстом.
 */
fun Fragment.showToast(message: CharSequence, duration: Int = Toast.LENGTH_LONG): Toast? =
    context?.let { SbisPopupNotification.pushToast(it, message, duration) }
