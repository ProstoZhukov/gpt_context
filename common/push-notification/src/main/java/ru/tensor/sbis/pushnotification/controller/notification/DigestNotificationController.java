package ru.tensor.sbis.pushnotification.controller.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.android_ext_decl.IntentAction;
import ru.tensor.sbis.pushnotification.PushType;
import ru.tensor.sbis.pushnotification.controller.base.SinglePushNotificationController;
import ru.tensor.sbis.pushnotification.controller.notification.base.PushIntentHelper;
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider;
import ru.tensor.sbis.pushnotification.model.PushData;
import ru.tensor.sbis.pushnotification.model.factory.PushDataFactory;
import ru.tensor.sbis.pushnotification.notification.PushNotification;
import ru.tensor.sbis.pushnotification.notification.decorator.impl.ContentDecorator;
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage;
import ru.tensor.sbis.pushnotification.util.PushLogger;
import ru.tensor.sbis.pushnotification_utils.PendingIntentSupportUtils;

/**
 * Уведомление-сводка о пропущенном в период тишины (ночью).
 *
 * @author am.boldinov
 */
public final class DigestNotificationController extends SinglePushNotificationController {

    @NonNull
    private final PushDataFactory<DigestPushData> mDataFactory = new DigestPushDataFactory();
    @NonNull
    private final PushIntentHelper mPushIntentHelper;

    public DigestNotificationController(@NonNull Context context) {
        super(context);
        mPushIntentHelper = new PushIntentHelper(context);
    }

    @Nullable
    @Override
    protected PushNotification createNotification(@NotNull PushNotificationMessage message) {
        final DigestPushData model = mDataFactory.create(message);
        final List<PushType> missedTypes = model.getMissedTypes();
        if (missedTypes != null && !missedTypes.isEmpty()) {
            final PushNotification notification = getPushBuildingHelper().createSbisNotification(new ContentDecorator(model.getMessage()));
            final NotificationCompat.Style style = new NotificationCompat.BigTextStyle().bigText(model.getMessage().getMessage());
            notification.getBuilder().setStyle(style);

            final Intent intent;
            if (missedTypes.size() > 1) {
                // Флаг для открытия NavigationDrawer, если типов несколько
                intent = PushNotificationComponentProvider.get(getContext()).getPushIntentHelper()
                        .createMainActivityIntent(new DigestContentCategory(PushType.DIGEST));
                intent.putExtra(IntentAction.Extra.MAIN_ACTIVITY_OPEN_NAVIGATION_EXTRA, true);
            } else {
                // Выставляем вкладку, которую нужно открыть, если тип всего 1
                intent = PushNotificationComponentProvider.get(getContext()).getPushIntentHelper()
                        .createMainActivityIntent(new DigestContentCategory(missedTypes.get(0)));
            }
            final PendingIntent pendingIntent = mPushIntentHelper.getUpdateActivityImmutable(
                    hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT
            );
            notification.getBuilder().setContentIntent(pendingIntent);
            return notification;
        } else {
            PushLogger.error("Received Digest Push with empty missed types.");
            return null;
        }
    }

    private static final class DigestPushDataFactory implements PushDataFactory<DigestPushData> {

        private static final String KEY_MISSED_TYPES = "missedTypes";

        @NotNull
        @Override
        public DigestPushData create(@NotNull PushNotificationMessage message) {
            final DigestPushData data = new DigestPushData(message);
            final JSONArray missedTypesJson = message.getData().optJSONArray(KEY_MISSED_TYPES);
            if (missedTypesJson != null) {
                int length = missedTypesJson.length();
                final List<PushType> missedTypes = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    String value = missedTypesJson.optString(i);
                    PushType type = PushType.fromValue(value);
                    if (type != PushType.UNDEFINED) {
                        missedTypes.add(type);
                    }
                }
                data.setMissedTypes(missedTypes);
            }
            return data;
        }
    }

    private static final class DigestPushData extends PushData {

        @Nullable
        private List<PushType> mMissedTypes;

        public DigestPushData(@NotNull PushNotificationMessage message) {
            super(message);
        }

        @Nullable
        public List<PushType> getMissedTypes() {
            return mMissedTypes;
        }

        public void setMissedTypes(@Nullable List<PushType> missedTypes) {
            mMissedTypes = missedTypes;
        }
    }
}
