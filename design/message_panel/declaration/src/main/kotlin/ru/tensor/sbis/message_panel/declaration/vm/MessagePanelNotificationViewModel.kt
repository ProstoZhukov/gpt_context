package ru.tensor.sbis.message_panel.declaration.vm

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData

/**
 * TODO: 11/13/2020 Добавить документацию
 *
 * @author ma.kolpakov
 */
interface MessagePanelNotificationViewModel {

    val toast: LiveData<String>

    fun showToast(message: String)
    fun showToast(@StringRes messageRes: Int)
}