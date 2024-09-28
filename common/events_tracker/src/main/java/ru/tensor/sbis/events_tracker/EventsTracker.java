package ru.tensor.sbis.events_tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Stack;

import kotlin.Deprecated;
import ru.tensor.sbis.plugin_struct.feature.Feature;
import ru.tensor.sbis.statistic.StatisticService;
import ru.tensor.sbis.statistic.model.StatisticEvent;
import timber.log.Timber;

/**
 * Трекер событий.
 *
 * @author kv.martyshenko
 */
public class EventsTracker implements Feature {

    private static final String EMPTY = "";

    private static final String ITEM_DATA = "data: ";

    private static final String SCREEN_NAME = "Screen_Name";

    private static final String LOGIN = "onLogin";
    private static final String LOGOUT = "onLogout";

    private static final String ITEM_MEMORY_ON_DISK = "memory_on_disk";

    private final Stack<String> mScreens = new Stack<>();

    @Nullable
    private final FirebaseCrashlytics crashlytics;

    public EventsTracker(@NonNull Context context) {
        FirebaseCrashlytics fbCrashlytics = null;
        try {
            fbCrashlytics = FirebaseCrashlytics.getInstance();
        } catch (Exception e) {
            Timber.i(e, "Fail initialize Crashlytics");
        }
        crashlytics = fbCrashlytics;
    }

    /**
     * Уведомить об успешном логине.
     */
    public synchronized void onLogin() {
        logMessage(LOGIN);
    }

    /**
     * Уведомить об успешном разлогине.
     */
    public synchronized void onLogout() {
        logMessage(LOGOUT + " " + screensStack());
    }

    /**
     * Установить информацию о текущем пользователе.
     *
     * @param userId идентификатор пользователя.
     * @param name имя пользователя.
     */
    @Deprecated(message = "Не нужно записывать чувствительную информацию о пользователе")
    public synchronized void setUser(@NonNull String userId, @NonNull String name) {
    }

    /**
     * Затрекать текущий экран.
     *
     * @param activity
     * @param screenName название экрана.
     */
    public synchronized void trackScreen(@NonNull Activity activity,
                                         @NonNull String screenName) {
        logMessage(SCREEN_NAME + " " + screenName + "(" + activity.getClass().getCanonicalName() + ")");
    }

    /**
     * Заисать информацию о появлени экрана.
     *
     * @param screenName название экрана.
     */
    public synchronized void pushScreen(@NonNull String screenName) {
        mScreens.push(screenName);
    }

    /**
     * Заисать информацию о выходе с экрана.
     *
     * @param screenName название экрана.
     */
    public synchronized void popScreen(@NonNull String screenName) {
        if (mScreens.size() > 0
                && mScreens.search(screenName) >= 0) {
            mScreens.pop();
        }
    }

    /**
     * Заисать информацию о событии.
     *
     * @param tag тег
     * @param message сообщение
     */
    @Deprecated(message = "Используйте напрямую StatisticService")
    public synchronized void dispatch(@NonNull String tag, @NonNull String message) {
        // отправка события должна быть безопасной
        try {
            reportEvent(tag, message, true);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Отправляет в аналитику данные по занимаемому месту на диске.
     *
     * @memory представление занимаемого места на диске, должно быть не равно пустой строке, иначе бросает ислючение [IllegalStateException]
     */
    @SuppressLint("DefaultLocale")
    public synchronized void reportDiskSpaceUsage(@NonNull String diskSpace) throws IllegalStateException {
        if ("".equals(diskSpace)) {
            throw new IllegalStateException("Не корректные данные об объеме памяти, занимаемым приложением на диске");
        }
        reportEvent(
                ITEM_MEMORY_ON_DISK,
                diskSpace,
                false
        );
    }

    private String screensStack() {
        String screensStr = EMPTY;
        for (String screen : mScreens) {
            if (!TextUtils.isEmpty(screensStr)) {
                screensStr += ">";
            }
            screensStr += screen;
        }
        return screensStr;
    }

    private void reportEvent(
            @NonNull String event,
            @NonNull String data,
            boolean addScreenStack
    ) {
        sendCrashInfo(event, data, addScreenStack);
        sendStatistic(event, data, addScreenStack);
    }

    private void sendStatistic(
            @NonNull String event,
            @NonNull String data,
            boolean addScreenStack
    ) {
        StatisticEvent statisticEvent = new StatisticEvent(
                "EventsTracker",
                event,
                data
        );
        StatisticService.INSTANCE.report(statisticEvent);
    }

    private void sendCrashInfo(
            @NonNull String event,
            @NonNull String data,
            boolean addScreenStack
    ) {
        StringBuilder buff = new StringBuilder();

        buff.append(event).append("\n");
        buff.append(ITEM_DATA).append(data).append("\n");
        if (addScreenStack) {
            buff.append(screensStack()).append("\n");
        }

        logMessage(buff.toString());
    }

    private synchronized void logMessage(@NonNull String msg) {
        if (crashlytics != null) {
            crashlytics.log(msg);
        }
    }
}