package ru.tensor.sbis.mvp.search;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import io.reactivex.disposables.CompositeDisposable;
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter;
import ru.tensor.sbis.common.R;
import ru.tensor.sbis.common.util.AdjustResizeHelper;
import ru.tensor.sbis.design.view.input.searchinput.SearchInput;
import ru.tensor.sbis.design.view.input.searchinput.SearchInputKt;
import ru.tensor.sbis.mvp.fragment.BaseListFragmentWithTwoWayPagination;
import ru.tensor.sbis.mvp.search.behavior.DefaultSearchInputBehavior;
import ru.tensor.sbis.mvp.search.behavior.SearchInputBehavior;

/**
 * Legacy-код
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression", "deprecation"})
@UiThread
public abstract class BaseSearchableView
        <DM, A extends BaseTwoWayPaginationAdapter<DM>, V, P extends SearchablePresenter<V>>
        extends BaseListFragmentWithTwoWayPagination<DM, A, V, P>
        implements SearchableView<DM>, AdjustResizeHelper.KeyboardEventListener {

    private static final String SEARCH_PANEL_TRANSITION_Y_KEY = BaseSearchableView.class.getCanonicalName() + "SEARCH_PANEL_VISIBLE";

    protected SearchInput mSearchPanel;
    protected CompositeDisposable mSearchDisposable;
    private SearchInputBehavior mSearchInputBehavior;


    /**
     * Child class should have {@link SearchInput} view with
     * R.id.search_filter_panel id in layout file.
     */
    @CallSuper
    @Override
    protected void initViews(@NonNull View mainView, @Nullable Bundle savedInstanceState) {
        mSearchPanel = inflateSearchPanel(mainView);
        mSearchInputBehavior = createSearchInputBehavior();
        if (savedInstanceState != null) {
            restoreStateFromBundle(savedInstanceState);
        }
    }

    @CallSuper
    @Override
    protected void initViewListeners() {
        super.initViewListeners();
        mSearchDisposable = SearchablePresenterBinder.bindToSearchInputBehavior(
                getPresenter(),
                getSearchInputBehavior());
    }

    @NonNull
    protected final SearchInputBehavior getSearchInputBehavior() {
        return mSearchInputBehavior;
    }

    @NonNull
    protected SearchInputBehavior createSearchInputBehavior() {
        if (mSearchPanel != null) {
            return new DefaultSearchInputBehavior(mSearchPanel);
        } else {
            throw new IllegalStateException("Your layout should contain SearchInput");
        }
    }

    protected SearchInput inflateSearchPanel(@NonNull View mainView) {
        return mainView.findViewById(R.id.search_filter_panel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mSbisListView != null) {
            getPresenter().setProgressDelay(mSbisListView.getProgressDelayMillis());
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (!mSearchDisposable.isDisposed()) {
            mSearchDisposable.dispose();
        }
        super.onDestroyView();
        mSearchPanel = null;
        mSearchInputBehavior = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSearchPanel != null) {
            outState.putFloat(SEARCH_PANEL_TRANSITION_Y_KEY, mSearchPanel.getTranslationY());
        }
    }

    protected void restoreStateFromBundle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && mSearchPanel != null) {
            mSearchPanel.setTranslationY(savedInstanceState.getFloat(SEARCH_PANEL_TRANSITION_Y_KEY));
        }
    }

    //region SearchableView interface implementation
    @Override
    public void clearSearchQuery() {
        getSearchInputBehavior().setSearchText(SearchInputKt.DEFAULT_SEARCH_QUERY);
    }

    @Override
    public void showCursorInFiltersPanel() {
        getSearchInputBehavior().showCursorInSearch();
    }

    @Override
    public void showKeyboard() {
        getSearchInputBehavior().showKeyboard();
    }

    @Override
    public void hideKeyboard() {
        getSearchInputBehavior().hideKeyboard();
    }

    @Override
    public void hideCursorFromSearch() {
        getSearchInputBehavior().hideCursorFromSearch();
    }

    /**
     * This method is used to show {@link SearchInput} when user scroll list.
     * Override this method if other views should also react to scroll.
     */
    @Override
    public void showControls() {
        if (isAnimatedSearchPanel() && mSearchPanel != null) {
            mSearchPanel.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2));
        }
    }

    /**
     * This method is used to hide {@link SearchInput} when user scroll list.
     * Override this method if other views should also react to scroll.
     */
    @Override
    public void hideControls() {
        if (isAnimatedSearchPanel() && mSearchPanel != null) {
            mSearchPanel.animate()
                    .translationY(-mSearchPanel.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2));
        }

        getPresenter().onKeyboardClosed(true);
    }

    @Override
    public void hideInformationView() {
        if (!getSearchInputBehavior().getSearchText().isEmpty() && mSbisListView != null) {
            mSbisListView.postHideInformationView();
        } else {
            super.hideInformationView();
        }
    }

    @Override
    public void enableFolders() {
        // do nothing by default
    }

    @Override
    public void disableFolders() {
        // do nothing by default
    }

    @Override
    public void enableFilters() {
        // do nothing by default
    }

    @Override
    public void disableFilters() {
        // do nothing by default
    }
    //endregion SearchableView interface implementation

    //region AdjustResizeHelper.KeyboardEventListener
    @Override
    public boolean onKeyboardOpenMeasure(int keyboardHeight) {
        return true;
    }

    @Override
    public boolean onKeyboardCloseMeasure(int keyboardHeight) {
        return true;
    }
    //endregion

    protected boolean isAnimatedSearchPanel() {
        return true;
    }

}

