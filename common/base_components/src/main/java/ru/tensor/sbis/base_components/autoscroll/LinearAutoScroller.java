package ru.tensor.sbis.base_components.autoscroll;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Реализация авто-скроллера для работы с {@link LinearLayoutManager}.
 *
 * @author am.boldinov
 */
public class LinearAutoScroller extends BaseAutoScroller {

    @NonNull
    private final LinearLayoutManager mLayoutManager;

    /**
     * Порог, после которого перестает работать авто-скрол.
     */
    private final int mThreshold;

    /**
     * Флаг, возможен ли автоскролл (с учетом состояния списка перед изменением данных).
     */
    private boolean mPossibleAutoScroll;

    @SuppressWarnings("unused")
    public LinearAutoScroller(@NonNull LinearLayoutManager layoutManager, int threshold) {
        mLayoutManager = layoutManager;
        mThreshold = threshold;
    }

    public LinearAutoScroller(@NonNull LinearLayoutManager layoutManager, int threshold, @NonNull Matcher matcher) {
        super(matcher);
        mLayoutManager = layoutManager;
        mThreshold = threshold;
    }

    @Override
    public void onBeforeContentChanged() {
        int firstVisibleBefore = mLayoutManager.findFirstVisibleItemPosition();
        mPossibleAutoScroll = firstVisibleBefore < mThreshold;
    }

    @Override
    public void onAfterContentChanged(boolean firstChanged) {
        if (mPossibleAutoScroll && firstChanged) {
            int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
            if (firstVisible != -1 && firstVisible < mThreshold) {
                // Если виден элемент раньше порогового, выполняем авто-скролл в начало списка
                scrollToTop();
            }
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onContentRangeInserted(int position, int count) {
        if (position == 0) {
            mPossibleAutoScroll = true;
            onAfterContentChanged(true);
        }
    }

    protected void scrollToTop() {
        mLayoutManager.scrollToPosition(0);
    }
}
