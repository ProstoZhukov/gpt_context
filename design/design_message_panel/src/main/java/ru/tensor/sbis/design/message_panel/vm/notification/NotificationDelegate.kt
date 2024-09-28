package ru.tensor.sbis.design.message_panel.vm.notification

import androidx.annotation.StringRes

/**
 * Внутренний API для работы с уведомлениями панели ввода
 *
 * @author ma.kolpakov
 */
internal interface NotificationDelegate {

    fun showToast(message: String)

    fun showToast(@StringRes messageId: Int)
}
