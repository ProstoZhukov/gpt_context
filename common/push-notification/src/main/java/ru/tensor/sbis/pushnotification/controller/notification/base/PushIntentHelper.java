package ru.tensor.sbis.pushnotification.controller.notification.base;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.android_ext_decl.IntentAction;
import ru.tensor.sbis.pushnotification.PushContentCategory;
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentProvider;
import ru.tensor.sbis.pushnotification_utils.PendingIntentSupportUtils;

/**
 * Utility to navigate when performing push-notification actions.
 *
 * @author am.boldinov
 */
public class PushIntentHelper {

    @NonNull
    private final Context mContext;

    public PushIntentHelper(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Creates pending intent with specified intent on top
     * and main activity intent in back stack.
     *
     * @param contentIntent       - target intent
     * @param pushContentCategory - menu tab
     * @param mainExtras          - extras for main activity intent
     * @param requestCode         - pending intent request code
     *                            (take care of its uniqueness)
     * @param flags               - pending intent flags
     * @return configured pending intent
     */
    public PendingIntent createIntentWithBackStack(
            @NonNull Intent contentIntent,
            @NonNull PushContentCategory pushContentCategory,
            @Nullable Bundle mainExtras,
            int requestCode,
            int flags
    ) {
        final Intent mainIntent = createMainActivityIntent(pushContentCategory, mainExtras);
        PendingIntent pendingIntent;
        try {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder
                    .addNextIntent(mainIntent)
                    .addNextIntent(contentIntent);
            pendingIntent = stackBuilder.getPendingIntent(requestCode, flags);
        } catch (Exception e) {
            pendingIntent = PendingIntent.getActivity(mContext, requestCode, mainIntent, flags);
            // Logger.logDebug(e);
        }
        return pendingIntent;
    }

    /**
     * Creates pending intent with specified intent on top
     * and main activity intent in back stack.
     *
     * @param contentIntent       - target intent
     * @param pushContentCategory - menu tab
     * @param requestCode         - pending intent request code
     *                            (take care of its uniqueness)
     * @param flags               - pending intent flags
     * @return configured pending intent
     */
    public PendingIntent createIntentWithBackStack(
            @NonNull Intent contentIntent,
            @NonNull PushContentCategory pushContentCategory,
            int requestCode,
            int flags
    ) {
        return createIntentWithBackStack(
                contentIntent,
                pushContentCategory,
                null,
                requestCode,
                flags
        );
    }

    /**
     * Creates intent with specified intent on top
     * and main activity intent in back stack.
     *
     * @param contentIntent       - target intent
     * @param pushContentCategory - menu tab
     * @param mainExtras          - extras for main activity intent
     * @param requestCode         - pending intent request code
     *                            (take care of its uniqueness)
     * @return configured pending intent
     */
    public PendingIntent createIntentWithBackStack(
            @NonNull Intent contentIntent,
            @NonNull PushContentCategory pushContentCategory,
            @Nullable Bundle mainExtras,
            int requestCode
    ) {
        return createIntentWithBackStack(
                contentIntent,
                pushContentCategory,
                mainExtras,
                requestCode,
                PendingIntentSupportUtils.mutateToImmutableFlagsIfNeeded(PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }

    /**
     * Creates intent with specified intent on top
     * and main activity intent in back stack.
     *
     * @param contentIntent       - target intent
     * @param pushContentCategory - menu tab
     * @param requestCode         - pending intent request code
     *                            (take care of its uniqueness)
     * @return configured pending intent
     */
    public PendingIntent createIntentWithBackStack(
            @NonNull Intent contentIntent,
            @NonNull PushContentCategory pushContentCategory,
            int requestCode
    ) {
        return createIntentWithBackStack(
                contentIntent,
                pushContentCategory,
                null,
                requestCode
        );
    }

    /**
     * Creates main activity intent with specified menu tab.
     *
     * @param pushContentCategory - menu tab
     * @param extras              - extras for main activity intent
     * @return main activity intent
     */
    public Intent createMainActivityIntent(@NonNull PushContentCategory pushContentCategory, @Nullable Bundle extras) {
        Intent intent = PushNotificationComponentProvider.get(mContext).getMainActivityProvider()
                .getMainActivityIntent();
        intent.putExtra(IntentAction.Extra.PUSH_CONTENT_CATEGORY, pushContentCategory);
        if (extras != null) {
            intent.putExtras(extras);
        }
        return intent;
    }

    /**
     * Creates main activity intent with specified menu tab.
     *
     * @param pushContentCategory - menu tab
     * @return main activity intent
     */
    public Intent createMainActivityIntent(@NonNull PushContentCategory pushContentCategory) {
        return createMainActivityIntent(pushContentCategory, null);
    }

    /**
     * Creates main activity pending intent with specified menu tab.
     *
     * @param pushContentCategory - menu tab
     * @param extras              - extras for main activity intent
     * @param requestCode         - pending intent request code
     *                            (take care of its uniqueness)
     * @return configured pending intent
     */
    public PendingIntent createMainActivityPendingIntent(
            @NonNull PushContentCategory pushContentCategory,
            @Nullable Bundle extras,
            int requestCode
    ) {
        final Intent mainIntent = createMainActivityIntent(pushContentCategory, extras);
        return PendingIntent.getActivity(
                mContext,
                requestCode,
                mainIntent,
                PendingIntentSupportUtils.mutateToImmutableFlagsIfNeeded(PendingIntent.FLAG_UPDATE_CURRENT)
        );
    }

    /**
     * Creates main activity pending intent with specified menu tab.
     *
     * @param pushContentCategory - menu tab
     * @param requestCode         - pending intent request code
     *                            (take care of its uniqueness)
     * @return configured pending intent
     */
    public PendingIntent createMainActivityPendingIntent(
            @NonNull PushContentCategory pushContentCategory,
            int requestCode
    ) {
        return createMainActivityPendingIntent(pushContentCategory, null, requestCode);
    }

    /**
     * Метод для получения преднастроенного PendingIntent.getActivity(...)
     * с выставленными флагами 'FLAG_UPDATE_CURRENT' и 'FLAG_IMMUTABLE'.
     * (Флаг 'FLAG_IMMUTABLE' будет добавлен для всех версий Android с API >= 23)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateCurrentActivityImmutable(int requestCode, Intent intent) {
        return getUpdateActivityImmutable(requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Метод для получения PendingIntent.getActivity(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_IMMUTABLE'.
     * (Флаг 'FLAG_IMMUTABLE' будет добавлен для всех версий Android с API >= 23)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateActivityImmutable(int requestCode, Intent intent, int intentFlags) {
        return PendingIntentSupportUtils.getUpdateActivityImmutable(
                mContext, requestCode, intent, intentFlags
        );
    }

    /**
     * Метод для получения преднастроенного PendingIntent.getActivity(...)
     * с выставленными флагами 'FLAG_UPDATE_CURRENT' и 'FLAG_MUTABLE'.
     * (Флаг 'FLAG_MUTABLE' будет добавлен для всех версий Android с API >= 31)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateCurrentActivityMutable(int requestCode, Intent intent) {
        return getUpdateActivityMutable(requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Метод для получения PendingIntent.getActivity(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_MUTABLE'.
     * (Флаг 'FLAG_MUTABLE' будет добавлен для всех версий Android с API >= 31)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateActivityMutable(int requestCode, Intent intent, int intentFlags) {
        return PendingIntentSupportUtils.getUpdateActivityMutable(
                mContext, requestCode, intent, intentFlags
        );
    }

    /**
     * Метод для получения преднастроенного PendingIntent.getBroadcast(...)
     * с выставленными флагами 'FLAG_UPDATE_CURRENT' и 'FLAG_IMMUTABLE'.
     * (Флаг 'FLAG_IMMUTABLE' будет добавлен для всех версий Android с API >= 23)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateCurrentBroadcastImmutable(int requestCode, Intent intent) {
        return getUpdateBroadcastImmutable(requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Метод для получения PendingIntent.getBroadcast(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_IMMUTABLE'.
     * (Флаг 'FLAG_IMMUTABLE' будет добавлен для всех версий Android с API >= 23)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateBroadcastImmutable(int requestCode, Intent intent, int intentFlags) {
        return PendingIntentSupportUtils.getUpdateBroadcastImmutable(
                mContext, requestCode, intent, intentFlags
        );
    }

    /**
     * Метод для получения преднастроенного PendingIntent.getBroadcast(...)
     * с выставленными флагами 'FLAG_UPDATE_CURRENT' и 'FLAG_MUTABLE'.
     * (Флаг 'FLAG_MUTABLE' будет добавлен для всех версий Android с API >= 31)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateCurrentBroadcastMutable(int requestCode, Intent intent) {
        return getUpdateBroadcastMutable(requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Метод для получения PendingIntent.getBroadcast(...), в котором к переданному
     * значению флага будет дополнительно добавлено значение 'FLAG_MUTABLE'.
     * (Флаг 'FLAG_MUTABLE' будет добавлен для всех версий Android с API >= 31)
     *
     * @param requestCode код запроса.
     * @param intent      объект [Intent] для отложенного действия.
     * @param intentFlags значения флага для обработки действия системой.
     * @return [PendingIntent]
     */
    public PendingIntent getUpdateBroadcastMutable(int requestCode, Intent intent, int intentFlags) {
        return PendingIntentSupportUtils.getUpdateBroadcastMutable(
                mContext, requestCode, intent, intentFlags
        );
    }
}
