package ru.tensor.sbis.pushnotification.proxy;

import android.app.Notification;

import androidx.core.app.NotificationManagerCompat;
import ru.tensor.sbis.pushnotification.notification.PushNotification;

/**
 * Интерфейс менеджера для публикации/отмены публикации уведомлений.
 *
 * В приложении является заменой стандартного менеджера публикации уведомлений.
 * @see androidx.core.app.NotificationManagerCompat
 *
 * Позволяет внедрить свою логику обработки перед вызовом соответствующих методов
 * стандартного менеджера.
 *
 * @author am.boldinov
 */
public interface NotificationManagerInterface {

    /**
     * Публикации уведомления.
     *
     * @see androidx.core.app.NotificationManagerCompat#notify(String, int, Notification)
     *
     * @param tag          - тэг уведомления для публикации
     * @param id           - id уведомления для публикации
     * @param notification - объект уведомления
     */
    void notify(String tag, int id, PushNotification notification);

    /**
     * Отмена публикации уведомления.
     *
     * @see androidx.core.app.NotificationManagerCompat#cancel(String, int)
     *
     * @param tag - тэг уведомления
     * @param id  - id уведомления
     */
    void cancel(String tag, int id);

    /**
     * Отмена публикации всех уведомлений.
     *
     * @see NotificationManagerCompat#cancelAll()
     */
    void cancelAll();

}
