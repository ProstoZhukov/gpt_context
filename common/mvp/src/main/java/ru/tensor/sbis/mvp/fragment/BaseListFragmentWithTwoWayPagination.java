package ru.tensor.sbis.mvp.fragment;

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
import ru.tensor.sbis.common.util.RecyclerViewExt;
import ru.tensor.sbis.design.list_utils.AbstractListView;
import ru.tensor.sbis.design.text_span.SimpleInformationView;
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager;
import ru.tensor.sbis.mvp.presenter.BasePresenterFragment;
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter;
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView;

/**
 * NOTE: if yoi will modify this class please make this changes in {@link BaseListBottomSheetDialogFragmentWithTwoWayPagination} also!
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class BaseListFragmentWithTwoWayPagination
        <DM, A extends BaseTwoWayPaginationAdapter<DM>, V, P extends BaseTwoWayPaginationPresenter<V>>
        extends BasePresenterFragment<V, P> implements BaseTwoWayPaginationView<DM> {

    /**
     * Ключ для скролл позиции
     */
    public static final String SCROLL_POSITION_STATE = BaseListFragmentWithTwoWayPagination.class.getCanonicalName() + ".scroll_position_state";

    protected A mAdapter;

    @SuppressWarnings("rawtypes")
    @Nullable
    protected AbstractListView mSbisListView;

    /**
     * @deprecated в базовых классах нигде не используется, удалить проблемно по причине множественного обращения.
     * Рекомендуется в наследниках больше на него не ссылаться и удалить в связанных задачах.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Nullable
    protected PaginationLayoutManager mLayoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            if (mAdapter == null) {
                throw new IllegalStateException("You must inject adapter!");
            }
        }
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

    @SuppressWarnings("deprecation")
    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSbisListView != null) {
            mSbisListView.setAdapter(null);
            mSbisListView.setOnRefreshListener(null);
            mSbisListView.clearOnScrollListener();
            mSbisListView = null;
        }
        mLayoutManager = null;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnScrollListener = null;
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSbisListView != null) {
            int scrollPosition = RecyclerViewExt.findFirstCompletelyVisibleItemPosition(mSbisListView.getRecyclerView());
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
        if (mSbisListView != null) {
            mSbisListView.setOnRefreshListener(() -> mPresenter.onRefresh());
            mSbisListView.addOnScrollListener(mOnScrollListener);
        }
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mPresenter.onScroll(dy,
                    RecyclerViewExt.findFirstVisibleItemPosition(mSbisListView.getRecyclerView()),
                    RecyclerViewExt.findLastVisibleItemPosition(mSbisListView.getRecyclerView()),
                    recyclerView.computeVerticalScrollOffset()
            );
        }
    };

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
    public void notifyItemsChanged(int position, int count, @Nullable Object payLoad) {
        if (count > 1) {
            mAdapter.notifyItemRangeChanged(position, count);
        } else {
            if (payLoad != null) {
                mAdapter.notifyItemChanged(position, payLoad);
            } else {
                mAdapter.notifyItemChanged(position);
            }
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
        }
        hideInformationView();
    }

    @Override
    public void showLoadingError(@StringRes int errorTextResId) {
        if (isResumed()) { // показываем сообщение только если пользователь видит контент
            showToast(errorTextResId);
        }
    }

    @Override
    public void showLoadingError(@NonNull String errorText) {
        if (isResumed()) { // показываем сообщение только если пользователь видит контент
            showToast(errorText);
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (BuildConfig.DEBUG) {
            if (position < 0 || mAdapter.getItemCount() < position + 1) {
                throw new IllegalArgumentException("Position must be in the data list range." +
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
    @SuppressWarnings("unchecked")
    public void showMessageInEmptyView(@StringRes int messageTextId) {
        if (mSbisListView != null) {
            mSbisListView.showInformationViewData(createEmptyViewContent(messageTextId));
        }
    }

    @Override
    public void ignoreProgress(boolean ignore) {
        if (mSbisListView != null) {
            mSbisListView.ignoreProgress(ignore);
        }
    }

    /**
     * Сообщение с деталями в пустой вью
     */
    @SuppressWarnings("unchecked")
    public void showMessageInEmptyView(@StringRes int messageTextId, @StringRes int detailTextId) {
        if (mSbisListView != null) {
            mSbisListView.showInformationViewData(createEmptyViewContent(messageTextId, detailTextId));
        }
    }

    @NonNull
    protected Object createEmptyViewContent(@StringRes int messageTextId) {
        return new SimpleInformationView.Content(
                getContext(), 0, messageTextId, 0);
    }

    @NonNull
    protected Object createEmptyViewContent(String messageText) {
        return new SimpleInformationView.Content(null, messageText, null);
    }

    @NonNull
    protected Object createEmptyViewContent(@StringRes int messageTextId, @StringRes int detailTextId) {
        return new SimpleInformationView.Content(
                getContext(), messageTextId, 0, detailTextId);
    }

    protected abstract void initViews(@NonNull View mainView, @Nullable Bundle savedInstanceState);

    @LayoutRes
    protected abstract int getLayoutRes();

}
