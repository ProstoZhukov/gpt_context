package ru.tensor.sbis.pushnotification.receiver

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.entrypoint_guard.bcr.EntryPointBroadcastReceiver
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider.get

/**
 * Приемник события об удалении пользователем пуш уведомления из шторки.
 *
 * @author am.boldinov
 */
class DismissNotificationReceiver : EntryPointBroadcastReceiver() {

    override fun onReady(context: Context, intent: Intent) {
        get(context).getPushCenter().onSwipeOut(intent)
    }

}