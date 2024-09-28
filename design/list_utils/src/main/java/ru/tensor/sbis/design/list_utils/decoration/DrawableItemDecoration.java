package ru.tensor.sbis.design.list_utils.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by aa.mironychev on 11.12.2017.
 *
 * @author am.boldinov
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class DrawableItemDecoration extends DividerItemDecorationWithInsets {

    private final Drawable mDrawable;
    private final Paint mBackgroundPaint;
    private final int mAlpha;

    public DrawableItemDecoration(@NonNull Context context, @DrawableRes int decorationDrawableResId) {
        this(context, decorationDrawableResId, null);
    }

    public DrawableItemDecoration(@NonNull Context context, @DrawableRes int decorationDrawableResId, @Nullable Integer backgroundColor) {
        mDrawable = ContextCompat.getDrawable(context, decorationDrawableResId);
        mBackgroundPaint = new Paint();
        if (backgroundColor != null) {
            mBackgroundPaint.setColor(backgroundColor);
        }
        mAlpha = mBackgroundPaint.getAlpha();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDrawable.getIntrinsicHeight());
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mBackgroundPaint != null) {
            drawForEach(c, parent,
                    (canvas, child, left, top, right, bottom) -> {
                        mBackgroundPaint.setAlpha((int) (child.getAlpha() * mAlpha));
                        canvas.drawRect(left, top, right, bottom, mBackgroundPaint);
                    }
            );
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawForEach(c, parent,
                (canvas, child, left, top, right, bottom) -> {
                    mDrawable.setBounds(left, top, right, bottom);
                    mDrawable.setAlpha((int) (child.getAlpha() * 255));
                    mDrawable.draw(canvas);
                }
        );
    }

    private void drawForEach(Canvas c, RecyclerView parent, @NonNull DrawDelegate delegate) {
        int left = parent.getLeft();
        int right = parent.getRight();
        int childCount = parent.getChildCount();
        for (int i = getStartInset(); i < childCount - getEndInset(); i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDrawable.getIntrinsicHeight();
            delegate.draw(c, child, left, top, right, bottom);
        }
    }

    /**
     * Draw делегат
     */
    @SuppressWarnings("JavaDoc")
    protected interface DrawDelegate {
        /** @SelfDocumented */
        void draw(Canvas canvas, View child, int left, int top, int right, int bottom);
    }

}
