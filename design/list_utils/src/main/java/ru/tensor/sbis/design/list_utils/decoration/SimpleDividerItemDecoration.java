package ru.tensor.sbis.design.list_utils.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import ru.tensor.sbis.design.R;

/**
 * Разделитель списка обычной полосой
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class SimpleDividerItemDecoration extends DividerItemDecorationWithInsets {
    private final Drawable mDivider;

    public SimpleDividerItemDecoration(@NonNull Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.list_item_separator);
    }

    public SimpleDividerItemDecoration(@NonNull Context context, int startInset, int endInset) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.list_item_separator);
        setStartInset(startInset);
        setEndInset(endInset);
    }

    public SimpleDividerItemDecoration(@NonNull Context context, @DrawableRes int drawableRes) {
        mDivider = ContextCompat.getDrawable(context, drawableRes);
    }

    /**
     * Draw any appropriate decorations into the Canvas supplied to the RecyclerView.
     * Any content drawn by this method will be drawn after the item views are drawn
     * and will thus appear over the views.
     *
     * @param c Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView.
     */
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        assertNegativeInsetValue();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = getStartInset(); i < childCount - getEndInset(); i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

}