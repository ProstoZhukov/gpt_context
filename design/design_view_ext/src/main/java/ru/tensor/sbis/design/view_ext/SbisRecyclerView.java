package ru.tensor.sbis.design.view_ext;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vitalydemidov on 14/01/16.
 */
public class SbisRecyclerView extends RecyclerView {

    @Nullable
    private View mEmptyView;

    @NonNull
    private final AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            refreshUiIfNeeded();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            refreshUiIfNeeded();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            refreshUiIfNeeded();
        }
    };


    public SbisRecyclerView(Context context) {
        super(context);
    }

    public SbisRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SbisRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressWarnings("unused")
    public void setEmptyView(@NonNull View emptyView) {
        mEmptyView = emptyView;
        refreshUiIfNeeded();
    }

    private void refreshUiIfNeeded() {
        if (getAdapter() != null && mEmptyView != null) {
            final boolean isEmpty = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(isEmpty ? VISIBLE : GONE);
            setVisibility(isEmpty ? GONE : VISIBLE);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }

        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }

        refreshUiIfNeeded();
    }
}
