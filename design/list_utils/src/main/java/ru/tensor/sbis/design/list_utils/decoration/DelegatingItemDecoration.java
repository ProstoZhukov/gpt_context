package ru.tensor.sbis.design.list_utils.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Декорация для recycler view, делегирующая логику отрисовки для каждого элемента списка.
 *
 * @author am.boldinov
 */
@SuppressWarnings({"SameReturnValue", "NullableProblems", "unused"})
public abstract class DelegatingItemDecoration extends DividerItemDecorationWithInsets {

    /**
     * Получить высоту декорации.
     * @param view      - элемент, после которого будет декорация
     * @param parent    - recycler view, для которого рисуем декорацию
     * @return высота декорации в пикселах
     */
    protected abstract int getDecorationHeight(View view, RecyclerView parent);

    /**
     * Получить верхний отступ декорации.
     * @param view      - элемент, после которого будет декорация
     * @param parent    - recycler view, для которого рисуем декорацию
     * @return верхний отступ в пикселах
     */
    protected int getDecorationTop(View view, RecyclerView parent) {
        return 0;
    }

    /**
     * Получить делегата для отрисовки на {@link #onDraw(Canvas, RecyclerView, RecyclerView.State)}.
     * @param c         - canvas
     * @param parent    - recycler view, для которого рисуем декорацию
     * @param state     - состояние recycler view
     * @return объект, реализующий логику отрисовки или null, если на onDraw отрисовывать ничего не нужно
     */
    @Nullable
    protected DrawDelegate getOnDrawDelegate(Canvas c, RecyclerView parent, RecyclerView.State state) {
        return null;
    }

    /**
     * Получить делегата для отрисовки на {@link #onDrawOver(Canvas, RecyclerView, RecyclerView.State)}.
     * @param c         - canvas
     * @param parent    - recycler view, для которого рисуем декорацию
     * @param state     - состояние recycler view
     * @return объект, реализующий логику отрисовки или null, если на onDrawOver отрисовывать ничего не нужно
     */
    @Nullable
    protected DrawDelegate getOnDrawOverDelegate(Canvas c, RecyclerView parent, RecyclerView.State state) {
        return null;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int top = getDecorationTop(view, parent);
        final int bottom = top + getDecorationHeight(view, parent);
        outRect.set(0, top, 0, bottom);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final DrawDelegate delegate = getOnDrawDelegate(c, parent, state);
        if (delegate != null) {
            drawForEach(c, parent, delegate);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final DrawDelegate delegate = getOnDrawOverDelegate(c, parent, state);
        if (delegate != null) {
            drawForEach(c, parent, delegate);
        }
    }

    /**
     * Вызываем отрисовку для всех декораций с указанным делегатом.
     * @param c         - canvas
     * @param parent    - recycler view, для которого отрисовываются декорации
     * @param delegate  - делегат с логикой отрисовки
     */
    private void drawForEach(Canvas c, RecyclerView parent, @NonNull DrawDelegate delegate) {
        int left = parent.getLeft();
        int right = parent.getRight();
        int childCount = parent.getChildCount();
        for (int i = getStartInset(); i < childCount - getEndInset(); i++) {
            View prev = parent.getChildAt(i);
            View next = i < childCount - 1 ? parent.getChildAt(i + 1) : null;
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) prev.getLayoutParams();
            int top = params.bottomMargin + prev.getBottom() + getDecorationTop(prev, parent);
            int bottom = top + getDecorationHeight(next, parent);
            delegate.draw(c, prev, next, left, top, right, bottom);
        }
    }

    /**
     * Интерфейс объекта, определяющего логику отрисовки декорации.
     */
    protected interface DrawDelegate {
        void draw(Canvas canvas, @NonNull View prev, @Nullable View next, int left, int top, int right, int bottom);
    }

}
