package ru.tensor.sbis.pushnotification.notification.decorator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

/**
 * Интерфейс для декорации объекта push-уведомления.
 *
 * @author am.boldinov
 */
public interface NotificationDecorator {

    /**
     * Применение декорирования
     *
     * @param builder билдер стандартного системного push-уведомления
     */
    void decorate(@NonNull NotificationCompat.Builder builder);

}
