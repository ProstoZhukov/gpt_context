package ru.tensor.sbis.design.list_utils.decoration.offset;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.design.list_utils.decoration.Decoration;

import android.view.View;

/**
 * Базовая реализация отступов для декорации.
 *
 * @author sa.nikitin
 */
@SuppressWarnings({"JavaDoc", "unused"})
public class BaseOffsetProvider implements Decoration.ItemOffsetProvider {

    private int mLeft, mTop, mRight, mBottom;

    public BaseOffsetProvider() {
    }

    public BaseOffsetProvider(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    /** @SelfDocumented */
    public int getLeft() {
        return mLeft;
    }

    /** @SelfDocumented */
    public void setLeft(int left) {
        mLeft = left;
    }

    /** @SelfDocumented */
    public int getTop() {
        return mTop;
    }

    /** @SelfDocumented */
    public void setTop(int top) {
        mTop = top;
    }

    /** @SelfDocumented */
    public int getRight() {
        return mRight;
    }

    /** @SelfDocumented */
    public void setRight(int right) {
        mRight = right;
    }

    /** @SelfDocumented */
    public int getBottom() {
        return mBottom;
    }

    /** @SelfDocumented */
    public void setBottom(int bottom) {
        mBottom = bottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View itemView,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.set(mLeft, mTop, mRight, mBottom);
    }

}
