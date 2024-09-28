package ru.tensor.sbis.richtext.view.prefetch;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import androidx.annotation.NonNull;
import io.reactivex.internal.schedulers.RxThreadFactory;

/**
 * Класс для хранения пула потоков, на которых выполняются операции по вычислению богатого текста
 * и инфлейту View-компонентов.
 *
 * @author am.boldinov
 */
class RichTextExecutor {

    private static final class ExecutorHolder {
        @NonNull
        private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new RxThreadFactory("RichTextPrecomputedThread"));
    }

    @NonNull
    public static ScheduledExecutorService get() {
        return ExecutorHolder.executor;
    }
}
