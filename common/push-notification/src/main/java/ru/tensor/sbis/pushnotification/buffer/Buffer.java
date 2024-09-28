package ru.tensor.sbis.pushnotification.buffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.tensor.sbis.pushnotification.util.PushLogger;

/**
 * Temporary data storage.
 * Provides the ability to push and receive a sequence of items.
 *
 * @author am.boldinov
 */
public class Buffer<T> {

    /**
     * Buffered sequence receiver.
     *
     * @param <T> - type of items in buffer
     */
    public interface Receiver<T> {
        /**
         * Buffering finished callback. Call's when current buffering sequence interrupted.
         *
         * @param fromBuffer - buffered sequence of items
         */
        void receive(@NonNull List<T> fromBuffer);
    }

    /**
     * Buffered sequence receiver.
     */
    @Nullable
    private Receiver<T> mReceiver;

    /**
     * List of buffered items.
     */
    @NonNull
    private final CopyOnWriteArrayList<T> mBuffer = new CopyOnWriteArrayList<>();

    public Buffer() {
    }

    public Buffer(@Nullable Receiver<T> receiver) {
        this.mReceiver = receiver;
    }

    /**
     * Returns true if there is no buffered items, false elsewhere.
     */
    public boolean isEmpty() {
        return mBuffer.size() == 0;
    }

    /**
     * Push list to end of buffered sequence.
     *
     * @param item - item for pushing
     */
    public void push(@NonNull T item) {
        mBuffer.add(item);
    }

    /**
     * Retrieve sequence of items from buffer.
     *
     * @return - retrieved sequence of items
     */
    @NonNull
    public List<T> retrieve() {
        final List<T> retrieved = new ArrayList<>(mBuffer);
        mBuffer.clear();
        return retrieved;
    }

    /**
     * Deliver buffered sequence to receiver.
     */
    public void deliver() {
        deliverWithAdditive(null);
    }

    /**
     * Deliver buffered sequence with additive to receiver.
     *
     * @param additive - additional items
     */
    @SuppressWarnings("SameParameterValue")
    protected void deliverWithAdditive(@Nullable List<T> additive) {
        final List<T> buffered = new ArrayList<>(mBuffer);
        PushLogger.event("Buffer deliverWithAdditive, clear buffer");
        mBuffer.clear();
        if (additive != null && additive.size() > 0) {
            buffered.addAll(additive);
        }
        if (mReceiver != null && buffered.size() > 0) {
            mReceiver.receive(buffered);
        }
    }

}
