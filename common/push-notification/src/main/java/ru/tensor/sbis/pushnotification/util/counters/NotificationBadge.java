package ru.tensor.sbis.pushnotification.util.counters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

/**
 * Класс для установки значения счётчика на значке приложения (бейжджа), поддерживающий различные Launcher'ы.
 * Заимствован из репозитория приложения Telegram, в котором он используется вместо библиотеки ShortcutBadger. Доработан
 * для возможности провайдить посредством Dagger.
 * @see <a href="https://github.com/DrKLO/Telegram/blob/d073b80063c568f31d81cc88c927b47c01a1dbf4/TMessagesProj/src/main/java/org/telegram/messenger/NotificationBadge.java">Оригинал</a>
 */
public class NotificationBadge {

    private final List<Class<? extends Badger>> BADGERS = new LinkedList<>();
    private boolean initied;
    private Badger badger;
    private ComponentName componentName;
    @NonNull
    private final Context context;
    @NonNull
    private final Handler handler;

    public NotificationBadge(@NonNull Context context) {
        this.context = context.getApplicationContext();
        handler = new Handler(context.getMainLooper());
    }

    /**
     * Интерфейс бейджа
     */
    public interface Badger {
        /**
         * Обновить счетчик в бейдже
         * @param badgeCount актуальный счетчик
         */
        void executeBadge(int badgeCount);

        /**
         * @return список поддерживаемых Launcher'ов
         */
        List<String> getSupportLaunchers();
    }

    /**
     * Бейдж для кастомного Adw Launcker'а
     */
    public class AdwHomeBadger implements Badger {

        private static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
        private static final String PACKAGENAME = "PNAME";
        private static final String CLASSNAME = "CNAME";
        private static final String COUNT = "COUNT";

        @Override
        public void executeBadge(int badgeCount) {

            final Intent intent = new Intent(INTENT_UPDATE_COUNTER);
            intent.putExtra(PACKAGENAME, componentName.getPackageName());
            intent.putExtra(CLASSNAME, componentName.getClassName());
            intent.putExtra(COUNT, badgeCount);
            if (canResolveBroadcast(intent)) {
                runOnUIThread(() -> context.sendBroadcast(intent));
            }
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Arrays.asList(
                    "org.adw.launcher",
                    "org.adwfreak.launcher"
            );
        }
    }

    /**
     * Бейдж для кастомного Apex Launcker'а
     */
    public class ApexHomeBadger implements Badger {

        private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
        private static final String PACKAGENAME = "package";
        private static final String COUNT = "count";
        private static final String CLASS = "class";

        @Override
        public void executeBadge(int badgeCount) {

            final Intent intent = new Intent(INTENT_UPDATE_COUNTER);
            intent.putExtra(PACKAGENAME, componentName.getPackageName());
            intent.putExtra(COUNT, badgeCount);
            intent.putExtra(CLASS, componentName.getClassName());
            if (canResolveBroadcast(intent)) {
                runOnUIThread(() -> context.sendBroadcast(intent));
            }
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.anddoes.launcher");
        }
    }

    /**
     * Бейдж для Launcker'а устройств Asus
     */
    public class AsusHomeBadger implements Badger {

        private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
        private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
        private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";

        @Override
        public void executeBadge(int badgeCount) {
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
            intent.putExtra(INTENT_EXTRA_PACKAGENAME, componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, componentName.getClassName());
            intent.putExtra("badge_vip_count", 0);
            if (canResolveBroadcast(intent)) {
                runOnUIThread(() -> context.sendBroadcast(intent));
            }
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.asus.launcher");
        }
    }

    /**
     * Бейдж для Launcker'а по умолчанию
     */
    public class DefaultBadger implements Badger {
        private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
        private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
        private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";

        @Override
        public void executeBadge(int badgeCount) {
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
            intent.putExtra(INTENT_EXTRA_PACKAGENAME, componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, componentName.getClassName());
            runOnUIThread(() -> {
                try {
                    context.sendBroadcast(intent);
                } catch (Exception ignore) {

                }
            });
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Arrays.asList(
                    "fr.neamar.kiss",
                    "com.quaap.launchtime",
                    "com.quaap.launchtime_official"
            );
        }
    }

    /**
     * Бейдж для Launcker'а устройств Huawei
     */
    public class HuaweiHomeBadger implements Badger {

        @Override
        public void executeBadge(int badgeCount) {
            final Bundle localBundle = new Bundle();
            localBundle.putString("package", context.getPackageName());
            localBundle.putString("class", componentName.getClassName());
            localBundle.putInt("badgenumber", badgeCount);
            runOnUIThread(() -> {
                try {
                    context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
                } catch (Exception e) {
                    Timber.d(e);
                }
            });
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList(
                    "com.huawei.android.launcher"
            );
        }
    }

    /**
     * Бейдж для Launcker'а устройств Htc
     */
    public class NewHtcHomeBadger implements Badger {

        private static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
        private static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
        private static final String PACKAGENAME = "packagename";
        private static final String COUNT = "count";
        private static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
        private static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";

        @Override
        public void executeBadge(int badgeCount) {

            final Intent intent1 = new Intent(INTENT_SET_NOTIFICATION);
            intent1.putExtra(EXTRA_COMPONENT, componentName.flattenToShortString());
            intent1.putExtra(EXTRA_COUNT, badgeCount);

            final Intent intent = new Intent(INTENT_UPDATE_SHORTCUT);
            intent.putExtra(PACKAGENAME, componentName.getPackageName());
            intent.putExtra(COUNT, badgeCount);

            if (canResolveBroadcast(intent1) || canResolveBroadcast(intent)) {
                runOnUIThread(() -> {
                    context.sendBroadcast(intent1);
                    context.sendBroadcast(intent);
                });
            }
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.htc.launcher");
        }
    }

    /**
     * Бейдж для кастомного Nova Launcker'а
     */
    public class NovaHomeBadger implements Badger {

        private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
        private static final String COUNT = "count";
        private static final String TAG = "tag";

        @Override
        public void executeBadge(int badgeCount) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG, componentName.getPackageName() + "/" + componentName.getClassName());
            contentValues.put(COUNT, badgeCount);
            context.getContentResolver().insert(Uri.parse(CONTENT_URI), contentValues);
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.teslacoilsw.launcher");
        }
    }

    /**
     * Бейдж для Launcker'а устройств OPPO
     */
    public class OPPOHomeBader implements Badger {

        private static final String PROVIDER_CONTENT_URI = "content://com.android.badge/badge";
        /*private static final String INTENT_ACTION = "com.oppo.unsettledevent";
        private static final String INTENT_EXTRA_PACKAGENAME = "pakeageName";
        private static final String INTENT_EXTRA_BADGE_COUNT = "number";
        private static final String INTENT_EXTRA_BADGE_UPGRADENUMBER = "upgradeNumber";*/
        private static final String INTENT_EXTRA_BADGEUPGRADE_COUNT = "app_badge_count";
        private int mCurrentTotalCount = -1;

        private OPPOHomeBader() {
        }

        @Override
        public void executeBadge(int badgeCount) {
            if (mCurrentTotalCount == badgeCount) {
                return;
            }
            mCurrentTotalCount = badgeCount;
            executeBadgeByContentProvider(badgeCount);
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.oppo.launcher");
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void executeBadgeByContentProvider(int badgeCount) {
            try {
                Bundle extras = new Bundle();
                extras.putInt(INTENT_EXTRA_BADGEUPGRADE_COUNT, badgeCount);
                context.getContentResolver().call(Uri.parse(PROVIDER_CONTENT_URI), "setAppBadgeCount", null, extras);
            } catch (Throwable ignored) {

            }
        }
    }

    /**
     * Бейдж для Launcker'а устройств Samsung
     */
    public class SamsungHomeBadger implements Badger {
        private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
        private final String[] CONTENT_PROJECTION = new String[]{"_id","class"};

        private DefaultBadger defaultBadger;

        @Override
        public void executeBadge(int badgeCount) {
            try {
                if (defaultBadger == null) {
                    defaultBadger = new DefaultBadger();
                }
                defaultBadger.executeBadge(badgeCount);
            } catch (Exception ignore) {

            }

            Uri mUri = Uri.parse(CONTENT_URI);
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(mUri, CONTENT_PROJECTION, "package=?", new String[]{componentName.getPackageName()}, null);
                if (cursor != null) {
                    String entryActivityName = componentName.getClassName();
                    boolean entryActivityExist = false;
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        ContentValues contentValues = getContentValues(componentName, badgeCount, false);
                        contentResolver.update(mUri, contentValues, "_id=?", new String[]{String.valueOf(id)});
                        if (entryActivityName.equals(cursor.getString(cursor.getColumnIndex("class")))) {
                            entryActivityExist = true;
                        }
                    }

                    if (!entryActivityExist) {
                        ContentValues contentValues = getContentValues(componentName, badgeCount, true);
                        contentResolver.insert(mUri, contentValues);
                    }
                }
            } finally {
                close(cursor);
            }
        }

        private ContentValues getContentValues(ComponentName componentName, int badgeCount, boolean isInsert) {
            ContentValues contentValues = new ContentValues();
            if (isInsert) {
                contentValues.put("package", componentName.getPackageName());
                contentValues.put("class", componentName.getClassName());
            }

            contentValues.put("badgecount", badgeCount);

            return contentValues;
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Arrays.asList(
                    "com.sec.android.app.launcher",
                    "com.sec.android.app.twlauncher"
            );
        }
    }

    /**
     * Бейдж для Launcker'а устройств Htc
     */
    public class SonyHomeBadger implements Badger {

        private static final String INTENT_ACTION = "com.sonyericsson.home.action.UPDATE_BADGE";
        private static final String INTENT_EXTRA_PACKAGE_NAME = "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME";
        private static final String INTENT_EXTRA_ACTIVITY_NAME = "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME";
        private static final String INTENT_EXTRA_MESSAGE = "com.sonyericsson.home.intent.extra.badge.MESSAGE";
        private static final String INTENT_EXTRA_SHOW_MESSAGE = "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE";

        private static final String PROVIDER_CONTENT_URI = "content://com.sonymobile.home.resourceprovider/badge";
        private static final String PROVIDER_COLUMNS_BADGE_COUNT = "badge_count";
        private static final String PROVIDER_COLUMNS_PACKAGE_NAME = "package_name";
        private static final String PROVIDER_COLUMNS_ACTIVITY_NAME = "activity_name";
        private static final String SONY_HOME_PROVIDER_NAME = "com.sonymobile.home.resourceprovider";
        private final Uri BADGE_CONTENT_URI = Uri.parse(PROVIDER_CONTENT_URI);

        private AsyncQueryHandler mQueryHandler;

        @Override
        public void executeBadge(int badgeCount) {
            if (sonyBadgeContentProviderExists()) {
                executeBadgeByContentProvider(badgeCount);
            } else {
                executeBadgeByBroadcast(badgeCount);
            }
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Arrays.asList("com.sonyericsson.home", "com.sonymobile.home");
        }

        private  void executeBadgeByBroadcast(int badgeCount) {
            final Intent intent = new Intent(INTENT_ACTION);
            intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, componentName.getPackageName());
            intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, componentName.getClassName());
            intent.putExtra(INTENT_EXTRA_MESSAGE, String.valueOf(badgeCount));
            intent.putExtra(INTENT_EXTRA_SHOW_MESSAGE, badgeCount > 0);
            runOnUIThread(() -> context.sendBroadcast(intent));
        }

        private void executeBadgeByContentProvider(int badgeCount) {
            if (badgeCount < 0) {
                return;
            }

            if (mQueryHandler == null) {
                mQueryHandler = new SafeQueryHandler(context.getApplicationContext().getContentResolver());
            }
            insertBadgeAsync(badgeCount, componentName.getPackageName(), componentName.getClassName());
        }

        private void insertBadgeAsync(int badgeCount, String packageName, String activityName) {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(PROVIDER_COLUMNS_BADGE_COUNT, badgeCount);
            contentValues.put(PROVIDER_COLUMNS_PACKAGE_NAME, packageName);
            contentValues.put(PROVIDER_COLUMNS_ACTIVITY_NAME, activityName);
            mQueryHandler.startInsert(0, null, BADGE_CONTENT_URI, contentValues);
        }

        private  boolean sonyBadgeContentProviderExists() {
            boolean exists = false;
            ProviderInfo info = context.getPackageManager().resolveContentProvider(SONY_HOME_PROVIDER_NAME, 0);
            if (info != null) {
                exists = true;
            }
            return exists;
        }

    }

    private static class SafeQueryHandler extends AsyncQueryHandler {

        SafeQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
            } catch (Throwable ignore) {
            }
        }
    }

    /**
     * Бейджер для Launcker'а устройств Xiaomi
     */
    public class XiaomiHomeBadger implements Badger {

        private static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
        private static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
        private static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";

        @Override
        public void executeBadge(int badgeCount) {
            final String badgeCountString = badgeCount == 0 ? "" : String.valueOf(badgeCount);
            try {
                @SuppressWarnings("rawtypes")
                @SuppressLint("PrivateApi")
                Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
                Object miuiNotification = miuiNotificationClass.newInstance();
                Field field = miuiNotification.getClass().getDeclaredField("messageCount");
                field.setAccessible(true);
                field.set(miuiNotification, badgeCountString);
            } catch (Throwable e) {
                final Intent localIntent = new Intent(INTENT_ACTION);
                localIntent.putExtra(EXTRA_UPDATE_APP_COMPONENT_NAME, componentName.getPackageName() + "/" + componentName.getClassName());
                localIntent.putExtra(EXTRA_UPDATE_APP_MSG_TEXT, badgeCountString);
                if (canResolveBroadcast(localIntent)) {
                    runOnUIThread(() -> context.sendBroadcast(localIntent));
                }
            }
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Arrays.asList(
                    "com.miui.miuilite",
                    "com.miui.home",
                    "com.miui.miuihome",
                    "com.miui.miuihome2",
                    "com.miui.mihome",
                    "com.miui.mihome2"
            );
        }
    }

    /**
     * Бейджер для Launcker'а устройств Lenovo (ZUK Mobile)
     */
    public class ZukHomeBadger implements Badger {

        private final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void executeBadge(int badgeCount) {
            final Bundle extra = new Bundle();
            extra.putInt("app_badge_count", badgeCount);
            runOnUIThread(() -> {
                try {
                    context.getContentResolver().call(CONTENT_URI, "setAppBadgeCount", null, extra);
                } catch (Exception e) {
                    Timber.d(e);
                }
            });
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.zui.launcher");
        }
    }

    /**
     * Бейджер для Launcker'а устройств Vivo
     */
    public class VivoHomeBadger implements Badger {

        @Override
        public void executeBadge(int badgeCount) {
            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            intent.putExtra("className", componentName.getClassName());
            intent.putExtra("notificationNum", badgeCount);
            context.sendBroadcast(intent);
        }

        @Override
        public List<String> getSupportLaunchers() {
            return Collections.singletonList("com.vivo.launcher");
        }
    }

    {
        BADGERS.add(AdwHomeBadger.class);
        BADGERS.add(ApexHomeBadger.class);
        BADGERS.add(NewHtcHomeBadger.class);
        BADGERS.add(NovaHomeBadger.class);
        BADGERS.add(SonyHomeBadger.class);
        BADGERS.add(XiaomiHomeBadger.class);
        BADGERS.add(AsusHomeBadger.class);
        BADGERS.add(HuaweiHomeBadger.class);
        BADGERS.add(OPPOHomeBader.class);
        BADGERS.add(SamsungHomeBadger.class);
        BADGERS.add(ZukHomeBadger.class);
        BADGERS.add(VivoHomeBadger.class);
    }

    /**
     * Установка нового значения счетчика
     *
     * @param badgeCount актуальный счетчик
     * @return true если счетчик успешно обновлен
     */
    public boolean applyCount(int badgeCount) {
        try {
            if (badger == null && !initied) {
                initBadger();
                initied = true;
            }
            if (badger == null) {
                return false;
            }
            badger.executeBadge(badgeCount);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private void initBadger() {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            return;
        }

        componentName = launchIntent.getComponent();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo != null) {
            String currentHomePackage = resolveInfo.activityInfo.packageName;
            for (Class<? extends Badger> b : BADGERS) {
                Badger shortcutBadger = null;
                try {
                    Constructor<?> constr = b.getDeclaredConstructor(getClass());
                    constr.setAccessible(true);
                    shortcutBadger = (Badger) constr.newInstance(this);
                } catch (Exception ignored) {
                }
                if (shortcutBadger != null && shortcutBadger.getSupportLaunchers().contains(currentHomePackage)) {
                    badger = shortcutBadger;
                    break;
                }
            }
            if (badger != null) {
                return;
            }
        }

        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        //noinspection ConstantConditions
        if (resolveInfos != null) {
            for (int a = 0; a < resolveInfos.size(); a++) {
                resolveInfo = resolveInfos.get(a);
                String currentHomePackage = resolveInfo.activityInfo.packageName;

                for (Class<? extends Badger> b : BADGERS) {
                    Badger shortcutBadger = null;
                    try {
                        Constructor<?> constr = b.getDeclaredConstructor(getClass());
                        constr.setAccessible(true);
                        shortcutBadger = (Badger) constr.newInstance(this);
                    } catch (Exception ignored) {
                    }
                    if (shortcutBadger != null && shortcutBadger.getSupportLaunchers().contains(currentHomePackage)) {
                        badger = shortcutBadger;
                        break;
                    }
                }
                if (badger != null) {
                    break;
                }
            }
        }

        if (badger == null) {
            if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                badger = new XiaomiHomeBadger();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("ZUK")) {
                badger = new ZukHomeBadger();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
                badger = new OPPOHomeBader();
            } else if (Build.MANUFACTURER.equalsIgnoreCase("VIVO")) {
                badger = new VivoHomeBadger();
            } else {
                badger = new DefaultBadger();
            }
        }

    }

    @SuppressWarnings("ConstantConditions")
    private boolean canResolveBroadcast(Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }

    private void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    private void runOnUIThread(@NonNull Runnable runnable) {
        handler.post(runnable);
    }
}