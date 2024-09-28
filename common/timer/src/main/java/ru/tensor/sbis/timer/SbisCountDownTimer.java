package ru.tensor.sbis.timer;

import android.os.Bundle;
import android.os.CountDownTimer;

/**
 * @author am.boldinov
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class SbisCountDownTimer extends CountDownTimer {

    private boolean isRunning;
    private final SbisCountDownTimerListener mListener;
    private long mCurrentMillisUntilFinished;
    private Bundle mArguments;
    private String mCallerObject;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public SbisCountDownTimer(long millisInFuture, long countDownInterval, SbisCountDownTimerListener listener) {
        super(millisInFuture, countDownInterval);
        mListener = listener;
        mCurrentMillisUntilFinished = millisInFuture;
    }

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public SbisCountDownTimer(String callerObject, long millisInFuture, long countDownInterval, SbisCountDownTimerListener listener) {
        this(millisInFuture, countDownInterval, listener);
        mCallerObject = callerObject;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        isRunning = true;
        mCurrentMillisUntilFinished = millisUntilFinished;
        mListener.onTick(millisUntilFinished, mCallerObject);
    }

    @Override
    public void onFinish() {
        isRunning = false;
        mCurrentMillisUntilFinished = 0;
        mListener.onFinish(mCallerObject);
    }

    /** @SelfDocumented */
    public boolean isRunning() {
        return isRunning;
    }

    /** @SelfDocumented */
    public void resumeTimer() {
        mListener.onResume(mCurrentMillisUntilFinished, mCallerObject);
    }

    /** @SelfDocumented */
    public synchronized void startTimer() {
        start();
        isRunning = true;
        resumeTimer();
    }

    /** @SelfDocumented */
    public synchronized void cancelTimer() {
        cancel();
        isRunning = false;
    }

    /** @SelfDocumented */
    public Bundle getArguments() {
        return mArguments;
    }

    /**
     * @SelfDocumented
     * */
    public void setArguments(Bundle arguments) {
        mArguments = arguments;
    }

    /** @SelfDocumented */
    public long getCurrentMillis() {
        return mCurrentMillisUntilFinished;
    }


}
