package ru.tensor.sbis.pushnotification.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import ru.tensor.sbis.entrypoint_guard.service.EntryPointService;
import ru.tensor.sbis.pushnotification.R;
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider;
import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification.util.PushLogger;
import ru.tensor.sbis.pushnotification_utils.PendingIntentSupportUtils;
import ru.tensor.sbis.pushnotification_utils.PushThemeProvider;
import timber.log.Timber;

/**
 * Сервис, который позволяет приложению постоянно оставаться в памяти.
 * <p>
 * На некоторых прошивках после выгрузки приложения из Recent Apps
 * все интенты, направленные в приложение игнорируются.
 * В такой ситуации уведомления, приходящие от сервиса Firebase
 * не доходят до самого приложения.
 *
 * @author am.boldinov
 */
public class KeepAliveForegroundService extends EntryPointService {

    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    @Override
    protected void onReady() { }

    // region Drive service methods

    /**
     * Возвращает значение настройки: включен или выключен сервис.
     *
     * @param context - контекст приложения
     * @return значение настройки
     */
    public static boolean isEnabled(@NonNull Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(getPreferenceKey(context), getDefaultState(context));
    }

    /**
     * Запускает сервис, если настройка "включена".
     *
     * @param context - контекст приложения
     */
    public static void startIfNeed(@NonNull Context context) {
        if (isEnabled(context)) {
            start(context);
        }
    }

    /**
     * Останавливает сервис, выставляя настройку в положение "выключено".
     *
     * @param context - контекст приложения
     */
    public static void disable(@NonNull Context context) {
        if (isEnabled(context)) {
            PushLogger.event("Disable service forcibly.");
            SharedPreferences preferences = getSharedPreferences(context);
            preferences.edit().putBoolean(getPreferenceKey(context), false).apply();
            stop(context);
        }
    }

    /**
     * Обновляет состояние сервиса в соответствии с текущей настройкой.
     *
     * @param context - контекст приложения
     */
    public static void update(@NonNull Context context) {
        PushLogger.event("Update service according to preference.");
        if (isEnabled(context)) {
            start(context);
        } else {
            stop(context);
        }
    }

    private static void start(@NonNull Context context) {
        PushLogger.event("Start service.");
        try {
            Intent intent = new Intent(context, KeepAliveForegroundService.class);
            ContextCompat.startForegroundService(context, intent);
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    private static void stop(@NonNull Context context) {
        PushLogger.event("Stop service.");
        try {
            Intent intent = new Intent(context, KeepAliveForegroundService.class);
            context.stopService(intent);
        } catch (Exception e) {
            Timber.d(e);
        }
    }
    // endregion

    // region Service methods
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(FOREGROUND_NOTIFICATION_ID, createNotification());
        PushLogger.event("Service started successfully.");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        PushLogger.event("Task removed. Attempt to restart service if need.");
        startIfNeed(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        PushLogger.event("Service destroyed. Attempt to restart if need.");
        startIfNeed(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // endregion

    // region Notification
    private Notification createNotification() {
        return new ForegroundNotification(getApplicationContext()).build();
    }

    private static final class ForegroundNotification extends PushNotification {

        ForegroundNotification(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void configure(@NonNull NotificationCompat.Builder builder) {
            builder.setColor(PushThemeProvider.getColor(getContext()));
            builder.setSmallIcon(PushThemeProvider.getSmallIconRes(getContext()));
            builder.setPriority(Notification.PRIORITY_MIN);
            builder.setCategory(Notification.CATEGORY_SERVICE);
            builder.setContentTitle(getContext().getString(R.string.push_notification_stable_notification_title));
            builder.setContentIntent(createAction());
        }

        private PendingIntent createAction() {
            // При клике на уведомление открываем экран настроек
            Intent intent = PushNotificationComponentProvider.get(getContext())
                    .getPushIntentHelper().createMainActivityIntent(new StableNotificationContentCategory());
            return PendingIntentSupportUtils.getUpdateActivityImmutable(getContext(), hashCode(), intent);
        }
    }
    // endregion

    // region Shared prefs

    /**
     * Возвращает ключ, под которым записана настройка сервиса.
     *
     * @param context - контекст приложения
     * @return ключ из настроек
     */
    @NonNull
    public static String getPreferenceKey(@NonNull Context context) {
        return context.getString(ru.tensor.sbis.pushnotification_utils.R.string.push_notification_utils_settings_stable_push_service_key);
    }

    /**
     * Возвращает значение настройки по умолчанию.
     *
     * @param context - контекст приложения
     * @return значение по умолчанию
     */
    private static boolean getDefaultState(@NonNull Context context) {
        return context.getResources().getBoolean(R.bool.push_notification_default_stable_push_service_enabled);
    }

    /**
     * Возвращает настройки приложения.
     *
     * @param context - контекст приложения
     * @return настройки приложения
     */
    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    // endregion

}
