package ru.tensor.sbis.design.list_utils.decoration;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by ss.buvaylink on 04.04.2016.
 */
@SuppressWarnings({"ConstantConditions", "RedundantClassCall"})
public class LinearSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpace;
    private boolean mDrawOnLast;

    public LinearSpaceItemDecoration(int space) {
        mSpace = space;
    }

    public LinearSpaceItemDecoration(int space, boolean drawOnLast) {
        this(space);
        mDrawOnLast = drawOnLast;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!LinearLayoutManager.class.isInstance(layoutManager)) {
            return;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        if (!mDrawOnLast && parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            return;
        }
        if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            outRect.bottom = mSpace;
        } else {
            outRect.right = mSpace;
        }
    }

}
