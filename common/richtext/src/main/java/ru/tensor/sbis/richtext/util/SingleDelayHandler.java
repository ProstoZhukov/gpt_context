package ru.tensor.sbis.richtext.util;

import android.os.Handler;
import androidx.annotation.NonNull;
import android.os.Looper;

/**
 * Хендлер, который обрабатывает только одно(первое) сообщение за определенный промежуток времени.
 *
 * @author am.boldinov
 */
public class SingleDelayHandler {

    @NonNull
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private long mLastPostTime;

    /**
     * Публикует runnable сообщение в очередь в случае если прошло определенное время с момента предыдущей публикации
     */
    public void post(@NonNull Runnable runnable, long delayMillis) {
        if (System.currentTimeMillis() - mLastPostTime <= delayMillis) {
            return;
        }
        mLastPostTime = System.currentTimeMillis();
        mHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * Удаляет ожидающее runnable сообщение из очереди
     */
    public void removeCallbacks(@NonNull Runnable runnable) {
        mLastPostTime = 0;
        mHandler.removeCallbacks(runnable);
    }

    /**
     * Возвращает хендлер на котором выполняются операции
     */
    @NonNull
    public Handler getMainHandler() {
        return mHandler;
    }
}