package ru.tensor.sbis.richtext.view.prefetch;

import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.Trace;
import android.view.View;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Шедулер для асинхронного инфлейта View.
 *
 * @author am.boldinov
 */
@MainThread
class InflateJobScheduler<V extends View> {

    private static final long MAX_IDLE_JOB_TIME_MILLIS = 12;
    private static final long MAX_BACKGROUND_JOB_TIME_MILLIS = 32;
    private static final long BACKGROUND_JOB_LAUNCH_PERIOD_MILLIS = MAX_BACKGROUND_JOB_TIME_MILLIS * 2;

    @NonNull
    private final Context mContext;
    @NonNull
    private final ViewInflater<V> mInflater;
    @NonNull
    private final OnFinishInflateCallback<V> mCallback;
    private final AtomicBoolean mIsRunning = new AtomicBoolean(false);
    @NonNull
    private final AtomicInteger mInflateCount = new AtomicInteger();
    @NonNull
    private final AtomicInteger mExpectCount = new AtomicInteger();
    @Nullable
    private ScheduledFuture<?> mScheduledFuture;

    @NonNull
    private final MessageQueue.IdleHandler mIdleHandler = () -> {
        submitInflateProcess(MAX_IDLE_JOB_TIME_MILLIS);
        return mIsRunning.get();
    };
    private final MessageQueue mMainQueue;

    @MainThread
    InflateJobScheduler(@NonNull Context context, @NonNull ViewInflater<V> inflater,
                        @NonNull OnFinishInflateCallback<V> callback) {
        mContext = context;
        mInflater = inflater;
        mCallback = callback;
        mMainQueue = Looper.myQueue();
    }

    /**
     * Запускает асинхронную операцию инфлейта View.
     * В случае повторного вызова не останавливает запущенную операцию и добавляет
     * необходимое количество к текущему.
     *
     * @param count количество View, которое необходимо создать
     */
    public void schedule(int count) {
        mExpectCount.addAndGet(count);
        if (mIsRunning.compareAndSet(false, true)) {
            mMainQueue.addIdleHandler(mIdleHandler);
            mScheduledFuture = RichTextExecutor.get().scheduleWithFixedDelay(
                    () -> submitInflateProcess(MAX_BACKGROUND_JOB_TIME_MILLIS),
                    0,
                    BACKGROUND_JOB_LAUNCH_PERIOD_MILLIS,
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Останавливает запущенную ранее операцию инлфейта View.
     */
    public void cancel() {
        if (mIsRunning.compareAndSet(true, false)) {
            mMainQueue.removeIdleHandler(mIdleHandler);
            if (mScheduledFuture != null) {
                mScheduledFuture.cancel(false);
                mScheduledFuture = null;
            }
            mExpectCount.set(0);
            mInflateCount.set(0);
        }
    }

    private void submitInflateProcess(long maxTimeMillis) {
        Trace.beginSection("InflateJobScheduler submitInflateProcess");
        long elapsed = 0;
        long last = 0;
        while (mIsRunning.get() && elapsed < maxTimeMillis * 1_000_000 - last) {
            final long start = System.nanoTime();
            Trace.beginSection("InflateJobScheduler inflate view");
            final V view = mInflater.inflate(mContext);
            Trace.endSection();
            if (mIsRunning.get()) {
                last = System.nanoTime() - start;
                elapsed += last;
                mCallback.onFinishInflate(view);
                if (mInflateCount.incrementAndGet() >= mExpectCount.get()) {
                    cancel();
                    break;
                }
            }
        }
        Trace.endSection();
    }

    public interface ViewInflater<V extends View> {

        @AnyThread
        @NonNull
        V inflate(@NonNull Context context);
    }

    public interface OnFinishInflateCallback<V extends View> {

        @AnyThread
        void onFinishInflate(@NonNull V view);
    }
}
