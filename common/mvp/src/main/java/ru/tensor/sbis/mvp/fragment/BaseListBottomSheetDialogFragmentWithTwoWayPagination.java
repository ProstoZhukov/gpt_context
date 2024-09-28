package ru.tensor.sbis.mvp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter;
import ru.tensor.sbis.common.BuildConfig;
import ru.tensor.sbis.design.list_utils.SbisListView;
import ru.tensor.sbis.design.text_span.SimpleInformationView;
import ru.tensor.sbis.design_notification.SbisPopupNotification;
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager;
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter;
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView;

/**
 * NOTE: if yoi will modify this class please make this changes in {@link BaseListFragmentWithTwoWayPagination} also!
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@SuppressWarnings({"unused", "RedundantSuppression", "deprecation"})
@Deprecated
public abstract class BaseListBottomSheetDialogFragmentWithTwoWayPagination
        <DM, A extends BaseTwoWayPaginationAdapter<DM>, V, P extends BaseTwoWayPaginationPresenter<V>>
        extends BottomSheetDialogPresenterFragment<V, P> implements BaseTwoWayPaginationView<DM> {

    public static final String SCROLL_POSITION_STATE = BaseListBottomSheetDialogFragmentWithTwoWayPagination.class.getCanonicalName() + ".scroll_position_state";

    protected A mAdapter;
    @Nullable
    protected SbisListView mSbisListView;
    @Nullable
    protected PaginationLayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            if (mAdapter == null) {
                throw new IllegalStateException("You must inject adapter!");
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(getLayoutRes(), container, false);
        initViews(mainView, savedInstanceState);
        initViewListeners();
        return mainView;
    }

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
        mPresenter.viewIsStarted();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.viewIsResumed();
    }

    @Override
    public void onPause() {
        mPresenter.viewIsPaused();
        super.onPause();
    }

    @CallSuper
    @Override
    public void onStop() {
        mPresenter.viewIsStopped();
        super.onStop();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSbisListView != null) {
            mSbisListView.setAdapter(null);
        }
    }

    @SuppressWarnings("NullableProblems")
    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSbisListView != null && mLayoutManager != null) {
            int scrollPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (scrollPosition == 0) {
                scrollPosition = -1;
            }
            outState.putInt(SCROLL_POSITION_STATE, scrollPosition);
        }
    }

    @CallSuper
    protected void restoreFromBundle(@NonNull Bundle savedInstanceState) {
        int scrollSavedPosition = savedInstanceState.getInt(SCROLL_POSITION_STATE, -1);
        if (scrollSavedPosition > 0 && mSbisListView != null) {
            mSbisListView.scrollToPosition(scrollSavedPosition);
        }
    }

    protected void initViewListeners() {
        final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @SuppressWarnings("NullableProblems")
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPresenter.onScroll(dy,
                        mLayoutManager.findFirstVisibleItemPosition(),
                        mLayoutManager.findLastVisibleItemPosition(),
                        recyclerView.computeVerticalScrollOffset()
                );
            }
        };
        if (mSbisListView != null) {
            mSbisListView.setOnRefreshListener(() -> mPresenter.onRefresh());
            mSbisListView.addOnScrollListener(onScrollListener);
        }
    }

    @Override
    public void updateListViewState() {
        if (mSbisListView != null) {
            mSbisListView.updateViewState();
        }
    }

    @Override
    public void updateDataList(List<DM> dataList, int offset) {
        mAdapter.setData(dataList, offset);
    }

    @Override
    public void updateDataListWithoutNotification(List<DM> dataList, int offset) {
        mAdapter.setDataWithoutNotify(dataList, offset);
    }

    @Override
    public void notifyItemsInserted(int position, int count) {
        if (count > 1) {
            mAdapter.notifyItemRangeInserted(position, count);
        } else {
            mAdapter.notifyItemInserted(position);
        }
    }

    @Override
    public void notifyItemsChanged(int position, int count) {
        notifyItemsChanged(position, count, null);
    }

    @Override
    public void notifyItemsChanged(int position, int count, @Nullable Object payload) {
        if (count > 1) {
            mAdapter.notifyItemRangeChanged(position, count);
        } else {
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void notifyItemsRemoved(int position, int count) {
        if (count > 1) {
            mAdapter.notifyItemRangeRemoved(position, count);
        } else {
            mAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showOlderLoadingProgress(boolean show) {
        mAdapter.showOlderLoadingProgress(show);
    }

    @Override
    public void showNewerLoadingProgress(boolean show) {
        mAdapter.showNewerLoadingProgress(show);
    }

    @Override
    public void showControls() {
        // do nothing here
        // override if needed to react on scroll
    }

    @Override
    public void hideControls() {
        // do nothing here
        // override if needed to react on scroll
    }

    @Override
    public void resetUiState() {
        showControls();
        if (mSbisListView != null) {
            mSbisListView.scrollToPosition(0);
            mSbisListView.hideInformationView();
        }
    }

    @Override
    public void showLoadingError(@StringRes int errorTextResId) {
        Context context = getContext();

        if (BuildConfig.DEBUG) {
            if (context == null) {
                throw new IllegalStateException("Don't call view's methods after" +
                        " it is detached from context");
            }
        }

        if (errorTextResId != 0) {
            SbisPopupNotification.pushToast(requireContext(), errorTextResId);
        }
    }

    @Override
    public void showLoadingError(@NonNull String errorText) {
        Context context = getContext();

        if (BuildConfig.DEBUG) {
            if (context == null) {
                throw new IllegalStateException("Don't call view's methods after" +
                        " it is detached from context");
            }
        }

        SbisPopupNotification.pushToast(requireContext(), errorText);
    }

    @Override
    public void scrollToPosition(int position) {
        if (BuildConfig.DEBUG) {
            if (position < 0 || mAdapter.getItemCount() < position + 1) {
                throw new IllegalArgumentException("Position mast be in the data list range." +
                        "Position: " + position + ", data list size: " + mAdapter.getItemCount());
            }
        }

        if (mSbisListView != null) {
            mSbisListView.scrollToPosition(position);
        }
    }

    //region BaseLoadingView
    @Override
    public void showLoading() {
        //ignore
    }

    @Override
    public void hideLoading() {
        if (mSbisListView != null) {
            mSbisListView.setRefreshing(false);
        }
    }
    //endregion

    @Override
    public void hideInformationView() {
        if (mSbisListView != null) {
            mSbisListView.hideInformationView();
        }
    }

    /**
     * Сообщение в пустой вью
     */
    public void showMessageInEmptyView(@StringRes int messageTextId) {
        if (mSbisListView != null) {
            SimpleInformationView.Content content = new SimpleInformationView.Content(
                    getContext(), 0, messageTextId, 0);
            mSbisListView.showInformationViewData(content);
        }
    }

    @Override
    public void ignoreProgress(boolean ignore) {
        mSbisListView.ignoreProgress(ignore);
    }

    /**
     * Сообщение с деталями в пустой вью
     */
    public void showMessageInEmptyView(@StringRes int messageTextId, @StringRes int detailTextId) {
        if (mSbisListView != null) {
            SimpleInformationView.Content content = new SimpleInformationView.Content(
                    getContext(), messageTextId, 0, detailTextId);
            mSbisListView.showInformationViewData(content);
        }
    }

    protected abstract void initViews(@NonNull View mainView, @Nullable Bundle savedInstanceState);

    @LayoutRes
    protected abstract int getLayoutRes();

}
