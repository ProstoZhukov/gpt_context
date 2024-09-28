package ru.tensor.sbis.pushnotification.notification.decorator.impl;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import ru.tensor.sbis.pushnotification.notification.decorator.NotificationDecorator;
import ru.tensor.sbis.pushnotification_utils.notification.channels.NotificationChannelUtils;

/**
 * Декоратор, устанавливающий канал для публикации уведомлений.
 *
 * @author am.boldinov
 */
public class ChannelDecorator implements NotificationDecorator {

    private final String mChannelId;
    private final boolean mUpdate;

    /**
     * @param update флаг типа действия, если true - обновление уведомления
     */
    public ChannelDecorator(boolean update) {
        mChannelId = NotificationChannelUtils.DEFAULT_CHANNEL_ID;
        mUpdate = update;
    }

    @Override
    public void decorate(@NonNull NotificationCompat.Builder builder) {
        builder.setChannelId(mChannelId);
        if (mUpdate) {
            builder.setOnlyAlertOnce(true);
        }
    }
}
