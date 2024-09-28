package ru.tensor.sbis.mvp.interactor.crudinterface;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.functions.Function;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.scroll.ScrollEvent;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter;
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager;
import ru.tensor.sbis.mvp.search.SearchablePresenter;
import ru.tensor.sbis.mvp.search.SearchableView;
import timber.log.Timber;

/**
 * Базовый класс презентера для списка с поиском и двунаправленной подгрузкой страниц
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"JavaDoc", "SpellCheckingInspection", "unused", "RedundantSuppression"})
public abstract class SearchableListAbstractTwoWayPaginationPresenter
        <VIEW extends SearchableView<DataModel>, DataModel, FILTER extends ListFilter, QUERYFILTER>
        extends ListAbstractTwoWayPaginationPresenter<VIEW, DataModel, FILTER, QUERYFILTER>
        implements SearchablePresenter<VIEW> {

    @NonNull
    protected String mSearchQuery = "";

    protected boolean mKeyboardIsVisible;
    private boolean mIgnoreKeyboardEvents = true;
    protected boolean mFocusInFilterPanel;

    /**
     * Флаг для отложенного скролла списка вверх
     */
    private boolean mPendingScrollToTop = false;
    /**
     * Сигнализирует о том, что идет запрос после смены поисковой строки
     */
    private boolean mPendingSearchRequest;
    /**
     * Задержка при отображении прогрессбара загрузки данных
     */
    private int mProgressDelayMillis;

    @NonNull
    protected final ScrollHelper mScrollHelper;

    @NonNull
    private final SerialDisposable mScrollEventDisposable = new SerialDisposable();

    public SearchableListAbstractTwoWayPaginationPresenter(@NonNull FILTER filter,
                                                           @NonNull SubscriptionManager subscriptionManager,
                                                           @NonNull NetworkUtils networkUtils,
                                                           @NonNull ScrollHelper scrollHelper) {
        super(filter, subscriptionManager, networkUtils);
        initDefaultKeyboardVisibility();
        mScrollHelper = scrollHelper;
        mScrollEventDisposable.set(mScrollHelper.getScrollEventObservable()
                .subscribe(
                        scrollEvent -> {
                            if (mView != null) {
                                switch (scrollEvent) {
                                    case SCROLL_DOWN:
                                        mView.hideControls();
                                        break;

                                    case SCROLL_UP:
                                    case SCROLL_UP_FAKE:
                                        mView.showControls();
                                        break;
                                }
                            }
                        },
                        throwable -> Timber.d("Error in scroll event type observable %s", getClass().getSimpleName())
                )
        );
    }

    /**
     * @SelfDocumented
     */
    @Override
    public void setProgressDelay(int delayMillis) {
        mProgressDelayMillis = delayMillis;
    }

    protected void initDefaultKeyboardVisibility() {
        mKeyboardIsVisible = false;
    }

    //region Lifecycle
    @Override
    public void viewIsResumed() {
        super.viewIsResumed();
        mIgnoreKeyboardEvents = false;
        if (mView != null) {
            if (mKeyboardIsVisible) {
                mView.showKeyboard();
            }
        }
    }

    @Override
    public void viewIsPaused() {
        mIgnoreKeyboardEvents = true;
        if (mView != null && mKeyboardIsVisible) {
            // Just hide keyboard and do not change {@link #mKeyboardIsVisible}
            // flag to show keyboard when view will be resumed again.
            mView.hideKeyboard();
        }
        super.viewIsPaused();
    }

    @Override
    public void onDestroy() {
        mScrollEventDisposable.dispose();
        super.onDestroy();
    }
    //endregion Lifecycle

    //region TwoWayPagination implementation
    @Override
    public void onScroll(int dy, int firstVisibleItemPosition, int lastVisibleItemPosition, int computeVerticalScrollOffset) {
        super.onScroll(dy, firstVisibleItemPosition, lastVisibleItemPosition, computeVerticalScrollOffset);
        mScrollHelper.onScroll(dy, computeVerticalScrollOffset);
    }
    //endregion TwoWayPagination implementation

    @Override
    public void onSearchQueryChanged(@NonNull String searchQuery) {
        if (!mSearchQuery.equals(searchQuery)) {
            mSearchQuery = searchQuery;
            mPendingScrollToTop = true;
            searchQueryChanged(mSearchQuery);
            makeSearchRequest();
            mPendingSearchRequest = true;
        }
    }

    @NonNull
    @Override
    protected Observable<? extends PagedListResult<DataModel>> getUpdatingListByLastEntityObservable(@Nullable DataModel dataModel, int itemsCount, boolean fromPullToRefresh) {
        return super.getUpdatingListByLastEntityObservable(dataModel, itemsCount, fromPullToRefresh)
                .flatMap((Function<PagedListResult<DataModel>, ObservableSource<? extends PagedListResult<DataModel>>>) pagedListResult -> {
                    final Observable<PagedListResult<DataModel>> result = Observable.just(pagedListResult);
                    if (mProgressDelayMillis > 0 && mPendingSearchRequest && !mSearchQuery.isEmpty() && pagedListResult.getDataList().isEmpty()
                            && (pagedListResult.hasMore() || !pagedListResult.isFullyCached())) {
                        resetLoadingStatus(); // сбрасываем статус загрузки, чтоб все последующие запросы на обновление не вставали в очередь
                        return result.delay(mProgressDelayMillis, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                    return result;
                });
    }

    @Override
    protected boolean processErrorImmediately() {
        if (isPendingSearchLoadingState()) {
            return false;
        }
        return super.processErrorImmediately();
    }

    @Override
    protected void loadOlderPage() {
        if (isPendingSearchLoadingState()) {
            // если выполняется pending search delay update и прилетел запрос на older - форсим перезагрузку списка,
            // которая должна была произойти вместо delay
            updateDataList(false);
        } else {
            super.loadOlderPage();
        }
    }

    @Override
    protected void finalProcessUpdating() {
        super.finalProcessUpdating();
        mPendingSearchRequest = false;
        //Скроллим вверх если нужно
        if (mPendingScrollToTop) {
            if (mView != null && !getDataList().isEmpty()) {
                mView.scrollToPosition(0);
            }
            mPendingScrollToTop = false;
        }
    }

    @Override
    public void onSearchClearButtonClicked() {
        if (mView != null) {
            mView.clearSearchQuery();
            mView.hideInformationView();
        }
    }

    @Override
    public void onSearchButtonClicked() {
        hideKeyboardInternal(true);
        if (!isEmptyLoadingState()) {
            makeSearchRequest();
        }
    }

    @CallSuper
    protected void makeSearchRequest() {
        resetUI();
        resetPagination(false);
        if (mView != null) {
            mView.hideLoading();
        }
        updateDataList(true);
    }

    protected void searchQueryChanged(@NonNull String searchQuery) {
        mFilter.setSearchQuery(searchQuery);
    }

    @Override
    public void onFilterPanelFocusStateChanged(boolean hasFocus) {
        mFocusInFilterPanel = hasFocus;
    }

    @Override
    public void onKeyboardOpened(boolean force) {
        showKeyboardInternal(force);
    }

    @Override
    public void onKeyboardClosed(boolean force) {
        hideKeyboardInternal(force);
    }

    private boolean isPendingSearchLoadingState() {
        return mLoadingState == LoadingState.NOT_LOADING && mPendingSearchRequest;
    }

    private void showKeyboardInternal(boolean force) {
        if (!mIgnoreKeyboardEvents) {
            mKeyboardIsVisible = true;
            // кидаем для последующей обработки SCROLL_DOWN
            mScrollHelper.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE_SOFT);
            if (force && mView != null) {
                mView.showKeyboard();
            }
        }
    }

    private void hideKeyboardInternal(boolean force) {
        if (!mIgnoreKeyboardEvents) {
            mKeyboardIsVisible = false;
            if (force && mView != null) {
                mView.hideKeyboard();
            }
        }
    }

}
