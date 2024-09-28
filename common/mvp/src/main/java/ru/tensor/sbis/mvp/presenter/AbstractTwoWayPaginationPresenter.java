package ru.tensor.sbis.mvp.presenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.SerialDisposable;
import ru.tensor.sbis.common.R;
import ru.tensor.sbis.common.generated.ErrorCode;
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import timber.log.Timber;

/**
 * Legacy-код
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"RedundantSuppression", "CanBeFinal"})
public abstract class AbstractTwoWayPaginationPresenter
        <VIEW extends BaseTwoWayPaginationView<DataModel>, DataModel, EventData>
        extends AbstractBasePresenter<VIEW, EventData>
        implements BaseTwoWayPaginationPresenter<VIEW> {

    private static final int NO_RESOURCE = -1;
    private static final int DEFAULT_ITEMS_RESERVE = 20;
    protected static final int DEFAULT_PAGE_SIZE = 50;
    private static final int DEFAULT_MAX_DATA_LIST_SIZE = 200;

    protected boolean mHasNewerPage;
    protected boolean mHasOlderPage;
    protected boolean mShowOlderProgress;

    //is set if update was requested while loading next page
    //updating will start after loading next page
    protected boolean mNeedUpdateDataList;

    @NonNull
    protected LoadingState mLoadingState = LoadingState.NOT_LOADING;
    protected PaginationEvent mMissedPaginationEvent;

    protected int mDataListOffset;

    protected int mFirstVisibleItem;
    protected int mLastVisibleItem;

    protected boolean mShowLoadingProcess;
    protected boolean mShowSyncProcess;

    @StringRes
    protected int mMissedErrorResId = NO_RESOURCE;

    @NonNull
    protected SerialDisposable mLoadingPageSubscription = new SerialDisposable();
    @SuppressWarnings("WeakerAccess")
    @Nullable
    Disposable mNetworkSubscription;
    @NonNull
    protected NetworkUtils mNetworkUtils;

    public AbstractTwoWayPaginationPresenter(@Nullable EventManagerSubscriber<EventData> eventManagerSubscriber,
                                             @NonNull NetworkUtils networkUtils) {
        super(eventManagerSubscriber);
        mNetworkUtils = networkUtils;
    }

    @Override
    public void attachView(@NonNull VIEW view) {
        super.attachView(view);

        if (isNeedToRestoreScrollPosition()) {
            view.scrollToPosition(mFirstVisibleItem);
        }

        if (mShowLoadingProcess || mShowSyncProcess) {
            view.showLoading();
        } else {
            view.hideLoading();
        }

        view.showOlderLoadingProgress(mShowOlderProgress);
        view.showNewerLoadingProgress(mHasNewerPage);

        if (mMissedErrorResId != NO_RESOURCE) {
            view.showLoadingError(mMissedErrorResId);
            mMissedErrorResId = NO_RESOURCE;
        }
    }

    @CallSuper
    @Override
    public void onDestroy() {
        mLoadingPageSubscription.dispose();
        if (mNetworkSubscription != null) {
            mNetworkSubscription.dispose();
        }
        super.onDestroy();
    }

    /**
     * Implement this method in your fragment if you want some action to be executed on onStart of VIEW
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void viewIsStarted() {
        //ignore
    }

    /**
     * Implement this method in your fragment if you want some action to be executed on onStop of VIEW
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void viewIsStopped() {
        //ignore
    }

    /**
     * Implement this method in your fragment if you want some action to be executed on onResume of VIEW
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void viewIsResumed() {
        //ignore
    }

    /**
     * Implement this method in your fragment if you want some action to be executed on onPause of VIEW
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void viewIsPaused() {
        //ignore
    }

    /**
     * Is newer pagination enabled or not. Some APIs
     * cannot provide newer pagination feature, in this case
     * you should to disable newer pagination by overriding this method.
     *
     * @return true - if newer pagination is supported, false - otherwise
     */
    protected boolean isNewerPaginationSupported() {
        return true;
    }

    /**
     * Get max size of data list. For two-way paginated
     * list the value is {@link #DEFAULT_MAX_DATA_LIST_SIZE}
     * by default. If two-way pagination not supported,
     * data list is not restricted by size.
     *
     * @return max data list size restriction
     */
    protected int getMaxDataListSize() {
        if (isNewerPaginationSupported()) {
            return DEFAULT_MAX_DATA_LIST_SIZE;
        }
        // Если отключена пагинация вверх, убираем ограничение с длины списка данных
        return Integer.MAX_VALUE;
    }

    @Override
    public void forceReloadDataList() {
        resetUI();
        resetPagination();
        onRefresh();
        if (mView != null) {
            mView.updateListViewState();
        }
    }

    /**
     * Resets pagination - erases all collected data to start pagination from scratch.
     */
    protected void resetPagination() {
        resetPagination(true);
    }

    protected void resetPagination(boolean clearData) {
        List<DataModel> dataModelList = getDataList();
        if (dataModelList != null && dataModelList.size() > 0) {
            if (clearData || mDataListOffset > 0) {
                // Очищаем список, если передан флаг очистки или в списке нет первой страницы
                dataModelList = new ArrayList<>();
                swapDataList(dataModelList);
            }

            mFirstVisibleItem = 0;
            mLastVisibleItem = 0;
            mDataListOffset = 0;
            mHasNewerPage = false;
            mHasOlderPage = false;
            mShowOlderProgress = false;

            if (mView != null) {
                mView.updateDataList(dataModelList, mDataListOffset);
                mView.showOlderLoadingProgress(mShowOlderProgress);
                mView.showNewerLoadingProgress(mHasNewerPage);
            }
        }
    }

    protected void resetUI() {
        if (mView != null) {
            mView.resetUiState();
        }
    }

    /**
     * Load older OR newer page depending on scrolling direction and availability of that page in database.
     * Loading starts only when you're {@link #DEFAULT_ITEMS_RESERVE} items or less below the end of data list.
     *
     * @param dy                          - y value by which view was scrolled
     * @param firstVisibleItemPosition    - adapterPosition of first visible item on screen
     * @param lastVisibleItemPosition     - adapterPosition of last visible item on screen
     * @param computeVerticalScrollOffset - computed scroll distance
     */
    @CallSuper
    @Override
    public void onScroll(int dy, int firstVisibleItemPosition, int lastVisibleItemPosition, int computeVerticalScrollOffset) {
        setEdgeItemsPositions(firstVisibleItemPosition, lastVisibleItemPosition);

        if (dy != 0) {
            if (isNewerDirection(dy)) {
                if (isNeedLoadNewerPage(firstVisibleItemPosition)) {
                    if (mLoadingState == LoadingState.UPDATE) {
                        mNeedUpdateDataList = true;
                    }
                    mHasNewerPage = false;
                    loadNewerPage();
                }
            } else {
                if (isNeedLoadOlderPage(lastVisibleItemPosition)) {
                    if (mLoadingState == LoadingState.UPDATE) {
                        mNeedUpdateDataList = true;
                    }
                    mHasOlderPage = false;
                    loadOlderPage();
                }
            }
        }
    }

    protected void setEdgeItemsPositions(int firstVisibleItemPosition, int lastVisibleItemPosition) {
        mFirstVisibleItem = firstVisibleItemPosition;
        mLastVisibleItem = lastVisibleItemPosition;
    }

    @Override
    public void onRefresh() {
        updateDataList(true);
    }

    protected boolean isNewerDirection(int dy) {
        return dy < 0;
    }

    protected boolean isNeedLoadNewerPage(int firstVisibleItemPosition) {
        return mHasNewerPage && firstVisibleItemPosition - mDataListOffset <= getItemsReserve()
                && (mLoadingState == LoadingState.NOT_LOADING || mLoadingState == LoadingState.UPDATE);
    }

    protected boolean isNeedLoadOlderPage(int lastVisibleItemPosition) {
        return mHasOlderPage && checkLastVisiblePositionForLoadOlderPage(lastVisibleItemPosition);
    }

    @SuppressWarnings("rawtypes")
    protected boolean checkLastVisiblePositionForLoadOlderPage(int lastVisibleItemPosition) {
        final List currentDataList = getDataList();
        return (currentDataList == null
                || currentDataList.size() + mDataListOffset - lastVisibleItemPosition <= getItemsReserve())
                && (mLoadingState == LoadingState.NOT_LOADING || mLoadingState == LoadingState.UPDATE);
    }

    //TODO После выполнения задачи поменять модификатор доступа на private
    //https://online.sbis.ru/doc/1d3d8514-eab9-457b-9453-e619086d7e63
    protected boolean isNeedLoadOlderPageAutomatically() {
        return isNeedLoadOlderPage(mLastVisibleItem);
    }

    protected boolean isNeedLoadNewerPageAutomatically() {
        return isNeedLoadNewerPage(mFirstVisibleItem);
    }

    protected void loadOlderPageAutomatically() {
        mHasOlderPage = true;
        if (isNeedLoadOlderPageAutomatically()) {
            loadOlderPage();
        }
    }

    protected void loadNewerPageAutomatically() {
        mHasNewerPage = true;
        if (isNeedLoadNewerPageAutomatically()) {
            loadNewerPage();
        }
    }

    protected int getPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    protected int getItemsReserve() {
        return DEFAULT_ITEMS_RESERVE;
    }

    protected boolean getReversedList() {
        return false;
    }

    protected void loadNewerPage() {
        mLoadingState = LoadingState.TO_NEWER;

        final int requestedListSize;

        if (mFirstVisibleItem < mDataListOffset) {
            int pageSize = getPageSize();
            requestedListSize = (1 + (mDataListOffset - mFirstVisibleItem) / pageSize) * pageSize;
        } else {
            requestedListSize = getPageSize();
        }

        List<DataModel> currentDataList = getDataList();

        //noinspection ConstantConditions
        if (currentDataList != null) {
            mLoadingPageSubscription.set(
                    getLoadingNewerDataObservable(currentDataList.size() == 0 ? null : currentDataList.get(0), requestedListSize)
                            .doAfterTerminate(this::resetLoadingStatus)
                            .subscribe(
                                    pagedListResult -> {
                                        processLoadingNewerPageResult(pagedListResult);
                                        finalProcessNewerPageLoading();
                                    },
                                    throwable -> {
                                        processLoadingNextPageError(throwable);
                                        finalProcessNewerPageLoading();
                                    }
                            )
            );
        }
    }

    protected void loadOlderPage() {
        List<DataModel> currentDataList = getDataList();

        if (currentDataList == null || currentDataList.isEmpty()) {
            updateDataList(false);
        } else {
            mLoadingState = LoadingState.TO_OLDER;
            //noinspection ConstantConditions
            mLoadingPageSubscription.set(
                    getLoadingOlderDataObservable(currentDataList.get(currentDataList.size() - 1), getPageSize())
                            .doAfterTerminate(this::resetLoadingStatus)
                            .subscribe(
                                    pagedListResult -> {
                                        processLoadingOlderPageResult(pagedListResult);
                                        finalProcessOlderPageLoading();
                                    },
                                    throwable -> {
                                        processLoadingNextPageError(throwable);
                                        finalProcessOlderPageLoading();
                                    }
                            )
            );
        }
    }

    /**
     * Проверить наличие в списке данных. Переопределите этот метод, если в списке
     * могут быть элементы, не относящиеся к основным данным.
     *
     * @param currentDataListSize - количество элементов в списке
     * @return true - если в списке есть данные, false - иначе
     */
    protected boolean hasData(int currentDataListSize) {
        return currentDataListSize > 0;
    }

    /**
     * Проверить наличие неполной страницы в списке. Переопределите этот метод, если
     * в списке могут быть элементы, не относящиеся к основным данным.
     *
     * @param currentDataListSize - количество элементов в списке
     * @return true - если в списке есть неполная страница, false - иначе
     */
    protected boolean hasPartialPage(int currentDataListSize) {
        return (currentDataListSize % getPageSize()) != 0;
    }

    /**
     * Получить последнюю сущность в текущем списке для выполнения обновления снизу-вверх.
     * В случае, если данный метод возвращает null, обновление выполняется сверху-вниз.
     *
     * @return последняя сущность в текущем списке либо null если требуется обновление сверху-вниз
     */
    @Nullable
    protected DataModel getLastEntityForUpdate() {
        final List<DataModel> currentDataList = getDataList();
        final int currentDataListSize = currentDataList == null ? 0 : currentDataList.size();
        return mFirstVisibleItem > 0 && currentDataListSize > 0
                ? currentDataList.get(currentDataListSize - 1)
                : null;
    }

    protected void updateDataList(boolean fromPullToRefresh) {
        if (!fromPullToRefresh && mLoadingState != LoadingState.NOT_LOADING) {//update will start after loading finished
            mNeedUpdateDataList = true;
        } else {
            mShowLoadingProcess = fromPullToRefresh;
            mLoadingState = LoadingState.UPDATE;
            mNeedUpdateDataList = false;

            if (mShowLoadingProcess && mView != null) {
                mView.showLoading();
            }

            List<DataModel> currentDataList = getDataList();

            final int currentDataListSize = currentDataList == null ? 0 : currentDataList.size();

            final DataModel lastEntity = getLastEntityForUpdate();

            final int requestedDataListSize;

            if (lastEntity == null) {
                if (currentDataListSize >= getMaxDataListSize()) {
                    requestedDataListSize = getMaxDataListSize();
                } else {
                    int loadedPages = currentDataListSize / getPageSize();
                    int addition = !hasData(currentDataListSize) || hasPartialPage(currentDataListSize) ? 1 : 0;
                    requestedDataListSize = (loadedPages + addition) * getPageSize();
                }
            } else {
                requestedDataListSize = currentDataListSize;
            }

            mLoadingPageSubscription.set(
                    getUpdatingListByLastEntityObservable(lastEntity, requestedDataListSize, fromPullToRefresh)
                            .doFinally(this::finalProcessUpdating)
                            .doAfterTerminate(this::resetLoadingStatus)
                            .subscribe(
                                    dataList -> processUpdatingDataListResult(dataList, lastEntity != null),
                                    this::processUpdatingDataListError
                            )
            );
        }
    }

    /**
     * Is it necessary to restore position in list view after changing configuration
     *
     * @return true if necessary
     */
    @SuppressWarnings("rawtypes")
    protected boolean isNeedToRestoreScrollPosition() {
        List currentDataList = getDataList();
        return isNeedToDisplayViewState() && currentDataList != null && !currentDataList.isEmpty() && mFirstVisibleItem >= 0;
    }

    /**
     * Updating data list by adding data models from pagedListResult to the beginning of dataList.
     * Removing all dataList elements from the tail that exceeds {@link #DEFAULT_MAX_DATA_LIST_SIZE} value.
     * After removing exceeding elements from list their count is being withdrawn from {@link #mDataListOffset}
     * <p>
     * ALSO removes exceeding headers on intersection of the lists
     *
     * @param pagedListResult result of loading newer page
     */
    protected void processLoadingNewerPageResult(@NonNull PagedListResult<DataModel> pagedListResult) {
        List<DataModel> currentDataList = getDataList();
        final List<DataModel> dataList = pagedListResult.getDataList();
        final int insertCount = dataList.size();
        final int oldOffset = mDataListOffset;
        mDataListOffset -= insertCount;

        mHasNewerPage = pagedListResult.hasMore();

        int offset = currentDataList.size() + dataList.size() - getMaxDataListSize();

        if (offset > 0) {
            mHasOlderPage = true;
            int firstPositionToRemove = getMaxDataListSize() - dataList.size();

            if (firstPositionToRemove <= 1) {
                Timber.d("Pagination. To more the result list, or to small the limit!");
                return;
            }

            List<DataModel> sublistToBeCleared = currentDataList.subList(firstPositionToRemove, firstPositionToRemove + offset);
            sublistToBeCleared.clear();
        }

        if (mDataListOffset < 0 || !mHasNewerPage) {
            mDataListOffset = 0;
        }

        currentDataList.addAll(0, dataList);

        if (mView != null) {
            mView.updateDataListWithoutNotification(currentDataList, mDataListOffset);
            if (insertCount > 0) {
                if (oldOffset == 0) {
                    mView.notifyItemsInserted(0, insertCount);
                } else {
                    // если использовать changed анимацию - список проскроллится вверх на неопределенную высоту
                    mView.notifyItemsRemoved(mDataListOffset, insertCount);
                    mView.notifyItemsInserted(mDataListOffset, insertCount);
                }
            }
            mView.showNewerLoadingProgress(mHasNewerPage);
        }
    }

    /**
     * Updating data list by adding data models from pagedListResult to the end of dataList.
     * Removing all dataList elements from the head that exceeds {@link #DEFAULT_MAX_DATA_LIST_SIZE} value.
     * After removing exceeding elements from list their count is being added to {@link #mDataListOffset}
     * <p>
     * ALSO removes exceeding headers on intersection of the lists
     *
     * @param pagedListResult result of loading older page
     */
    protected void processLoadingOlderPageResult(@NonNull PagedListResult<DataModel> pagedListResult) {
        List<DataModel> currentDataList = getDataList();
        final List<DataModel> dataList = pagedListResult.getDataList();
        int insertedFrom = currentDataList.size() + mDataListOffset;
        int insertCount = dataList.size();
        int updateCount = 0;

        if (getReversedList()) {
            updateCount++;
            insertCount--;
        }

        setHasOlderPageFromResult(pagedListResult);
        mShowOlderProgress = !pagedListResult.isFullyCached() || mHasOlderPage;

        int offset = currentDataList.size() + dataList.size() - getMaxDataListSize();

        if (offset > 0) {
            mHasNewerPage = true;

            if (offset >= currentDataList.size() + 1) {
                Timber.d("Pagination. To more the result list, or to small the limit!");
            }

            List<DataModel> sublistToBeCleared = currentDataList.subList(0, offset);
            sublistToBeCleared.clear();

            mDataListOffset += offset;
        }

        currentDataList.addAll(currentDataList.size(), dataList);

        if (mView != null) {
            mView.updateDataListWithoutNotification(currentDataList, mDataListOffset);
            mView.showOlderLoadingProgress(mShowOlderProgress);
            if (updateCount > 0) {
                mView.notifyItemsChanged(insertedFrom, updateCount);
            }
            if (insertCount > 0) {
                mView.notifyItemsInserted(insertedFrom + updateCount, insertCount);
            }
        }
    }

    /**
     * Updating data list by swapping data lists. Updates headers count.
     * Updates flag {@link #mHasNewerPage} or {@link #mHasOlderPage} depending on updateFromTail parameter.
     *
     * @param pagedListResult  result of updating current pages
     * @param updatingFromTail - should we update list from tail to head of from head to tail
     */
    protected void processUpdatingDataListResult(@NonNull PagedListResult<DataModel> pagedListResult,
                                                 boolean updatingFromTail) {
        final ErrorCode errorCode = pagedListResult.getCommandStatus() != null ? pagedListResult.getCommandStatus().getErrorCode() : null;
        List<DataModel> dataList = pagedListResult.getDataList();

        processUpdatingDataListResultWithoutViewUpdating(pagedListResult, updatingFromTail, errorCode);

        if (mView != null) {
            mView.updateDataList(dataList, mDataListOffset);
            mView.showOlderLoadingProgress(mShowOlderProgress);
            mView.showNewerLoadingProgress(mHasNewerPage);
            if (errorCode == ErrorCode.NETWORK_ERROR) {
                showEmptyViewIfNeeded(mView, dataList, getEmptyViewErrorId(), R.string.common_no_network_available_check_connection);
            } else {
                showEmptyViewIfNeeded(mView, dataList, getEmptyViewErrorId());
            }
        }
    }

    protected void processUpdatingDataListResultWithoutViewUpdating(@NonNull PagedListResult<DataModel> pagedListResult,
                                                                    boolean updatingFromTail,
                                                                    ErrorCode errorCode) {
        List<DataModel> dataList = pagedListResult.getDataList();
        if (ErrorCode.OTHER_ERROR.equals(errorCode) && dataList.isEmpty()) {
            updateDataList(false);
            return;
        }

        if (updatingFromTail) {
            mHasNewerPage = pagedListResult.hasMore();

            if (!mHasNewerPage && mDataListOffset > 0) {
                mDataListOffset = 0;
            }
        } else {
            setHasOlderPageFromResult(pagedListResult);

            if (mDataListOffset != 0) {
                Timber.d("There must no be offset!");
            }
        }

        mShowOlderProgress = !pagedListResult.isFullyCached() || mHasOlderPage;

        swapDataList(dataList);

        if (mShowSyncProcess = ErrorCode.SYNC_IN_PROGRESS.equals(errorCode)) {
            mShowLoadingProcess = true;
            if (mView != null) {
                mView.showLoading();
            }
        }

        loadPagesIfNeeded();
    }

    protected void loadPagesIfNeeded() {
        processMissingEvents();

        if (isNeedLoadOlderPageAutomatically()) {
            loadOlderPage();
        }
        if (isNeedLoadNewerPageAutomatically()) {
            loadNewerPage();
        }
    }

    protected void setHasOlderPageFromResult(@NonNull PagedListResult<DataModel> pagedListResult) {
        mHasOlderPage = pagedListResult.hasMore();
    }

    protected final void showEmptyViewIfNeeded(@NonNull VIEW view, @Nullable List<DataModel> dataList, @StringRes int errorMessageRes) {
        showEmptyViewIfNeeded(view, dataList, errorMessageRes, NO_RESOURCE);
    }

    protected void showEmptyViewIfNeeded(@NonNull VIEW view, @Nullable List<DataModel> dataList, @StringRes int errorMessageRes, @StringRes int errorDetailsRes) {
        if (!mShowSyncProcess && !mShowOlderProgress && !mHasNewerPage && (dataList == null || dataList.isEmpty())) {
            if (errorDetailsRes != NO_RESOURCE) {
                view.showMessageInEmptyView(errorMessageRes, errorDetailsRes);
            } else {
                view.showMessageInEmptyView(errorMessageRes);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void subscribeOnNetworkConnection() {
        if (!mNetworkUtils.isConnected()) {
            if (mNetworkSubscription == null || mNetworkSubscription.isDisposed()) {
                // Если сети на данный момент нет, запускаем ожидание соединения
                mNetworkSubscription = mNetworkUtils.networkStateObservable()
                        // костыль, без которого не испускаются данные при повторной подписке
                        // будут разбираться по задаче https://online.sbis.ru/doc/9f2b158a-b3d8-4fca-babe-0bf95b99361d
                        .mergeWith(mNetworkUtils.networkStateObservable())
                        .subscribe(networkAvailability -> {
                            if (mNetworkUtils.isConnected()) { // проверяем явно, т.к критично отписаться сразу
                                onNetworkConnected();
                                if (mNetworkSubscription != null) {
                                    mNetworkSubscription.dispose();
                                }
                            }
                        }, error -> new FallbackErrorConsumer("Error on network subscription"));
            }
        } else {
            if (mNetworkSubscription != null) {
                mNetworkSubscription.dispose();
            }
        }
    }

    protected void onNetworkConnected() {
        loadOlderPageAutomatically();
    }

    protected void resetLoadingStatus() {
        mLoadingState = LoadingState.NOT_LOADING;
    }

    protected void finalProcessUpdating() {
        mShowLoadingProcess = false;

        if (mView != null && !mShowSyncProcess) {
            mView.hideLoading();
        }

        if (mNeedUpdateDataList) {
            updateDataList(false);
        }
    }

    protected void finalProcessOlderPageLoading() {
        finalProcessNextPageLoading();
    }

    protected void finalProcessNewerPageLoading() {
        finalProcessNextPageLoading();
    }

    protected void finalProcessNextPageLoading() {
        final LoadingState currentState = mLoadingState;
        mLoadingState = LoadingState.NOT_LOADING;

        //todo to newer?! would be useful for chats
        if (currentState == LoadingState.TO_OLDER) {
            processMissingEvents();
        }

        if (mNeedUpdateDataList) {
            updateDataList(false);
        }
    }

    private void processMissingEvents() {
        PaginationEvent missedPaginationEvent = mMissedPaginationEvent;
        mMissedPaginationEvent = null;
        if (missedPaginationEvent != null) {
            switch (missedPaginationEvent) {
                case HAS_NEWER:
                    loadNewerPageAutomatically();
                    break;
                case HAS_OLDER:
                    loadOlderPageAutomatically();
                    break;
                case NO_DATA:
                    mShowOlderProgress = false;
                    if (mView != null) {
                        mView.showOlderLoadingProgress(false);
                        showEmptyViewIfNeeded(mView, getDataList(), getEmptyViewErrorId());
                    }
                    break;
                case NETWORK_ERROR:
                    processNetworkError();
                    break;
                case LOADING_ERROR:
                    processLoadingError();
                    break;
            }
        }
    }

    protected void processNetworkError() {
        subscribeOnNetworkConnection();
        List<DataModel> dataList = getDataList();
        if (mView != null && (dataList == null || dataList.isEmpty())) {
            mView.showMessageInEmptyView(getEmptyViewErrorId(), R.string.common_no_network_available_check_connection);
        }
    }

    protected void processLoadingError() {
        List<DataModel> dataList = getDataList();
        if (mView != null && (dataList == null || dataList.isEmpty())) {
            mView.showMessageInEmptyView(getEmptyViewErrorId(), getEmptyViewLoadingErrorCommentId());
        }
    }

    protected void processLoadingNextPageError(@NonNull Throwable error) {
        processLoadingError();
        if (mView != null) {
            mView.showLoadingError(R.string.common_data_loading_error);
        } else {
            mMissedErrorResId = R.string.common_data_loading_error;
        }
    }

    protected void processUpdatingDataListError(@NonNull Throwable error) {
        processLoadingNextPageError(error);
    }

    protected void setMissedErrorResId(@StringRes int errorResId) {
        mMissedErrorResId = errorResId;
    }

    @SuppressWarnings("unused")
    protected void setFirstVisibleItemPosition(int firstVisibleItemPosition) {
        mFirstVisibleItem = firstVisibleItemPosition;
    }

    @StringRes
    protected abstract int getEmptyViewErrorId();

    /**
     * Переопределите этот метод для указания текста
     * комментария empty view в случае ошибки загрузки.
     */
    protected int getEmptyViewLoadingErrorCommentId() {
        return 0;
    }

    @Nullable
    protected abstract List<DataModel> getDataList();

    protected abstract void swapDataList(@NonNull List<DataModel> dataList);

    @NonNull
    protected abstract Observable<? extends PagedListResult<DataModel>> getLoadingNewerDataObservable(
            @Nullable DataModel dataModel, int itemsCount
    );

    @NonNull
    protected abstract Observable<? extends PagedListResult<DataModel>> getLoadingOlderDataObservable(
            @NonNull DataModel dataModel, int itemsCount
    );

    @NonNull
    protected abstract Observable<? extends PagedListResult<DataModel>> getUpdatingListByLastEntityObservable(
            @Nullable DataModel dataModel,
            int itemsCount,
            boolean fromPullToRefresh);

    protected enum LoadingState {
        TO_NEWER,
        TO_OLDER,
        UPDATE,
        LOADING_UNSPECIFIED,
        NOT_LOADING
    }

    protected enum PaginationEvent {
        NO_DATA,
        HAS_NEWER,
        HAS_OLDER,
        NETWORK_ERROR,
        LOADING_ERROR
    }

}
