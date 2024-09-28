package ru.tensor.sbis.pushnotification.center.commands;

import androidx.annotation.NonNull;

import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface;

/**
 * Команда для отмены/удаления всех уведомлений приложения из шторки
 *
 * @author am.boldinov
 */
public class CancelAllCommand implements PushNotificationCommand {

    @Override
    public void execute(@NonNull NotificationManagerInterface manager) {
        manager.cancelAll();
    }

}
