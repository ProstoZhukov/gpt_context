package ru.tensor.sbis.pushnotification.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import ru.tensor.sbis.pushnotification_utils.notification.channels.NotificationChannelUtils;
import ru.tensor.sbis.pushnotification.notification.decorator.NotificationDecorator;

/**
 * Base push notification class. Allows you to create
 * system notifications with "based on", "configurable" and "decorated" principles.
 *
 * @author am.boldinov
 */
public class PushNotification {

    private static final String DEFAULT_CATEGORY = Notification.CATEGORY_MESSAGE;

    @NonNull
    private final NotificationCompat.Builder builder;

    private boolean mAllowedToDecorate = true;
    private boolean mIsGuaranteed = false;

    /**
     * Create notification with default category.
     *
     * @param builder - builder
     */
    public PushNotification(@NonNull NotificationCompat.Builder builder) {
        this(builder, DEFAULT_CATEGORY);
    }

    /**
     * Create notification with specified category.
     *
     * @param builder  - builder
     * @param category - notification category (see {@link Notification#CATEGORY_MESSAGE} e.g.)
     */
    public PushNotification(@NonNull NotificationCompat.Builder builder, String category) {
        this.builder = builder;
        this.builder.setCategory(category);
        configure(this.builder);
    }

    /**
     * Create notification with default channel id.
     *
     * @param context - application context
     */
    public PushNotification(@NonNull Context context) {
        this(context, NotificationChannelUtils.DEFAULT_CHANNEL_ID);
    }

    /**
     * Create notification with specified channel id.
     *
     * @param context   - application context
     * @param channelId - channel id
     */
    public PushNotification(@NonNull Context context, @NonNull String channelId) {
        this(new NotificationCompat.Builder(context.getApplicationContext(), channelId));
    }

    /**
     * Create notification with specified channel id and category.
     *
     * @param context   - application context
     * @param channelId - channel id
     * @param category  - notification category
     */
    public PushNotification(@NonNull Context context, @NonNull String channelId, @NonNull String category) {
        this(new NotificationCompat.Builder(context.getApplicationContext(), channelId), category);
    }

    /**
     * Configure notification.
     *
     * @param builder - builder
     */
    protected void configure(@NonNull NotificationCompat.Builder builder) {
        /* Override this method to configure notification */
    }

    /**
     * @return Builder class for {@link NotificationCompat} objects.
     */
    @NonNull
    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

    /**
     * @return application context
     */
    @SuppressLint("RestrictedApi")
    @NonNull
    protected Context getContext() {
        return builder.mContext;
    }

    /**
     * @return true if decorator applying enabled.
     */
    public boolean isAllowedToDecorate() {
        return mAllowedToDecorate;
    }

    /**
     * Enable/disable decorator applying.
     */
    public void setAllowedToDecorate(boolean allowed) {
        mAllowedToDecorate = allowed;
    }

    /**
     * @return true if notification should be shown whatever the circumstances.
     */
    public boolean isGuaranteed() {
        return mIsGuaranteed;
    }

    /**
     * If true - notification should be shown whatever the circumstances.
     */
    public void setGuaranteed(boolean guaranteed) {
        mIsGuaranteed = guaranteed;
    }

    /**
     * Build system notification instance.
     *
     * @return system notification
     */
    public Notification build() {
        return builder.build();
    }

    /**
     * Sequentially applies decorators to notification if decorating is allowed.
     *
     * @param decorators - decorators
     * @return decorated notification
     */
    public PushNotification decorate(@NonNull NotificationDecorator... decorators) {
        if (mAllowedToDecorate) {
            for (NotificationDecorator decorator : decorators) {
                if (decorator != null) {
                    decorator.decorate(builder);
                }
            }
        }
        return this;
    }

}
