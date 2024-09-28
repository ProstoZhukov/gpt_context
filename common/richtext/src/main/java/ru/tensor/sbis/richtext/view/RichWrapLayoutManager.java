package ru.tensor.sbis.richtext.view;

import android.view.View;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.view.strategy.LineCursor;
import ru.tensor.sbis.richtext.view.strategy.ViewLayout;
import ru.tensor.sbis.richtext.view.strategy.WrapLineStrategy;
import ru.tensor.sbis.richtext.view.worker.TextLineWrapWorker;
import ru.tensor.sbis.richtext.view.worker.WrapWorker;
import timber.log.Timber;

/**
 * Управляет процессом обтекания View-компонентов в богатом тексте.
 * Обтекание происходит прострочно слева-направо, таким образом все обтекаемые компоненты
 * образуют очередь.
 *
 * @author am.boldinov
 * @see WrapWorker
 */
class RichWrapLayoutManager {

    @NonNull
    private final List<WrapWorker> mWorkerQueue = new LinkedList<>();
    @NonNull
    private final LineCursor mLineCursor = new LineCursor();
    @NonNull
    private final RichViewAdapter mAdapter;

    RichWrapLayoutManager(@NonNull RichViewAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * Запускает процесс обтекания View-компонентов в тексте.
     * Процесс будет остановлен когда воркер обтекания каждого компонента
     * будет завершен {@link WrapWorker#isFinished()}
     */
    void wrap(@NonNull ViewLayout layout) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            addWorker(layout, i, true);
            while (!mWorkerQueue.isEmpty()) {
                final int line = mLineCursor.get();
                for (int j = 0; j < mWorkerQueue.size(); j++) {
                    final WrapWorker worker = mWorkerQueue.get(j);
                    worker.doWork(layout, mLineCursor);
                    if (worker.isFinished()) {
                        mWorkerQueue.remove(j);
                        j--;
                    }
                }
                i = wrapNext(layout, line, i);
                mLineCursor.moveToNext();
            }
            mLineCursor.close();
        }
    }

    private int wrapNext(@NonNull ViewLayout layout, int line, int index) {
        if (index < mAdapter.getItemCount() - 1) {
            final int next = layout.getText().getSpanStart(mAdapter.getItemInternal(index + 1));
            if (next < layout.getLineEnd(line)) {
                index++;
                final WrapWorker worker = addWorker(layout, index, false);
                if (worker != null) {
                    worker.doWork(layout, mLineCursor);
                    if (worker.isFinished()) {
                        mWorkerQueue.remove(worker);
                    }
                    index = wrapNext(layout, line, index);
                }
            }
        }
        return index;
    }

    @Nullable
    private WrapWorker addWorker(@NonNull ViewLayout layout, int index, boolean openLine) {
        final View view = mAdapter.getViewHolder(index).view;
        if (view.getVisibility() == View.GONE) {
            return null;
        }
        final ViewStubSpan viewSpan = mAdapter.getItemInternal(index);
        final int position = layout.getText().getSpanStart(viewSpan);
        final WrapLineStrategy wrapLineStrategy = viewSpan.getWrapLineStrategy();
        if (position == -1 || wrapLineStrategy == null) {
            Timber.e("RichViewLayout: viewSpan position not found in text, view will be hidden");
            view.setVisibility(View.GONE);
            return null;
        }
        final WrapWorker worker = new TextLineWrapWorker(viewSpan, view, position);
        if (openLine) {
            mLineCursor.open(layout.getLineForOffset(position));
        }
        mWorkerQueue.add(worker);
        return worker;
    }
}
