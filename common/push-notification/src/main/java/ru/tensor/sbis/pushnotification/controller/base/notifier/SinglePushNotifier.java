package ru.tensor.sbis.pushnotification.controller.base.notifier;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;

import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface;

/**
 * Реализация Singleton уведомителя. Экземпляр этого класса публикует одно уведомление по тегу.
 * В случае повторного вызова {@link #notify(NotificationManagerInterface, PushNotification)}
 * опубликованное ранее уведомление заменится на новое.
 *
 * @author am.boldinov
 */
public final class SinglePushNotifier implements PushNotifier {

    private static final int NOTIFY_ID = 193531;

    @NonNull
    private final String mNotifyTag;

    /**
     * @param notifyTag тег для указания пространства имен для типа уведомлений
     */
    public SinglePushNotifier(@NonNull String notifyTag) {
        mNotifyTag = notifyTag;
    }

    @Override
    public int notify(@NotNull NotificationManagerInterface manager, @NotNull PushNotification notification) {
        manager.notify(mNotifyTag, NOTIFY_ID, notification);
        return NOTIFY_ID;
    }

    @Override
    public void cancel(@NotNull NotificationManagerInterface manager) {
        manager.cancel(mNotifyTag, NOTIFY_ID);
    }

}
