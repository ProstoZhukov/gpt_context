package ru.tensor.sbis.pushnotification_utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import ru.tensor.sbis.pushnotification_utils.notification.channels.NotificationChannelUtils;

/**
 * Класс утилит для получения сохраненных настроек пуш уведомлений из SharedPreferences
 *
 * @author am.boldinov
 */
public class PushPreferenceUtils {

    private static SharedPreferences sPreferences;
    private static final int VIBRATION_PATTERN_ELEMENT = 500;

    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        if (sPreferences == null) {
            sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sPreferences;
    }

    /**
     * Возвращает статус настройки звука пуш-уведомлений.
     * Актуально для версий до Android Oreo.
     */
    public static boolean notificationSoundEnabled(@NonNull Context context) {
        return getSharedPreferences(context).getBoolean(context.getString(R.string.push_notification_utils_settings_notification_sound_key),
                context.getResources().getBoolean(R.bool.push_notification_utils_default_value_push_notification_sound));
    }

    /**
     * Возвращает статус настройки вибрации пуш-уведомлений.
     * Актуально для версий до Android Oreo.
     */
    public static boolean notificationVibrateEnabled(@NonNull Context context) {
        return getSharedPreferences(context).getBoolean(context.getString(R.string.push_notification_utils_settings_notification_vibration_key),
                context.getResources().getBoolean(R.bool.push_notification_utils_default_value_push_notification_vibrate));
    }

    /**
     * Возвращает паттерн вибрации при получении пуш-уведомления
     */
    public static long[] getDefaultVibrationPattern() {
        return new long[] { VIBRATION_PATTERN_ELEMENT, VIBRATION_PATTERN_ELEMENT, VIBRATION_PATTERN_ELEMENT };
    }

    /**
     * Проверяет включены ли уведомления в настройках системы, с учетом дефолтного канала уведомлений
     *
     * @return true если уведомления включены и будут показаны, false если уведомления отключены
     */
    public static boolean systemNotificationsEnabled(@NonNull Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
                && NotificationChannelUtils.isChannelEnabled(context, NotificationChannelUtils.DEFAULT_CHANNEL_ID);
    }
}
