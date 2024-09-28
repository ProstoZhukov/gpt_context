package ru.tensor.sbis.mvp_extensions.view_state

import android.os.Handler
import android.os.Looper

/**
 * Декоратор для выполнения переданного действия для вью
 * @author sa.nikitin
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ViewDecorator<VIEW> {

    /**
     * Вью на которой выполняется действие
     */
    var view: VIEW? = null

    /**
     * Главный поток
     */
    val uiHandler = Handler(Looper.getMainLooper())

    /**
     * Выполняет переданное аргументом действие
     *
     * @param action действие
     * @return true, если действие выполнено, иначе false
     */
    inline fun action(crossinline action: VIEW.() -> Unit): Boolean =
        with(view) {
            if (this != null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    uiHandler.post { action(this) }
                } else {
                    action(this)
                }
                true
            } else {
                false
            }
        }

    /**
     * Выполняет переданное аргументом действие. Метод для Java
     *
     * @param viewAction действие
     * @return true, если действие выполнено, иначе false
     */
    fun action(viewAction: ViewAction<VIEW>): Boolean = action { viewAction.perform(this) }
}