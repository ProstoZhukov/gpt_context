package ru.tensor.sbis.design.list_utils.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Реализация декорации элемента списка эффектом тени.
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public class ShadowItemDecoration extends DelegatingItemDecoration {

    /**
     * Тень над элементом.
     */
    private final Drawable mAboveShadowDrawable;

    /**
     * Тень под элементом.
     */
    private final Drawable mBelowShadowDrawable;

    @Nullable
    private int[] mIgnoredViewTypes;

    public ShadowItemDecoration(@NonNull Context context,
                                @DrawableRes int aboveShadowDrawableResId,
                                @DrawableRes int belowShadowDrawableResId) {
        mAboveShadowDrawable = ContextCompat.getDrawable(context, aboveShadowDrawableResId);
        mBelowShadowDrawable = ContextCompat.getDrawable(context, belowShadowDrawableResId);
    }

    public ShadowItemDecoration ignoreViewTypes(int... viewTypes) {
        mIgnoredViewTypes = viewTypes;
        return this;
    }

    @Override
    protected int getDecorationHeight(View view, RecyclerView parent) {
        return mAboveShadowDrawable.getIntrinsicHeight() + mBelowShadowDrawable.getIntrinsicHeight();
    }

    @Nullable
    @Override
    protected DrawDelegate getOnDrawOverDelegate(Canvas c, RecyclerView parent, RecyclerView.State state) {
        return (canvas, prev, next, left, top, right, bottom) -> {
            if (allowDrawShadow(parent, prev)) {
                drawBelowShadow(canvas, prev, left, top, right, bottom);
            }
            if (allowDrawShadow(parent, next)) {
                // Рисуем над-тень только если есть следующий элемент
                drawAboveShadow(canvas, next, left, top, right, bottom);
            }
        };
    }

    /**
     * Отрисовать тень под элементом.
     * @param canvas    - canvas
     * @param item      - элемент списка, под которым нужно отрисовать тень
     * @param left      - левый край области отрисовки
     * @param top       - верхний край области отрисовки
     * @param right     - правый край области отрисовки
     * @param bottom    - нижний край области отрисовки
     */
    private void drawBelowShadow(Canvas canvas, @NonNull View item, int left, int top, int right, int bottom) {
        final int translation = Math.round(item.getTranslationY());
        top += translation;
        bottom += translation - mAboveShadowDrawable.getIntrinsicHeight();
        mBelowShadowDrawable.setBounds(left, top, right, bottom);
        mBelowShadowDrawable.setAlpha(getAlpha(item));
        mBelowShadowDrawable.draw(canvas);
    }

    /**
     * Отрисовать тень над элементом.
     * @param canvas    - canvas
     * @param item      - элемент списка, над которым нужно отрисовать тень
     * @param left      - левый край области отрисовки
     * @param top       - верхний край области отрисовки
     * @param right     - правый край области отрисовки
     * @param bottom    - нижний край области отрисовки
     */
    private void drawAboveShadow(Canvas canvas, @NonNull View item, int left, int top, int right, int bottom) {
        if (preventDrawAbove(canvas, item, left, top, right, bottom)) {
            return;
        }
        final int translation = Math.round(item.getTranslationY());
        top += translation + mBelowShadowDrawable.getIntrinsicHeight();
        bottom += translation;
        mAboveShadowDrawable.setBounds(left, top, right, bottom);
        mAboveShadowDrawable.setAlpha(getAlpha(item));
        mAboveShadowDrawable.draw(canvas);
    }

    private boolean allowDrawShadow(@NonNull RecyclerView parent, @Nullable View view) {
        if (view == null) {
            return false;
        }
        if (mIgnoredViewTypes != null) {
            final RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);
            if (viewHolder != null) {
                for (int ignoredType : mIgnoredViewTypes) {
                    if (viewHolder.getItemViewType() == ignoredType) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Нужно ли заблокировать отрисовку тени над элементом. Переопределите этот метод,
     * если есть условия при которых необходимо заблокировать отрисовку декорации над элементом.
     *
     * @param canvas    - canvas
     * @param item      - элемент списка, над которым нужно отрисовать тень
     * @param left      - левый край области отрисовки
     * @param top       - верхний край области отрисовки
     * @param right     - правый край области отрисовки
     * @param bottom    - нижний край области отрисовки
     * @return true - если нужно заблокировать отрисовку декорации, false - иначе
     */
    @SuppressWarnings("SameReturnValue")
    protected boolean preventDrawAbove(@SuppressWarnings("unused") Canvas canvas, @NonNull View item, int left, int top, int right, int bottom) {
        return false;
    }

    private static int getAlpha(@Nullable View view) {
        if (view == null) {
            return 0;
        }
        return (int) (view.getAlpha() * 255);
    }

}
