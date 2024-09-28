package ru.tensor.sbis.mvp.search;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.disposables.SerialDisposable;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.common.util.scroll.ScrollHelper;
import ru.tensor.sbis.mvp.presenter.AbstractTwoWayPaginationPresenter;
import ru.tensor.sbis.mvp.presenter.EventManagerSubscriber;
import timber.log.Timber;

/**
 * Legacy-код
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class BaseSearchablePresenter
        <VIEW extends SearchableView<DataModel>, DataModel, EventData>
        extends AbstractTwoWayPaginationPresenter<VIEW, DataModel, EventData>
        implements SearchablePresenter<VIEW> {

    @NonNull
    protected String mSearchQuery = "";

    protected boolean mKeyboardIsVisible;
    private boolean mIgnoreKeyboardEvents = true;
    protected boolean mFocusInFilterPanel;

    @NonNull
    protected final ScrollHelper mScrollHelper;

    @NonNull
    private final SerialDisposable mScrollEventDisposable = new SerialDisposable();

    public BaseSearchablePresenter(@Nullable EventManagerSubscriber<EventData> eventManagerSubscriber,
                                   @NonNull NetworkUtils networkUtils,
                                   @NonNull ScrollHelper scrollHelper) {
        super(eventManagerSubscriber, networkUtils);
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
                        throwable -> Timber.d("Error in scroll event type observable " + getClass().getSimpleName())
                )
        );
    }

    @Override
    public void setProgressDelay(int delayMillis) {
        // ignore
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

    @CallSuper
    @Override
    public void onSearchQueryChanged(@NonNull String searchQuery) {
        if (!mSearchQuery.equals(searchQuery)) {
            mSearchQuery = searchQuery;
            makeSearchRequest();
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
        makeSearchRequest();
    }

    @CallSuper
    protected void makeSearchRequest() {
        resetUI();
        resetPagination();
        if (mView != null) {
            mView.hideLoading();
        }
    }

    @Override
    public void onFilterPanelFocusStateChanged(boolean hasFocus) {
        mFocusInFilterPanel = hasFocus;
    }

    @Override
    public void onKeyboardOpened(boolean force) {
        if (mFocusInFilterPanel) {
            mView.showCursorInFiltersPanel();
        }
        showKeyboardInternal(force);
    }

    @Override
    public void onKeyboardClosed(boolean force) {
        hideKeyboardInternal(force);
    }

    protected void initDefaultKeyboardVisibility() {
        mKeyboardIsVisible = false;
    }

    private void showKeyboardInternal(boolean force) {
        if (!mIgnoreKeyboardEvents) {
            mKeyboardIsVisible = true;
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
