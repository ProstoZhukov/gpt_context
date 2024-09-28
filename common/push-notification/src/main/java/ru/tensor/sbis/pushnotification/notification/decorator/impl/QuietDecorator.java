package ru.tensor.sbis.pushnotification.notification.decorator.impl;

import android.app.Notification;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import ru.tensor.sbis.pushnotification.notification.decorator.NotificationDecorator;

/**
 * Декоратор, задающий приоритет публикации уведомления.
 * <p>
 * Также в случае указания ниизкого приоритета уведомление публикуется без вибрации и звука.
 *
 * @author am.boldinov
 */
public class QuietDecorator implements NotificationDecorator {

    private final boolean mHeadsUp;

    /**
     * @param headsUp приоритет публикации уведомления
     */
    public QuietDecorator(boolean headsUp) {
        mHeadsUp = headsUp;
    }

    @Override
    public void decorate(@NonNull NotificationCompat.Builder builder) {
        builder.setDefaults(0) // cancel defaults
                .setLights(0, 0, 0) // disable light
                .setSound(null) // disable sound
                .setVibrate(mHeadsUp ? new long[]{0} : null) // disable vibration
                .setPriority(mHeadsUp // enable/disable heads-up
                        ? Notification.PRIORITY_MAX
                        : Notification.PRIORITY_LOW);
    }

}
