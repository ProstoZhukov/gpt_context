package ru.tensor.sbis.richtext.view.prefetch;

import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.objectpool.base.ConcurrentObjectPool;
import ru.tensor.sbis.richtext.view.RichViewLayout;

/**
 * ViewPool для автоматического предсоздания и хранения экземлпяров {@link RichViewLayout}
 * для последующего использования в качестве дочерних вью-холдеров богатого текста (ячеек таблиц, цитат и т.п.).
 *
 * @author am.boldinov
 */
@MainThread
public final class PrecomputedRichViewPool extends ConcurrentObjectPool<RichViewLayout> {

    @Nullable
    private InflateJobScheduler<RichViewLayout> mInflateScheduler;

    public PrecomputedRichViewPool(int capacity) {
        super(capacity);
    }

    /**
     * Присоединяет пул к контексту экрана и запускает автоматическое наполнение пула в фоне.
     *
     * @param context темизированный ui контекст
     */
    public void attach(@NonNull Context context) {
        detach();
        final int inflateCount = getCapacity() - getPoolSize();
        if (inflateCount > 0) {
            mInflateScheduler = new InflateJobScheduler<>(context, ctx -> {
                final RichViewLayout layout = new RichViewLayout(ctx);
                layout.setLayoutParams(new RichViewLayout.LayoutParams(RichViewLayout.LayoutParams.MATCH_PARENT, RichViewLayout.LayoutParams.WRAP_CONTENT));
                return layout;
            }, this::put);
            mInflateScheduler.schedule(inflateCount);
        }
    }

    /**
     * Отсоединяет пул от контекста и останавливает наполнение пула.
     */
    public void detach() {
        if (mInflateScheduler != null) {
            mInflateScheduler.cancel();
            mInflateScheduler = null;
        }
    }
}
