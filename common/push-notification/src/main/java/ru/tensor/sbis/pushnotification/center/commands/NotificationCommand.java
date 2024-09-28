package ru.tensor.sbis.pushnotification.center.commands;

/**
 * Базовый клас команды для работы с конкретным уведомлением
 *
 * @author am.boldinov
 */
public abstract class NotificationCommand implements PushNotificationCommand {

    private final String mTag;
    private final int mNotifyId;

    public NotificationCommand(String tag, int notifyId) {
        mTag = tag;
        mNotifyId = notifyId;
    }

    /**
     * Возвращает тег для работы с менеджером пуш уведомлений
     */
    public String getTag() {
        return mTag;
    }

    /**
     * Возвращает идентификатор публикации для работы с менеджером пуш уведомлений
     */
    public int getNotifyId() {
        return mNotifyId;
    }

}
