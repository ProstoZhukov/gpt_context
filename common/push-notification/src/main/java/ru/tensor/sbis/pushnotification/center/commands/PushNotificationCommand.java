package ru.tensor.sbis.pushnotification.center.commands;

import androidx.annotation.NonNull;

import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface;

/**
 * Интерфейс команды для выполнения на {@link NotificationManagerInterface}
 *
 * @author am.boldinov
 */
public interface PushNotificationCommand {

    /**
     * Выполняет команду над уведомлением, например публикация/удаления
     *
     * @param manager менеджер для публикации уведомлений
     */
    void execute(@NonNull NotificationManagerInterface manager);

}
