package ru.tensor.sbis.design.view_ext.swipereveallayout;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * @deprecated необходимо использовать SwipeableViewBinderHelper из common
 */
@SuppressWarnings({"deprecation", "unused"})
@Deprecated
public class ViewBinderHelper {

    private static final String KEY_LAST_POSITION = "ViewBinderHelper.LAST_POSITION";
    private static final float SWIPE_OFFSET_THRESHOLD = 0.1f;
    private static final int NO_POSITION = -1;

    @Nullable
    private WeakReference<SwipeRevealLayout> mLastLayout;
    private int mLastPosition = NO_POSITION;

    public void bind(@NonNull final SwipeHolderInterface swipeHolder) {
        swipeHolder.getSwipeRevealLayout().setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                if (!view.isClosing() && slideOffset > SWIPE_OFFSET_THRESHOLD) {
                    SwipeRevealLayout lastLayout = getLastLayout();
                    if (lastLayout != view) {
                        if (lastLayout != null) {
                            lastLayout.closeCompletely(true);
                        }
                        mLastLayout = new WeakReference<>(view);
                        mLastPosition = swipeHolder.getAdapterPosition();
                    }
                }
            }

            @Override
            public void onClosed(SwipeRevealLayout view) {
                if (view == getLastLayout()) {
                    mLastLayout = null;
                    mLastPosition = NO_POSITION;
                }
            }
        });
    }

    @Nullable
    private SwipeRevealLayout getLastLayout() {
        return mLastLayout == null ? null : mLastLayout.get();
    }

    public int getLastPosition() {
        return mLastPosition;
    }

    public void checkState(@NonNull SwipeRevealLayout swipeLayout, int position, boolean lock) {
        if (swipeLayout.shouldRequestLayout()) {
            swipeLayout.requestLayout();
        }
        swipeLayout.abort();

        if (mLastPosition == position) {
            if (mLastLayout == null) {
                mLastLayout = new WeakReference<>(swipeLayout);
            }
            if (swipeLayout.isClosed()) {
                swipeLayout.open(false);
            }
        } else if (swipeLayout.isOpened()) {
            swipeLayout.closeCompletely(false);
        }

        swipeLayout.setLockDrag(lock);
    }

    public void closeLastLayout() {
        closeLastLayout(true);
    }

    public void closeLastLayout(boolean animation) {
        SwipeRevealLayout lastLayout = getLastLayout();
        if (lastLayout != null) {
            lastLayout.closeCompletely(animation);
        }
        mLastLayout = null;
        mLastPosition = NO_POSITION;
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mLastPosition = savedInstanceState.getInt(KEY_LAST_POSITION);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_LAST_POSITION, mLastPosition);
    }

    public abstract static class SwipeHolder extends RecyclerView.ViewHolder implements SwipeHolderInterface {

        @NonNull
        protected final SwipeRevealLayout revealLayout;

        public SwipeHolder(@NonNull View itemView, @NonNull ViewBinderHelper viewBinderHelper) {
            super(itemView);
            revealLayout = itemView.findViewById(getRevealLayoutId());
            viewBinderHelper.bind(this);
        }

        @NonNull
        @Override
        public SwipeRevealLayout getSwipeRevealLayout() {
            return revealLayout;
        }

        @Override
        public void checkState(@NonNull ViewBinderHelper viewBinderHelper, boolean lock) {
            viewBinderHelper.checkState(getSwipeRevealLayout(), getAdapterPosition(), lock);
        }

        @IdRes
        protected abstract int getRevealLayoutId();

    }
}