package ru.tensor.sbis.timer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.tensor.sbis.event_bus.EventBusUtilsKt;

/**
 * Представляет собой сервис, уведомляющий о событиях таймера через EventBus.
 * @author am.boldinov
 */
public class SbisTimerService extends Service {

    public static final String EXTRA_TIMER_CALLER_OBJECT = "extra_sbis_timer_caller_object";
    public static final String EXTRA_TIMER_MILLIS_FUTURE = "extra_sbis_timer_millis";
    public static final String EXTRA_TIMER_MILLIS_INTERVAL = "extra_sbis_timer_interval";
    public static final String EXTRA_TIMER_NEED_RESTART = "extra_sbis_timer_need_restart";
    public static final long TIMER_DEFAULT_MILLIS_FUTURE = 59000;
    public static final long TIMER_DEFAULT_MILLIS_INTERVAL = 1000;

    private final IBinder mTimerBinder = new TimerBinder();
    private final Map<String, SbisCountDownTimer> mTimerMap = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mTimerBinder;
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        configure(intent);
        return START_NOT_STICKY;
    }

    private void configure(@NonNull Intent intent) {
        final long timerMillis = intent.getLongExtra(EXTRA_TIMER_MILLIS_FUTURE, TIMER_DEFAULT_MILLIS_FUTURE);
        final long countDownInterval = intent.getLongExtra(EXTRA_TIMER_MILLIS_INTERVAL, TIMER_DEFAULT_MILLIS_INTERVAL);
        final String callerObjectString = intent.getStringExtra(EXTRA_TIMER_CALLER_OBJECT);
        final boolean needRestart = intent.getBooleanExtra(EXTRA_TIMER_NEED_RESTART, false);
        final SbisCountDownTimer oldTimer = mTimerMap.get(callerObjectString);
        if (needRestart && oldTimer != null && oldTimer.isRunning()) {
            oldTimer.cancelTimer();
            mTimerMap.remove(callerObjectString);
        }
        if (oldTimer == null || !oldTimer.isRunning()) {
            SbisCountDownTimer sbisCountDownTimer = new SbisCountDownTimer(callerObjectString, timerMillis, countDownInterval, new SbisCountDownTimerListener() {
                @Override
                public void onTick(long millisUntilFinished, String callerObject) {
                    SbisTimerEvent event = new SbisTimerEvent(SbisTimerEvent.Event.TICK, millisUntilFinished, callerObject);
                    EventBusUtilsKt.postEventOnEventBusScope(event);
                }

                @Override
                public void onFinish(String callerObject) {
                    SbisTimerEvent event = new SbisTimerEvent(SbisTimerEvent.Event.FINISH, 0, callerObject);
                    EventBusUtilsKt.postEventOnEventBusScope(event);
                    mTimerMap.remove(callerObject);
                    if (mTimerMap.size() == 0) {
                        stopSelf();
                    }
                }

                @Override
                public void onResume(long millisUntilFinished, String callerObject) {
                    SbisTimerEvent event = new SbisTimerEvent(SbisTimerEvent.Event.RESUME, millisUntilFinished, callerObject);
                    EventBusUtilsKt.postEventOnEventBusScope(event);
                }
            });
            mTimerMap.put(callerObjectString, sbisCountDownTimer);
            sbisCountDownTimer.startTimer();
        } else {
            oldTimer.resumeTimer();
        }
    }

    /** @SelfDocumented */
    public boolean isTimerRunning(String callerObject) {
        SbisCountDownTimer timer = mTimerMap.get(callerObject);
        return timer != null && timer.isRunning();
    }

    /** @SelfDocumented */
    public void cancelTimer(String callerObject) {
        SbisCountDownTimer timer = mTimerMap.get(callerObject);
        if (timer != null) {
            timer.cancelTimer();
        }
    }

    /** @SelfDocumented */
    public long getCurrentTimerMillis(String callerObject) {
        SbisCountDownTimer timer = mTimerMap.get(callerObject);
        return timer != null ? timer.getCurrentMillis() : 0;
    }

    /** @SelfDocumented */
    public class TimerBinder extends Binder {

        @NonNull
        public SbisTimerService getService() {
            return SbisTimerService.this;
        }
    }
}
