package ru.tensor.sbis.common_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.tensor.sbis.common.util.NetworkUtils;

/**
 * Компонент для отображения вертикально ориентированного списка папок
 *
 * @author ev.grigoreva
 */
public class RecyclerViewWithEmptyView extends RecyclerView {

    @Nullable
    protected View mEmptyView;
    @Nullable
    private View mNetworkErrorView;

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            updateEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            updateEmptyView();
        }
    };

    public RecyclerViewWithEmptyView(Context context) {
        super(context);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*** @SelfDocumented */
    public void setEmptyView(@Nullable View emptyView) {
        mEmptyView = emptyView;
    }

    /*** @SelfDocumented */
    public void setNetworkErrorView(@Nullable View networkErrorView) {
        mNetworkErrorView = networkErrorView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        super.setAdapter(adapter);
    }

    /*** @SelfDocumented */
    public void updateEmptyView() {
        if (mEmptyView == null) {
            setVisibility(getAdapter().getItemCount() > 0 ? VISIBLE : GONE);
        } else {
            if (getAdapter().getItemCount() > 0) {
                mEmptyView.setVisibility(GONE);
                setVisibility(VISIBLE);
            } else {
                mEmptyView.setVisibility(VISIBLE);
                setVisibility(GONE);
            }
            if (mNetworkErrorView != null) {
                mNetworkErrorView.setVisibility(getAdapter().getItemCount() == 0 && !NetworkUtils.isConnected(getContext()) ? VISIBLE : GONE);
            }
        }
    }
}
