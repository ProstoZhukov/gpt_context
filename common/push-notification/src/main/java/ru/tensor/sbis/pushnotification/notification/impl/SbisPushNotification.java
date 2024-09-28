package ru.tensor.sbis.pushnotification.notification.impl;

import android.app.Notification;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification_utils.PushPreferenceUtils;
import ru.tensor.sbis.pushnotification_utils.PushThemeProvider;

/**
 * Push notification with configured design, rules, sound and vibration.
 *
 * @author am.boldinov
 */
public class SbisPushNotification extends PushNotification {

    private static final String SBIS_GROUP_KEY = "sbis_group_key";
    private static final int LIGHT_ON_OFF_MS = 1000;

    public SbisPushNotification(@NonNull Context context) {
        super(context);
    }

    public SbisPushNotification(@NonNull Context context, String channelId) {
        super(context, channelId);
    }

    public SbisPushNotification(@NonNull Context context, String channelId, String category) {
        super(context, channelId, category);
    }

    @Override
    protected void configure(@NonNull NotificationCompat.Builder builder) {
        super.configure(builder);
        setDesign(builder);
        setRules(builder);
        setLights(builder);

        /* Если включен звук */
        if (PushPreferenceUtils.notificationSoundEnabled(getContext())) {
            setSound(builder);
        }

        /* Если включена вибрация,
           проверка поддержки вибрации устройством */
        if (PushPreferenceUtils.notificationVibrateEnabled(getContext())) {
            final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                setVibration(builder);
            }
        }
    }

    private void setDesign(@NonNull NotificationCompat.Builder builder) {
        builder
                .setColor(PushThemeProvider.getColor(getContext()))
                .setSmallIcon(PushThemeProvider.getSmallIconRes(getContext()));
    }

    private void setRules(@NonNull NotificationCompat.Builder builder) {
        builder
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setGroup(SBIS_GROUP_KEY);
    }

    private void setLights(@NonNull NotificationCompat.Builder builder) {
        builder.setLights(ContextCompat.getColor(getContext(), ru.tensor.sbis.design.R.color.color_primary),
                LIGHT_ON_OFF_MS, LIGHT_ON_OFF_MS);
    }

    private void setSound(@NonNull NotificationCompat.Builder builder) {
        final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alarmSound != null) {
            builder.setSound(alarmSound);
        }
    }

    private void setVibration(@NonNull NotificationCompat.Builder builder) {
        builder.setVibrate(PushPreferenceUtils.getDefaultVibrationPattern());
    }

}
