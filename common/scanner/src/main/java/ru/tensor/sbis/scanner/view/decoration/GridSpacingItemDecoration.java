package ru.tensor.sbis.scanner.view.decoration;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * @author am.boldinov
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpanCount;
    private final int mSpacingInPx;

    public GridSpacingItemDecoration(int spanCount, int spacingInPx) {
        mSpanCount = spanCount;
        mSpacingInPx = spacingInPx;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % mSpanCount;

        outRect.left = mSpacingInPx - column * mSpacingInPx / mSpanCount;
        outRect.right = (column + 1) * mSpacingInPx / mSpanCount;

        if (position < mSpanCount) {
            outRect.top = mSpacingInPx;
        }
        outRect.bottom = mSpacingInPx;
    }
}
