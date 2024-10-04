package ru.tensor.sbis.design_notification.toast

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast

/**
 * [Toast], использующий [SafeToastContext] для перехвата исключения
 * [android.view.WindowManager.BadTokenException], которое может возникать на API 25.
 *
 * @see [https://github.com/PureWriter/ToastCompat](Оригинальный репозиторий решения)
 */
internal class ToastCompat private constructor(context: Context, private val toast: Toast) : Toast(context) {

    @Deprecated("Custom toast views are deprecated")
    override fun setView(view: View) {
        toast.view = view
        setContextCompat(view, SafeToastContext(view.context))
    }

    override fun show() = toast.show()

    override fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) = toast.setGravity(gravity, xOffset, yOffset)

    override fun setMargin(horizontalMargin: Float, verticalMargin: Float) =
        toast.setMargin(horizontalMargin, verticalMargin)

    override fun setText(resId: Int) = toast.setText(resId)

    override fun setText(s: CharSequence) = toast.setText(s)

    override fun getHorizontalMargin(): Float = toast.horizontalMargin

    override fun getVerticalMargin() = toast.verticalMargin

    override fun getDuration() = toast.duration

    override fun setDuration(duration: Int) {
        toast.duration = duration
    }

    override fun getGravity(): Int = toast.gravity

    override fun getXOffset(): Int = toast.xOffset

    override fun getYOffset(): Int = toast.yOffset

    @Deprecated("Custom toast views are deprecated")
    override fun getView(): View? = toast.view

    companion object {
        /**
         * Отображает стандартный [Toast] без риска возникновения BadTokenException на API 25
         */
        fun makeText(context: Context, text: CharSequence?, duration: Int): ToastCompat {
            // We cannot pass the SafeToastContext to Toast.makeText() because
            // the View will unwrap the base context and we are in vain.
            @SuppressLint("ShowToast") val toast = Toast.makeText(context, text, duration)
            setContextCompat(toast.view!!, SafeToastContext(context))
            return ToastCompat(context, toast)
        }

        @SuppressLint("DiscouragedPrivateApi")
        private fun setContextCompat(view: View, context: Context) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                try {
                    val field = View::class.java.getDeclaredField("mContext")
                    field.isAccessible = true
                    field[view] = context
                } catch (ignored: Throwable) {
                }
            }
        }
    }
}