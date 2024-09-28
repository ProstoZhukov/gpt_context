package ru.tensor.sbis.list.view.utils

import android.view.View
import androidx.annotation.CheckResult

/**
 * Фабрика обработчиков нажатий на элементы с паузами между нажатиями.
 *
 * @author du.bykov
 */
internal class ItemClickListenerFactory(
    internal var interval: Long = ITEM_CLICK_INTERVAL,
    private val getCurrentTime: () -> Long = System::currentTimeMillis,
    var shouldThrottleItemClicksSeparately: Boolean = false
) {

    private var globalLastClickTime = 0L

    @CheckResult
    fun createClickListener(clickAction: () -> Unit) = object : View.OnClickListener {

        private var lastClickTime = 0L

        override fun onClick(v: View?) {
            if (interval == 0L) {
                clickAction()
                return
            }
            if (!shouldThrottleItemClicksSeparately) lastClickTime = globalLastClickTime
            val clickTime = getCurrentTime()
            if (clickTime - lastClickTime >= interval) {
                lastClickTime = clickTime
                globalLastClickTime = clickTime
                clickAction()
            }
        }
    }
}

/**
 * Интервал по умолчанию между нажатиями на элементы списка
 */
internal const val ITEM_CLICK_INTERVAL = 800L