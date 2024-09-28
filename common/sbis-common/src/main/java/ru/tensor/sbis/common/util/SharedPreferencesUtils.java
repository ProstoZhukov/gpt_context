package ru.tensor.sbis.common.util;

import static ru.tensor.sbis.design.utils.PreconditionsKt.checkSafe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.BoolRes;
import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

import ru.tensor.sbis.common.R;
import ru.tensor.sbis.common.util.date.DateFormatTemplate;
import ru.tensor.sbis.common.util.date.DateFormatUtils;
import ru.tensor.sbis.info_decl.model.NotificationType;

public class SharedPreferencesUtils {

    private static SharedPreferences sPreferences;

    public static final String SHARED_PREFS_NAVIGATION_MENU_ITEM = SharedPreferencesUtils.class.getCanonicalName() + ".shared_prefs_navigation_menu_item";
    private static final String SHARED_PREFS_NOTIFICATION_TYPES_SHOW_PUSH = SharedPreferencesUtils.class.getCanonicalName() + ".shared_prefs_notification_types_show_push";
    public static final String SHARED_PREFS_CALENDAR_AVAILABILITY_MODULE_SETTING = SharedPreferencesUtils.class.getCanonicalName() + ".shared_prefs_calendar_module_availability";
    public static final String SHARED_PREFS_MOTIVATION_AVAILABILITY_MODULE_SETTING = SharedPreferencesUtils.class.getCanonicalName() + ".shared_prefs_motivation_module_availability";
    public static final String SHARED_PREFS_MEETINGS_AVAILABILITY_MODULE_SETTING = SharedPreferencesUtils.class.getCanonicalName() + ".shared_prefs_meetings_module_availability";
    public static final String SHARED_PREFS_EMPLOYEES_AVAILABILITY_MODULE_SETTING = SharedPreferencesUtils.class.getCanonicalName() + ".shared_prefs_employees_module_availability";
    private static final String REQUEST_CONTACT_PERMISSIONS = SharedPreferencesUtils.class.getCanonicalName() + ".request_contact_permissions";


    public static void setBooleanValueFromResources(@NonNull Context context, @NonNull String key, @BoolRes int valueRes) {
        getSharedPreferences(context)
                .edit()
                .putBoolean(key, context.getResources().getBoolean(valueRes))
                .apply();
    }

    public static boolean getBooleanValue(@NonNull Context context, @NonNull String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void setBooleanValue(@NonNull Context context, @NonNull String key, boolean value) {
        getSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public static void clearNavigationMenuPosition(@NonNull Context context) {
        getSharedPreferences(context).edit().remove(SHARED_PREFS_NAVIGATION_MENU_ITEM).apply();
    }

    public static void setShowPushForNotificationTypes(@NonNull Context context, boolean show) {
        getSharedPreferences(context)
                .edit()
                .putBoolean(SHARED_PREFS_NOTIFICATION_TYPES_SHOW_PUSH, show)
                .apply();
    }

    public static void setShowPushForNotificationType(@NonNull Context context, @NonNull NotificationType type, boolean show) {
        getSharedPreferences(context).edit().putBoolean(type.toString(), show).apply();
    }

    public static void setShowPushForNotificationTypes(@NonNull Context context, @NonNull List<NotificationType> types, boolean show) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        for (NotificationType type : types) {
            editor.putBoolean(type.toString(), show);
        }
        editor.apply();
    }

    public static void clearCalendarAvailability(@NonNull Context context) {
        getSharedPreferences(context).edit().putInt(SHARED_PREFS_CALENDAR_AVAILABILITY_MODULE_SETTING, -1).apply();
    }

    public static boolean getShowPushForNotificationTypes(@NonNull Context context) {
        return getSharedPreferences(context).getBoolean(SHARED_PREFS_NOTIFICATION_TYPES_SHOW_PUSH, true);
    }

    public static boolean getShowPushForNotificationType(@NonNull Context context, @NonNull NotificationType type) {
        return getSharedPreferences(context).getBoolean(type.toString(), true);
    }

    public static boolean shouldRequestContactsPermissions(@NonNull Context context) {
        return getSharedPreferences(context.getApplicationContext())
                .getBoolean(REQUEST_CONTACT_PERMISSIONS, true);
    }

    public static void disableRequestContactPermissions(@NonNull Context context) {
        getSharedPreferences(context.getApplicationContext())
                .edit()
                .putBoolean(REQUEST_CONTACT_PERMISSIONS, false)
                .apply();
    }

    public static boolean isNeedToRecordHistory(@NonNull Context context) {
        boolean defaultValue = context.getResources().getBoolean(R.bool.default_value_enable_history_recording);
        return getSharedPreferences(context.getApplicationContext())
                .getBoolean(context.getString(R.string.common_settings_enable_history_recording_key), defaultValue);
    }

    public static boolean isNeedToRecordLogs(@NonNull Context context) {
        boolean defaultValue = context.getResources().getBoolean(R.bool.default_value_enable_logs_recording);
        return getSharedPreferences(context.getApplicationContext())
                .getBoolean(context.getString(R.string.common_settings_enable_logs_recording_key), defaultValue);
    }

    public static boolean isNeedToClearCollagesCache(@NonNull Context context) {
        return getSharedPreferences(context.getApplicationContext())
                .getBoolean(context.getString(R.string.common_need_to_clear_collages_cache), true);
    }

    public static void setNeedToClearCollagesCache(@NonNull Context context, boolean needClear) {
        getSharedPreferences(context.getApplicationContext())
                .edit()
                .putBoolean(context.getString(R.string.common_need_to_clear_collages_cache), needClear)
                .apply();
    }

    /**
     * возвращает флаг доступа к разделу Сотрудники (состояние прав)
     *
     * смотрит в поле [SHARED_PREFS_EMPLOYEES_AVAILABILITY_MODULE_SETTING]
     * при -1 - значение при инициализации, @return false
     * при  0 - права отсутствуют, @return false
     * при  1 - права есть, @return true
     */
    public static boolean hasEmployeesPermissions(@NonNull Context context) {
        return getSharedPreferences(context.getApplicationContext())
                .getInt(SHARED_PREFS_EMPLOYEES_AVAILABILITY_MODULE_SETTING, -1) == 1;
    }

    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        if (sPreferences == null) {
            sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sPreferences;
    }
}
