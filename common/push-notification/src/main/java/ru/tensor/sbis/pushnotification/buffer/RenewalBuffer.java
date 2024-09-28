package ru.tensor.sbis.pushnotification.buffer;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * The buffer that starting buffering with first push operation and waiting for {@link RenewalBuffer#mRenewalTime} millis
 * for next push operation. Every subsequent push renewal waiting period. After {@link RenewalBuffer#mRenewalTime}
 * since last push operation buffer will deliver buffered items to receiver.
 *
 * @author am.boldinov
 */
public class RenewalBuffer<T> extends FixedDeliveryTimeBuffer<T> {

    /**
     * Default renewal buffering time value.
     */
    private static final long DEFAULT_RENEWAL_TIME = 150;

    /**
     * Maximal waiting time before next push.
     */
    private long mRenewalTime = DEFAULT_RENEWAL_TIME;

    @NonNull
    private final AtomicBoolean mInitialBuffering = new AtomicBoolean();

    public RenewalBuffer() {
    }

    public RenewalBuffer(@Nullable Receiver<T> receiver) {
        super(receiver);
    }

    /**
     * Set waiting time for next push.
     *
     * @param renewalTime waiting time
     */
    public void setRenewalTime(long renewalTime) {
        mRenewalTime = renewalTime;
    }

    @Override
    public void push(@NonNull T item) {
        if (mInitialBuffering.compareAndSet(false, true)) {
            deliverNow(item);
        } else {
            deliverAfter(mRenewalTime);
            super.push(item);
        }
    }
}
