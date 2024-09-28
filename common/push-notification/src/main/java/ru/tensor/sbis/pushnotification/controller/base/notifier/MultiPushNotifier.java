package ru.tensor.sbis.pushnotification.controller.base.notifier;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface;

/**
 * Реализация уведомителя, поддерживающего публикацию неограниченного кол-ва уведомлений по тегу.
 *
 * В случае вызова {@link #notify(NotificationManagerInterface, PushNotification)}
 * новое уведомление будет опубликовано как отдельный пуш в шторке.
 * Push-уведомления, опубликованные ранее по тому же тегу, останутся.
 *
 * Тег задает строковый идентификатор для заданной "группы" уведомлений.
 * Используется системным менеджером {@link androidx.core.app.NotificationManagerCompat}
 * при публикации уведомлений.
 *
 * @author am.boldinov
 */
public final class MultiPushNotifier implements PushNotifier {

    private static final String PREFIX = MultiPushNotifier.class.getCanonicalName();
    private static final String PREFERENCE_PREFIX = PREFIX.concat("_PREFERENCE_");
    private static final String KEY_LAST_NOTIFY_ID = MultiPushNotifier.class.getCanonicalName() + "_LAST_NOTIFY_ID_KEY";

    @NonNull
    private final SharedPreferences mPreferences;
    @NonNull
    private final String mNotifyTag;

    /**
     * @param notifyTag тег для публикации уведомлений
     */
    public MultiPushNotifier(@NonNull Context context, @NonNull String notifyTag) {
        mNotifyTag = notifyTag;
        final String preferenceName = PREFERENCE_PREFIX.concat(notifyTag);
        mPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public int notify(@NotNull NotificationManagerInterface manager, @NotNull PushNotification notification) {
        int notifyId = mPreferences.getInt(KEY_LAST_NOTIFY_ID, -1);
        if (notifyId < Integer.MAX_VALUE) {
            notifyId++;
        } else {
            notifyId = 0;
        }
        manager.notify(mNotifyTag, notifyId, notification);
        mPreferences.edit().putInt(KEY_LAST_NOTIFY_ID, notifyId).commit();
        return notifyId;
    }

    @Override
    public void cancel(@NotNull NotificationManagerInterface manager) {
        // ignore
    }

}
