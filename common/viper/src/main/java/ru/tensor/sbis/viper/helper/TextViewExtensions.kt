package ru.tensor.sbis.viper.helper

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Расширение для TextView, позволяющее адаптировать размер текста с учетом заданного числа строк в режиме выполнения.
 *
 * @param maxLines максимальное число строк для данного TextView
 * @param minTextSize минимальный доступный размер шрифта
 * @param lifecycle состояние данного TextView, передается для того, чтобы избежать утечек памяти при использовании ViewTreeObserver
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun TextView.adaptTextSize(maxLines: Int, minTextSize: Float = this.textSize, lifecycle: Lifecycle) {
    gravity = Gravity.CENTER_VERTICAL
    setLines(maxLines)

    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            layout?.run {
                if (lineCount > 1)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, minTextSize)
            }
            val viewTreeObserver = viewTreeObserver
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }
    val treeObserver = viewTreeObserver
    treeObserver.addOnGlobalLayoutListener(listener)

    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun connectListener() {
            if (treeObserver.isAlive) {
                treeObserver.removeOnGlobalLayoutListener(listener)
            }
            lifecycle.removeObserver(this)
        }
    })

}