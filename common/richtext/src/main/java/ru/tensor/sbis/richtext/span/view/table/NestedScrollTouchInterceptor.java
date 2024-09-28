package ru.tensor.sbis.richtext.span.view.table;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Компонент для отслеживания событий скролла вложенного скроллируемого контента.
 * Вложенный контент захватывает события скролла по горизонтали, передает управление
 * скроллом назад родительскому {@link HorizontalScrollView} через повторный тач, то есть
 * в рамках одного события перемещения родительский элемент не скроллится.
 *
 * @author am.boldinov
 */
public final class NestedScrollTouchInterceptor {

    @NonNull
    private final HorizontalScrollView mScrollView;
    @Nullable
    private View mCapturedView;
    private float mLastMotionX;
    private boolean mIsBeingDragged;

    public NestedScrollTouchInterceptor(@NonNull HorizontalScrollView scrollView) {
        mScrollView = scrollView;
    }

    /**
     * Определяет необходимость перехвата событий для определения текущей скроллируемой View.
     *
     * @param event событие из {@link HorizontalScrollView#onInterceptTouchEvent(MotionEvent)}
     * @return true если событие было перехвачено и должно быть обработано самим {@link HorizontalScrollView},
     * false если событие будет перенаправлено во вложенный скроллируемый контент
     */
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        final float x = event.getX() + mScrollView.getScrollX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mIsBeingDragged = false;
                mCapturedView = null;
                final float y = event.getY();
                final View child = findChildViewUnder(x, y);
                if (child instanceof ViewGroup) {
                    final View captured = hitTestNestedScrollView((ViewGroup) child, x, y, leftOffset(child), topOffset(child));
                    if (captured != null && captured.getWidth() >= ((View) captured.getParent()).getWidth()) {
                        mCapturedView = captured;
                    }
                }
                mLastMotionX = x;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float xDiff = x - mLastMotionX;
                mLastMotionX = x;
                if (mIsBeingDragged || xDiff != 0 && mCapturedView != null && mCapturedView.canScrollHorizontally(-(int) xDiff)) {
                    mIsBeingDragged = true;
                    mCapturedView = null;
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                mIsBeingDragged = false;
                mCapturedView = null;
                break;
            }
        }
        return true;
    }

    @Nullable
    private View findChildViewUnder(float x, float y) {
        final View root = mScrollView.getChildAt(0);
        if (root instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                final View child = viewGroup.getChildAt(i);
                if (isChildViewUnder(child, x, y)) {
                    return child;
                }
            }
        } else if (root != null && isChildViewUnder(root, x, y)) {
            return root;
        }
        return null;
    }

    private boolean isChildViewUnder(@NonNull View child, float x, float y) {
        return x >= child.getLeft() + child.getTranslationX() && x <= child.getRight() + child.getTranslationX()
                && y >= child.getTop() + child.getTranslationY() && y <= child.getBottom() + child.getTranslationY();
    }

    @Nullable
    private static View hitTestNestedScrollView(@NonNull ViewGroup root, float eventX, float eventY,
                                                float leftOffset, float topOffset) {
        for (int i = 0; i < root.getChildCount(); i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                final float left = leftOffset + leftOffset(child);
                final float top = topOffset + topOffset(child);
                if (eventX >= left && eventX <= left + child.getWidth()
                        && eventY >= top && eventY <= top + child.getHeight()) {
                    if (child instanceof RecyclerView || child instanceof HorizontalScrollView) {
                        return child;
                    } else {
                        return hitTestNestedScrollView((ViewGroup) child, eventX, eventY, left, top);
                    }
                }
            }
        }
        return null;
    }

    private static float leftOffset(@NonNull View view) {
        return view.getLeft() + view.getTranslationX();
    }

    private static float topOffset(@NonNull View view) {
        return view.getTop() + view.getTranslationY();
    }
}
