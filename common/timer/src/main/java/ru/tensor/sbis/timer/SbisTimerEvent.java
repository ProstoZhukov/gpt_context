package ru.tensor.sbis.timer;

/**
 * Событие таймера
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class SbisTimerEvent {

    private final Event mEvent;
    private final long mMillisUntilFinished;
    private final String mCallerObjectName;

    /**
     * Событие
     */
    public enum Event {
        TICK,
        RESUME,
        FINISH
    }

    /** @SelfDocumented */
    public SbisTimerEvent(Event event, long millisUntilFinished, String callerObjectName) {
        mEvent = event;
        mMillisUntilFinished = millisUntilFinished;
        mCallerObjectName = callerObjectName;
    }

    /** @SelfDocumented */
    public Event getEvent() {
        return mEvent;
    }

    /** @SelfDocumented */
    public long getMillisUntilFinished() {
        return mMillisUntilFinished;
    }

    /** @SelfDocumented */
    public String getCallerObjectName() {
        return mCallerObjectName;
    }

}
