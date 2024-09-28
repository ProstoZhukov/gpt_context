package ru.tensor.sbis.design_notification

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import ru.tensor.sbis.design.text_span.util.asRoboto
import ru.tensor.sbis.design_notification.toast.ToastCompat
import java.lang.ref.WeakReference

private const val MIN_TOAST_SHOW_DURATION = 200

/**
 * Отображает [Toast]'ы, предотвращая их накапливание при слишком быстром поступлении.
 * Сообщение обновляется не позднее чем через [MIN_TOAST_SHOW_DURATION] мс после поступления. Из сообщений, попавших в
 * этот интервал после отображения предыдущего, показано будет только последнее.
 *
 * @author us.bessonov
 */
internal object ToastManager {

    private var toast: WeakReference<Toast>? = null
    private var nextToast: WeakReference<Toast>? = null
    private var lastMessage: CharSequence? = null
    private var lastShowTime = 0L
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Отображает [Toast] с указанным текстом
     */
    fun pushToast(context: Context, message: CharSequence, duration: Int = Toast.LENGTH_LONG): Toast {
        val showTime = System.currentTimeMillis()
        val next = context.createToast(message, duration)
        when {
            message == lastMessage -> {
                updateCurrentToast(next, message, lastShowTime)
            }
            showTime - lastShowTime >= MIN_TOAST_SHOW_DURATION && nextToast == null -> {
                updateCurrentToast(next, message, showTime)
            }
            else -> {
                if (nextToast == null) {
                    handler.postDelayed({
                        updateCurrentToast(nextToast?.get(), message, System.currentTimeMillis())
                        nextToast = null
                    }, showTime - lastShowTime)
                }
                nextToast = WeakReference(next)
            }
        }
        return next
    }

    private fun updateCurrentToast(newToast: Toast?, message: CharSequence, showTime: Long) {
        lastMessage = message
        lastShowTime = showTime
        toast?.get()?.cancel()
        toast = newToast?.let { WeakReference(it) }
        newToast?.show()
    }
}

private fun Context.createToast(message: CharSequence, duration: Int): Toast {
    return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
        ToastCompat.makeText(this.applicationContext, asRoboto(this.applicationContext, message), duration)
    } else {
        Toast.makeText(this.applicationContext, asRoboto(this.applicationContext, message), duration)
    }
}