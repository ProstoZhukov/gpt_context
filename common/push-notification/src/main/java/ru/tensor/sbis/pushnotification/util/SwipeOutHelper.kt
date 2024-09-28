package ru.tensor.sbis.pushnotification.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import ru.tensor.sbis.pushnotification.R
import ru.tensor.sbis.pushnotification.receiver.DismissNotificationReceiver
import ru.tensor.sbis.pushnotification_utils.PendingIntentSupportUtils

/**
 * Утилита для работы со свайпом по пуш-уведомлению
 *
 * @author am.boldinov
 */
internal object SwipeOutHelper {

    private const val NOTIFY_TAG_FOR_SWIPE_OUT_KEY = "NOTIFY_TAG_FOR_SWIPE_OUT_KEY"
    private const val NOTIFY_ID_FOR_SWIPE_OUT_KEY = "NOTIFY_ID_FOR_SWIPE_OUT"

    /**
     * Создает интент на обработку события по удалению пуш-уведомления
     * @see [androidx.core.app.NotificationCompat.Builder.setDeleteIntent]
     *
     * @param context контекст приложения
     * @param tag тег, под которым было опубликовано уведомление
     * @param notifyId идентификатор, под которым было опубликовано уведомление
     * @see [ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface]
     */
    @JvmStatic
    fun createSwipeOutIntent(context: Context, tag: String, notifyId: Int): PendingIntent {
        // Uri needed to avoid matching of dismiss pending intents
        val uri = Uri.parse(String.format("content://sbis/push/%s/%d", tag, notifyId))
        val mimeType: String = context.getString(R.string.push_notification_mime_type)
        // Create intent with notify info
        val intent = Intent(context, DismissNotificationReceiver::class.java)
        intent.putExtra(NOTIFY_TAG_FOR_SWIPE_OUT_KEY, tag)
        intent.putExtra(NOTIFY_ID_FOR_SWIPE_OUT_KEY, notifyId)
        intent.setDataAndType(uri, mimeType)

        return PendingIntentSupportUtils.getUpdateBroadcastImmutable(context, 0, intent)
    }

    /**
     * Возвращает тег, под которым было опубликовано уведомление.
     * Можно использовать в момент обработки события удаления.
     */
    @JvmStatic
    fun getSwipeOutTag(intent: Intent): String? {
        return intent.getStringExtra(NOTIFY_TAG_FOR_SWIPE_OUT_KEY)
    }

    /**
     * Возвращает идентификатор, под которым было опубликовано ранее уведомление.
     *  Можно использовать в момент обработки события удаления.
     */
    @JvmStatic
    fun getSwipeOutId(intent: Intent): Int {
        return intent.getIntExtra(NOTIFY_ID_FOR_SWIPE_OUT_KEY, -1)
    }

}