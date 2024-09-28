package ru.tensor.sbis.pushnotification_utils.notification.channels;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import ru.tensor.sbis.pushnotification_utils.BuildConfig;
import ru.tensor.sbis.pushnotification_utils.R;
import ru.tensor.sbis.pushnotification_utils.PushPreferenceUtils;
import timber.log.Timber;

/**
 * Класс утилит для каналов уведомлений.
 *
 * @author am.boldinov
 */
public class NotificationChannelUtils {

    /** Prefix to provide distinction of application channels from other applications. */
    private static final String CHANNEL_ID_PREFIX = BuildConfig.MAIN_APP_ID.concat(".");

    /** Default channel id for notifications with unspecified channel. */
    public static final String DEFAULT_CHANNEL_ID = buildChannelId("DEFAULT");

    /** Id for channel for publishing file loading notifications. */
    public static final String FILE_LOADING_CHANNEL_ID = buildChannelId("_FILE_LOADING");

    // TODO удалить через несколько версий упоминание о старых каналах https://online.sbis.ru/opendoc.html?guid=b1520c61-f23c-4f40-bf91-4801baada77a
    /** Old Id for channel for publishing new notifications. Use only for deleting */
    private static final String OLD_NEW_CHANNEL_ID = buildChannelId("NEW");

    /** Old Id for channel for publishing updated notifications. Use only for deleting */
    private static final String OLD_UPDATE_CHANNEL_ID = buildChannelId("UPDATE");

    /** Old id for channel for publishing file loading notifications. Use only for deleting */
    private static final String OLD_FILE_LOADING_CHANNEL_ID_1 = buildChannelId("FILE_LOADING_CHANNEL");

    /** Old id for channel for publishing file loading notifications. Use only for deleting */
    private static final String OLD_FILE_LOADING_CHANNEL_ID_2 = buildChannelId("FILE_LOADING");

    /**
     * Build channel id from unique token.
     * @param uniqueToken - unique part of channel id
     * @return channel id
     */
    public static String buildChannelId(String uniqueToken) {
        return CHANNEL_ID_PREFIX.concat(uniqueToken);
    }

    /**
     * Check for notification channels support.
     * Channels supported from Android O.
     * @return true if channels is supported, false otherwise
     */
    public static boolean isSupportChannels() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * Open Android OS notification settings screen for new notifications.
     * @param context - application context
     * @return true - if screen started successfully, false - otherwise
     */
    public static boolean startNotificationChannelSettings(@NonNull Context context) {
        if (isSupportChannels()) {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationChannelUtils.DEFAULT_CHANNEL_ID);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Submit default channel in {@link NotificationManager} for notifying with unspecified channel.
     * @param context       - application context
     * @param channelName   - default channel name
     * @return result of submitting
     */
    public static boolean submitDefaultChannel(@NonNull Context context, @NonNull String channelName) {
        if (isSupportChannels()) {
            final NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.deleteNotificationChannel(OLD_NEW_CHANNEL_ID);
            manager.deleteNotificationChannel(OLD_UPDATE_CHANNEL_ID);
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            return submitChannel(context, channel);
        }
        return false;
    }

    /**
     * Submit channel in {@link NotificationManager} for notifying file loading notifications
     *
     * @param context   - application context
     * @return result of submitting
     */
    public static boolean submitFileLoadingChannel(@NonNull Context context) {
        if (isSupportChannels()) {
            NotificationChannel channel =
                    new NotificationChannel(
                            FILE_LOADING_CHANNEL_ID,
                            context.getString(R.string.push_notification_utils_file_loading_channel_name),
                            NotificationManager.IMPORTANCE_DEFAULT
                    );
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.setVibrationPattern(null);
            final NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.deleteNotificationChannel(OLD_FILE_LOADING_CHANNEL_ID_1);
            manager.deleteNotificationChannel(OLD_FILE_LOADING_CHANNEL_ID_2);
            manager.createNotificationChannel(channel);
            return true;
        }
        return false;
    }

    /**
     * Submit channel in {@link NotificationManager}.
     * @param context           - application context
     * @param channelToken      - channel token (unique part of id)
     * @param channelName       - channel name (displays in Notification Settings)
     * @param importance        - importance level (see {@link NotificationManager#IMPORTANCE_DEFAULT e.g.}
     * @return result of submitting
     */
    public static boolean submitChannel(@NonNull Context context, @NonNull String channelToken, @NonNull String channelName, int importance) {
        if (isSupportChannels()) {
            NotificationChannel channel = new NotificationChannel(buildChannelId(channelToken), channelName, importance);
            return submitChannel(context, channel);
        }
        return false;
    }

    /**
     * Submit channel in {@link NotificationManager}.
     * @param context - application context
     * @param channel - channel for submitting
     * @return result of submitting
     */
    public static boolean submitChannel(@NonNull Context context, @NonNull NotificationChannel channel) {
        if (isSupportChannels()) {
            if (assertChannelId(channel.getId())) {
                channel.enableVibration(true);
                channel.enableLights(true);
                final Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (sound != null) {
                    channel.setSound(sound, Notification.AUDIO_ATTRIBUTES_DEFAULT);
                }
                channel.setVibrationPattern(PushPreferenceUtils.getDefaultVibrationPattern());
                final NotificationManagerCompat manager = NotificationManagerCompat.from(context);
                manager.createNotificationChannel(channel);
                if (BuildConfig.DEBUG) {
                    Timber.d(
                            "Channel %s (%s) submitted.",
                            channel.getId(),
                            channel.getName()
                    );
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the enabled status of the notification channel
     * @param context - application context
     * @param channelId - identifier
     * @return true if the channel is enabled or missing in the system
     */
    public static boolean isChannelEnabled(@NonNull Context context, @NonNull String channelId) {
        if (isSupportChannels()) {
            final NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            final NotificationChannel channel = manager.getNotificationChannel(channelId);
            if (channel != null) {
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            } else {
                Timber.e("Notification channel with id %s not found", channelId);
            }
        }
        return true;
    }

    /**
     * Validate channel id.
     * @param channelId - channel id
     * @return true if assertion satisfied, false otherwise
     */
    private static boolean assertChannelId(String channelId) {
        // Assert channel id not empty
        if (TextUtils.isEmpty(channelId)) {
            Timber.e("Channel is is empty");
            return false;
        }
        // Assert channel id not reserved
        if (TextUtils.equals(channelId, NotificationChannel.DEFAULT_CHANNEL_ID)) {
            Timber.e("Channel id " + channelId + " is reserved by android.");
            return false;
        }
        return true;
    }

}
