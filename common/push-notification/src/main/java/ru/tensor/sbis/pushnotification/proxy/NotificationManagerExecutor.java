package ru.tensor.sbis.pushnotification.proxy;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification.util.PushLogger;

/**
 * Base implementation of {@link NotificationManagerInterface} interface.
 * Holding {@link NotificationManagerCompat} and directs him all action calls.
 *
 * @author am.boldinov
 */
public final class NotificationManagerExecutor implements NotificationManagerInterface {

    @NonNull
    private final NotificationManagerCompat mManager;

    private NotificationManagerExecutor(@NonNull NotificationManagerCompat manager) {
        mManager = manager;
    }

    public NotificationManagerExecutor(@NonNull Context context) {
        this(NotificationManagerCompat.from(context));
    }

    @Override
    public void notify(String tag, int id, PushNotification notification) {
        try {
            PushLogger.event("NotificationManagerExecutor.notify tag " + tag + " id " + id);
            mManager.notify(tag, id, notification.build());
        } catch (Exception e) {
            PushLogger.error(e);
            /* Повторная публикация уведомления */
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    mManager.notify(tag, id, notification.build());
                } catch (Exception e1) {
                    PushLogger.error(e1);
                }
            });
        }
    }

    @Override
    public void cancel(String tag, int id) {
        mManager.cancel(tag, id);
    }

    @Override
    public void cancelAll() {
        mManager.cancelAll();
    }

}
