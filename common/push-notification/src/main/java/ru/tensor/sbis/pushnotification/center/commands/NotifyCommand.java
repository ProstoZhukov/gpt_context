package ru.tensor.sbis.pushnotification.center.commands;

import androidx.annotation.NonNull;

import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface;

/**
 * Команда для публикации уведомления.
 *
 * @author am.boldinov
 */
public class NotifyCommand extends NotificationCommand {

    /**
     * Модель уведомления для публикации.
     */
    private final PushNotification mNotification;

    public NotifyCommand(String tag, int notifyId, PushNotification notification) {
        super(tag, notifyId);
        mNotification = notification;
    }

    /**
     * Получить модель уведомления для публикации.
     */
    public PushNotification getNotification() {
        return mNotification;
    }

    /**
     * Является ли публикация уведомления гарантированной.
     */
    public boolean isGuaranteed() {
        return mNotification.isGuaranteed();
    }

    @Override
    public void execute(@NonNull NotificationManagerInterface manager) {
        manager.notify(getTag(), getNotifyId(), getNotification());
    }

}
