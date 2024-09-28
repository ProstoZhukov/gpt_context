package ru.tensor.sbis.design.message_panel.vm.notification

import android.content.Context
import android.widget.Toast
import javax.inject.Inject

/**
 * @author ma.kolpakov
 */
internal class NotificationDelegateImpl @Inject constructor(
    private val appContext: Context
) : NotificationDelegate {

    override fun showToast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
    }

    override fun showToast(messageId: Int) =
        showToast(appContext.resources.getString(messageId))
}
