package ru.tensor.sbis.pushnotification.buffer;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.pushnotification.util.PushLogger;

/**
 * The buffer that allows to specify delivery time through {@link FixedDeliveryTimeBuffer#deliverIn(long)} and
 * {@link FixedDeliveryTimeBuffer#deliverAfter(long)} methods. If delivery time not specified or passed, than
 * items delivers instantly.
 *
 * @param <T> - type of items in buffer
 * @author am.boldinov
 */
public class FixedDeliveryTimeBuffer<T> extends Buffer<T> {

    @Nullable
    private volatile DeliveryThread mThread;

    private long mDeliveryTime = -1;

    public FixedDeliveryTimeBuffer() {
        this(null);
    }

    public FixedDeliveryTimeBuffer(@Nullable Receiver<T> receiver) {
        super(receiver);
    }

    /**
     * Specify the time at witch to deliver buffered items.
     *
     * @param time - timestamp of delivering
     */
    public void deliverIn(long time) {
        mDeliveryTime = time;
        long delay = mDeliveryTime - System.currentTimeMillis();
        postDelivering(delay);
    }

    /**
     * Specify the delay after witch to deliver buffered items.
     *
     * @param delay - delay of delivering in ms
     */
    public void deliverAfter(long delay) {
        mDeliveryTime = System.currentTimeMillis() + delay;
        PushLogger.event("FixedDeliveryTimeBuffer deliverAfter " + delay + "ms at mDeliveryTime " + mDeliveryTime);
        postDelivering(delay);
    }

    /**
     * Push list to end of buffered sequence and deliver immediately.
     * 
     * @param item - item for pushing
     */
    public void deliverNow(@NonNull T item) {
        PushLogger.event("FixedDeliveryTimeBuffer deliverNow");
        super.push(item);
        deliver();
    }
    
    @Override
    public void push(@NonNull T item) {
        super.push(item);
        if (System.currentTimeMillis() >= mDeliveryTime) {
            getHandler().post(this::deliver);
        }
    }

    /**
     * Stop handler thread after delivering if buffer is empty.
     */
    @Override
    public void deliver() {
        super.deliver();
        if (isEmpty()) {
            stopThread();
        }
    }

    /**
     * Post recursive delivering with check.
     *
     * @param delay - delay of checking
     */
    private void postDelivering(long delay) {
        getHandler().postDelayed(() -> {
            if (System.currentTimeMillis() >= mDeliveryTime) {
                // It's time to deliver
                deliver();
            } else {
                // Delivery time was delayed
                final long remainingDelay = mDeliveryTime - System.currentTimeMillis();
                PushLogger.event("FixedDeliveryTimeBuffer postDelivering at remainingDelay = " + remainingDelay + "ms");
                postDelivering(remainingDelay);
            }
        }, delay);
    }

    @NonNull
    private Handler getHandler() {
        if (mThread == null) {
            synchronized (this) {
                if (mThread == null) {
                    DeliveryThread thread = new DeliveryThread();
                    PushLogger.event("FixedDeliveryTimeBuffer start new thread " + thread.getName());
                    thread.start();
                    mThread = thread;
                }
            }
        }
        //noinspection ConstantConditions
        return mThread.getHandler();
    }

    private void stopThread() {
        if (mThread != null) {
            synchronized (this) {
                if (mThread != null) {
                    //noinspection ConstantConditions
                    PushLogger.event("FixedDeliveryTimeBuffer quit thread " + mThread.getName());
                    //noinspection ConstantConditions
                    mThread.quit();
                    mThread = null;
                }
            }
        }
    }

    private static final class DeliveryThread extends HandlerThread {

        Handler mHandler;

        DeliveryThread() {
            super("Buffer's handler thread");
        }

        @NonNull
        Handler getHandler() {
            if (mHandler == null) {
                mHandler = new Handler(getLooper());
            }
            return mHandler;
        }
    }

}
