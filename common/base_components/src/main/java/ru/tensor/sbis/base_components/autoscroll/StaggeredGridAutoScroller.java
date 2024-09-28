package ru.tensor.sbis.base_components.autoscroll;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Реализация авто-скроллера для работы с {@link StaggeredGridLayoutManager}.
 *
 * @author am.boldinov
 */
public class StaggeredGridAutoScroller extends BaseAutoScroller {

    @NonNull
    private final StaggeredGridLayoutManager mLayoutManager;

    /**
     * Порог, после которого перестает работать авто-скрол.
     */
    private final int mThreshold;

    /**
     * Флаг, возможен ли автоскролл (с учетом состояния списка перед изменением данных).
     */
    private boolean mPossibleAutoScroll;

    @SuppressWarnings("unused")
    public StaggeredGridAutoScroller(@NonNull StaggeredGridLayoutManager layoutManager, int threshold) {
        mLayoutManager = layoutManager;
        mThreshold = threshold;
    }

    public StaggeredGridAutoScroller(@NonNull StaggeredGridLayoutManager layoutManager, int threshold, @NonNull Matcher matcher) {
        super(matcher);
        mLayoutManager = layoutManager;
        mThreshold = threshold;
    }

    @Override
    public void onBeforeContentChanged() {
        int[] firstVisibleBefore = mLayoutManager.findFirstVisibleItemPositions(null);
        mPossibleAutoScroll = false;
        for (int before : firstVisibleBefore) {
            if (before < mThreshold) {
                mPossibleAutoScroll = true;
                break;
            }
        }
    }

    @Override
    public void onAfterContentChanged(boolean firstChanged) {
        if (mPossibleAutoScroll && firstChanged) {
            int[] firstVisible = mLayoutManager.findFirstVisibleItemPositions(null);
            for (int after : firstVisible) {
                if (after != -1 && after < mThreshold) {
                    // Если хотя бы в одной из колонок виден элемент раньше
                    // порогового, выполняем авто-скролл в начало списка
                    scrollToTop();
                    break;
                }
            }
        }
    }

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
