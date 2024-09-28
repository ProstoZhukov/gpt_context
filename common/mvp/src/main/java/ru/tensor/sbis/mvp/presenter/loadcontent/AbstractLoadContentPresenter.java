package ru.tensor.sbis.mvp.presenter.loadcontent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.common.exceptions.LoadDataException;

/**
 * Base presenter with load content functionality.
 *
 * @param <V> - view
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public abstract class AbstractLoadContentPresenter<V extends LoadContentView> implements LoadContentPresenter<V> {

    @Nullable
    protected V mView;

    /**
     * Loading status.
     */
    private LoadingStatus mStatus = LoadingStatus.NOT_LOADED;

    /**
     * Cache error skipped by detached view.
     */
    private LoadDataException mSkippedError;

    /**
     * Call {@link #onLoadingCompleted()} or {@link #onLoadingError(LoadDataException)}
     * after loading result arrived to provide correct view behaviour.
     *
     * @param force - manual initiation of loading
     */
    protected abstract void loadContent(boolean force);

    /**
     * Display loading error by attached view.
     *
     * @param view      - attached view
     * @param exception - loading exception
     */
    protected abstract void showLoadingError(@NonNull V view, @Nullable LoadDataException exception);

    /**
     * Returns loading status. This status manipulates by {@link #startLoading(boolean, boolean)}
     * and {@link #onLoadingCompleted()}/{@link #onLoadingError(LoadDataException)} callbacks.
     *
     * @return loading status
     */
    public LoadingStatus getLoadingStatus() {
        return mStatus;
    }

    // region LoadContentPresenter implementation
    @CallSuper
    @Override
    public void attachView(@NonNull V view) {
        mView = view;
        // Handler skipped error
        if (mSkippedError != null) {
            showLoadingError(view, mSkippedError);
            mSkippedError = null;
        }
        // Restore refresh state
        view.showLoadingProcess(getLoadingStatus() == LoadingStatus.IN_PROGRESS);
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onRefresh() {
        startLoading(true, true);
    }

    @Override
    public void startLoading(boolean force, boolean showProcess) {
        mStatus = LoadingStatus.IN_PROGRESS;
        if (mView != null && showProcess) {
            mView.showLoadingProcess(true);
        }
        loadContent(force);
    }

    @Override
    public void onLoadingCompleted() {
        mStatus = LoadingStatus.LOADING_SUCCESS;
        if (mView != null) {
            mView.showLoadingProcess(false);
        }
    }

    @Override
    public void onLoadingError(@Nullable LoadDataException exception) {
        mStatus = LoadingStatus.LOADING_FAILED;
        if (mView != null) {
            // Handle error
            mView.showLoadingProcess(false);
            showLoadingError(mView, exception);
        } else {
            // Cache error
            mSkippedError = exception;
        }
    }
    //endregion

    /**
     * Loading status enum.
     */
    public enum LoadingStatus {

        /**
         * Content not loaded yet.
         */
        NOT_LOADED,

        /**
         * Content is loading.
         */
        IN_PROGRESS,

        /**
         * Last loading was failed.
         */
        LOADING_FAILED,

        /**
         * Last loading was success.
         */
        LOADING_SUCCESS
    }

}
