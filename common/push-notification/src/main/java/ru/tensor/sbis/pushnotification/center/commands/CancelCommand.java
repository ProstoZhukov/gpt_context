package ru.tensor.sbis.pushnotification.center.commands;

import androidx.annotation.NonNull;

import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface;

/**
 * Команда для отмены/удаления конкретного уведомления из шторки по тегу и id
 *
 * @author am.boldinov
 */
public class CancelCommand extends NotificationCommand {

    public CancelCommand(String tag, int notifyId) { super(tag, notifyId); }

    @Override
    public void execute(@NonNull NotificationManagerInterface manager) {
        manager.cancel(getTag(), getNotifyId());
    }

}
