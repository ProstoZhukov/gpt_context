package ru.tensor.sbis.application_tools.logcrashesinfo.utils

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.TaskStackBuilder
import ru.tensor.sbis.application_tools.logcrashesinfo.LogAndCrashesActivity
import ru.tensor.sbis.application_tools.logcrashesinfo.crashes.models.CrashViewModel
import ru.tensor.sbis.pushnotification_utils.PendingIntentSupportUtils.mutateToImmutableFlagsIfNeeded
import ru.tensor.sbis.pushnotification_utils.PushThemeProvider.getColor
import ru.tensor.sbis.pushnotification_utils.PushThemeProvider.getSmallIconRes

/**
 * @author du.bykov
 *
 * Отображение краша с помощью механизма уведомлений.
 */
class CrashReporterViaNotification(private val application: Application) {
    fun report(crashViewModel: CrashViewModel) {
        val notification = notification(crashViewModel)
        val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "CrashAnalytics",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(crashViewModel.date, DEFAULT_ID, notification)
    }

    private fun notification(crashViewModel: CrashViewModel): Notification {
        val crashActivityIntent = Intent(application, LogAndCrashesActivity::class.java)
        crashActivityIntent.putExtra(LogAndCrashesActivity.CRASH_ID, crashViewModel.date)
        val stackBuilder = TaskStackBuilder.create(application)
        stackBuilder.addNextIntent(crashActivityIntent)
        val pendingIntent = stackBuilder.getPendingIntent(
            221,
            mutateToImmutableFlagsIfNeeded(PendingIntent.FLAG_UPDATE_CURRENT)
        )
        val notificationBuilder = Notification.Builder(application)
            .setContentTitle(crashViewModel.place)
            .setContentText(crashViewModel.date)
            .setColor(getColor(application))
            .setSmallIcon(getSmallIconRes(application))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID)
        }
        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_ID = "crashes"
        private const val DEFAULT_ID = -1
    }
}