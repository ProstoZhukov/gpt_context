package ru.tensor.sbis.design.list_utils.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * @author am.boldinov
 */
@SuppressWarnings({"NullableProblems", "unused", "JavaDoc"})
public class FlexibleItemDecoration extends DividerItemDecorationWithInsets {

    private final Paint mPaint;
    private final int mAlpha;

    public FlexibleItemDecoration(@ColorInt int color, float width) {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        mAlpha = mPaint.getAlpha();
    }

    public FlexibleItemDecoration(@ColorInt int color, float width, int startInset, int endInset) {
        this(color, width);
        setStartInset(startInset);
        setEndInset(endInset);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int position = params.getViewAdapterPosition();
        if (position >= 0 && position < state.getItemCount() && needDrawDivider(parent, position)) {
            outRect.set(0, 0, 0, (int) mPaint.getStrokeWidth());
        } else {
            outRect.setEmpty();
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        assertNegativeInsetValue();
        final int offset = (int) (mPaint.getStrokeWidth() / 2);
        for (int i = getStartInset(); i < parent.getChildCount() - getEndInset(); i++) {
            final View view = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            final int position = params.getViewAdapterPosition();
            if (position >= 0 && position < state.getItemCount() && needDrawDivider(parent, position)) {
                mPaint.setAlpha((int) (view.getAlpha() * mAlpha));
                float positionY = view.getBottom() + offset + view.getTranslationY();
                c.drawLine(view.getLeft(),
                        positionY,
                        view.getRight(),
                        positionY,
                        mPaint);
            }
        }
    }

    /** @SelfDocumented */
    @SuppressWarnings("SameReturnValue")
    protected boolean needDrawDivider(RecyclerView parent, int position) {
        return true;
    }

}
