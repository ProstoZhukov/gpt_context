package ru.tensor.sbis.design.scroll_to_top;


import android.animation.Animator;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.RecyclerView;

import kotlin.Deprecated;
import ru.tensor.sbis.design.utils.AnimationUtil;

/**
 * Слушатель событий скролла RecyclerView для контроля видимости виджета
 * ScrollToTop, связанного с этим RecyclerView.
 *
 * @author du.bykov
 */
@Deprecated(message = "Отказываемся от ScrollToTopHelper и всего что с ним связано,для отлавливания событий использовать - ScrollToTopSubscriptionHolder")
public class ScrollToTopListener extends RecyclerView.OnScrollListener {

    private final ScrollToTop scrollToTop;
    private boolean scrollToTopVisible;

    public ScrollToTopListener(ScrollToTop scrollToTop) {
        this.scrollToTop = scrollToTop;
        scrollToTopVisible = scrollToTop.getVisibility() == View.VISIBLE;
    }

    /** Возвращает видимость виджета */
    @SuppressWarnings("unused")
    public boolean isScrollToTopVisible() {
        return scrollToTopVisible;
    }

    @Override
    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
        if ((dy < 0 && scrollToTop.getVisibility() == View.VISIBLE) || !recyclerView.canScrollVertically(-1)) {
            scrollToTopVisible = false;
            AnimationUtil.updateAlpha(scrollToTop, 1, 0, new AnimationUtil.SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    scrollToTop.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    scrollToTop.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    scrollToTop.setVisibility(View.VISIBLE);
                }
            });
        } else if (dy > 0 && scrollToTop.getVisibility() != View.VISIBLE) {
            scrollToTopVisible = true;
            scrollToTop.setVisibility(View.VISIBLE);
            AnimationUtil.updateAlpha(scrollToTop, 0, 1, null);
        } else {
            scrollToTopVisible = scrollToTop.getVisibility() == View.VISIBLE;
        }
    }
}
