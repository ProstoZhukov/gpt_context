package ru.tensor.sbis.mvp.interactor.crudinterface;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.internal.functions.Functions;
import ru.tensor.sbis.common.generated.QueryDirection;
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer;
import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers;
import ru.tensor.sbis.common.util.NetworkUtils;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand;
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData;
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder;
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter;
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager;
import ru.tensor.sbis.mvp.presenter.AbstractTwoWayPaginationPresenter;
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView;
import ru.tensor.sbis.mvp.presenter.DisplayErrorDelegate;
import ru.tensor.sbis.platform.generated.Subscription;
import timber.log.Timber;

/**
 * Базовый класс презентера с двунаправленной подгрузкой страниц
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class BaseListAbstractTwoWayPaginationPresenter<
        VIEW extends BaseTwoWayPaginationView<DataModel>,
        DataModel,
        FILTER extends ListFilter,
        QUERY_FILTER, DATA_REFRESH_CALLBACK
        >
        extends AbstractTwoWayPaginationPresenter<VIEW, DataModel, EventData> {

    private static final int NO_COUNT = -1;

    @NonNull
    protected List<DataModel> mDataList = Collections.emptyList();
    @NonNull
    protected final FILTER mFilter;
    @NonNull
    protected final SubscriptionManager mSubscriptionManager;
    @NonNull
    private final CompositeDisposable mUiActionDisposable = new CompositeDisposable();
    private boolean mSubscribed;
    protected boolean mPrepared;
    protected boolean mRefreshOlderPage;
    protected boolean mWaitingForOlderRefreshCallback;
    protected boolean mRefreshNewerPage;
    private boolean mNetworkAvailableRequest; // TODO убрать по задаче https://online.sbis.ru/opendoc.html?guid=2be0a724-d19c-4d3f-a567-763054ad6f15
    private int mPreviousOlderPageResultCount = NO_COUNT;
    private int mPreviousNewerPageResultCount = NO_COUNT;
    private boolean mAutomaticallyLoadNextPage;

    public BaseListAbstractTwoWayPaginationPresenter(@NonNull FILTER filter, @NonNull SubscriptionManager subscriptionManager, @NonNull NetworkUtils networkUtils) {
        super(null, networkUtils);
        mFilter = filter;
        mSubscriptionManager = subscriptionManager;
    }

    @Override
    public void attachView(@NonNull VIEW view) {
        super.attachView(view);
        if (!mSubscribed) {
            mSubscriptionManager.addConsumer(this::onEvent);
            SubscriptionManager.Batch batch = mSubscriptionManager.batch();
            batch.manage(null, getRefreshCallbackSubscription(), true);
            configureSubscriptions(batch);
            batch.doAfterSubscribing(() -> {
                mPrepared = true;
                updateDataList(true);
            });
            batch.subscribe();
            mSubscribed = true;
        }
        mSubscriptionManager.resume();
    }

    @Override
    public void detachView() {
        mSubscriptionManager.pause();
        super.detachView();
    }

    @Override
    public void onDestroy() {
        mSubscriptionManager.dispose();
        mUiActionDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    @Override
    protected List<DataModel> getDataList() {
        return mDataList;
    }

    @Override
    protected void swapDataList(@NonNull List<DataModel> dataList) {
        if (mDataList != dataList) {
            mDataList = dataList;
        }
    }

    @Override
    protected void resetPagination(boolean clearData) {
        mWaitingForOlderRefreshCallback = false;
        mRefreshOlderPage = false;
        mRefreshNewerPage = false;
        mPreviousOlderPageResultCount = NO_COUNT;
        mPreviousNewerPageResultCount = NO_COUNT;
        super.resetPagination(clearData);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @NonNull
    @Override
    protected Observable<? extends PagedListResult<DataModel>> getLoadingNewerDataObservable(@Nullable DataModel dataModel, int itemsCount) {
        final ListFilter.Builder<DataModel, QUERY_FILTER> builder = mFilter.queryBuilder();
        if (builder instanceof AnchorPositionQueryBuilder) {
            // Корректируем размер для 0 страницы
            itemsCount = Math.min(mDataListOffset, itemsCount);
            final int from = mDataListOffset - itemsCount;
            ((AnchorPositionQueryBuilder) builder).from(from);
        }
        builder.anchorModel(dataModel)
                .itemsCount(itemsCount)
                .direction(QueryDirection.TO_NEWER)
                .inclusive(false);
        configureListQuery(builder);
        // TODO Проверить и убрать по задаче https://online.sbis.ru/opendoc.html?guid=2be0a724-d19c-4d3f-a567-763054ad6f15
        if (!isNeedAutoLoadingNextPage()) {
            if (mRefreshNewerPage) {
                return getListObservableCommand().refresh(builder.build());
            } else {
                mWaitingForOlderRefreshCallback = true;
                return getListObservableCommand().list(builder.build());
            }
        } else {
            return getListObservableCommand().list(builder.build());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @NonNull
    @Override
    protected Observable<? extends PagedListResult<DataModel>> getLoadingOlderDataObservable(@NonNull DataModel dataModel, int itemsCount) {
        final ListFilter.Builder<DataModel, QUERY_FILTER> builder = mFilter.queryBuilder();
        if (builder instanceof AnchorPositionQueryBuilder) {
            final int from = getOlderPageFromIndex();
            ((AnchorPositionQueryBuilder) builder).from(from);
        }
        builder.anchorModel(dataModel)
                .itemsCount(itemsCount)
                .direction(QueryDirection.TO_OLDER)
                .inclusive(false);
        configureListQuery(builder);
        if (mRefreshOlderPage) {
            return getListObservableCommand().refresh(builder.build());
        } else {
            mWaitingForOlderRefreshCallback = true;
            return getListObservableCommand().list(builder.build());
        }
    }

    @Override
    protected void processLoadingNewerPageResult(@NonNull PagedListResult<DataModel> pagedListResult) {
        super.processLoadingNewerPageResult(pagedListResult);
        // TODO Проверить и убрать по задаче https://online.sbis.ru/opendoc.html?guid=2be0a724-d19c-4d3f-a567-763054ad6f15
        if (!isNeedAutoLoadingNextPage()) {
            final int resultItemCount = pagedListResult.getDataList().size();
            mPreviousNewerPageResultCount = resultItemCount;
            if (mRefreshNewerPage) {
                mRefreshNewerPage = false;
            } else if ((pagedListResult.hasMore() || !pagedListResult.hasMore() && !pagedListResult.isFullyCached()) && resultItemCount < getPageSize()) {
                // если вызов прилетел из list, записей меньше чем запрашивали, то следующий запрос должен пойти из кеша для дозагрузки
                mRefreshNewerPage = true;
            }
        }
    }

    @Override
    protected void processLoadingOlderPageResult(@NonNull PagedListResult<DataModel> pagedListResult) {
        super.processLoadingOlderPageResult(pagedListResult);
        final int resultItemCount = pagedListResult.getDataList().size();
        mPreviousOlderPageResultCount = resultItemCount;
        if (mRefreshOlderPage) {
            mRefreshOlderPage = false;
        } else if ((pagedListResult.hasMore() || !pagedListResult.hasMore() && !pagedListResult.isFullyCached()) && resultItemCount < getPageSize()) {
            // если вызов прилетел из list, записей меньше чем запрашивали, то следующий запрос должен пойти из кеша для дозагрузки
            mRefreshOlderPage = true;
        }
    }

    @Override
    protected void setHasOlderPageFromResult(@NonNull PagedListResult<DataModel> pagedListResult) {
        super.setHasOlderPageFromResult(pagedListResult);
        if (!mWaitingForOlderRefreshCallback && !pagedListResult.isFullyCached()) {
            mHasOlderPage = true;
        }
    }

    //region костыль
    // todo: Проверить и убрать по задаче https://online.sbis.ru/opendoc.html?guid=2be0a724-d19c-4d3f-a567-763054ad6f15
    protected boolean isNeedAutoLoadingNextPage() {
        return true;
    }

    @Override
    protected void loadOlderPage() {
        if (getDataList().isEmpty() && isNeedAutoLoadingNextPage()) {
            // если ожидаем refresh callback то идет вызов в кеш
            updateDataList(!mWaitingForOlderRefreshCallback);
        } else {
            super.loadOlderPage();
        }
    }

    @Override
    protected void loadOlderPageAutomatically() {
        mAutomaticallyLoadNextPage = true;
        super.loadOlderPageAutomatically();
    }

    @Override
    protected void loadNewerPageAutomatically() {
        mAutomaticallyLoadNextPage = true;
        super.loadNewerPageAutomatically();
    }

    @Override
    protected boolean isNeedLoadOlderPageAutomatically() {
        if (!isNeedAutoLoadingNextPage()) {
            if (!mAutomaticallyLoadNextPage) {
                return false;
            }
            mAutomaticallyLoadNextPage = false;
            final boolean result = super.isNeedLoadOlderPageAutomatically();
            if (result && !mNetworkAvailableRequest) {
                mRefreshOlderPage = true;
            }
            return result;
        }
        return super.isNeedLoadOlderPageAutomatically();
    }

    @Override
    protected boolean isNeedLoadNewerPageAutomatically() {
        if (!isNeedAutoLoadingNextPage()) {
            if (!mAutomaticallyLoadNextPage) {
                return false;
            }
            mAutomaticallyLoadNextPage = false;
            final boolean result = super.isNeedLoadNewerPageAutomatically();
            if (result && !mNetworkAvailableRequest) {
                mRefreshNewerPage = true;
            }
            return result;
        }
        return super.isNeedLoadNewerPageAutomatically();
    }

    @Override
    protected boolean isNeedLoadNewerPage(int firstVisibleItemPosition) {
        if (!isNeedAutoLoadingNextPage()) {
            if ((getDataList().isEmpty() || mPreviousNewerPageResultCount == 0) && mWaitingForOlderRefreshCallback) {
                return false;
            }
        }
        return super.isNeedLoadNewerPage(firstVisibleItemPosition);
    }

    //endregion

    @Override
    protected boolean isNeedLoadOlderPage(int lastVisibleItemPosition) {
        if ((getDataList().isEmpty() || mPreviousOlderPageResultCount == 0) && mWaitingForOlderRefreshCallback) {
            return false;
        }
        return super.isNeedLoadOlderPage(lastVisibleItemPosition);
    }

    protected int getOlderPageFromIndex() {
        return mDataListOffset + Math.max(getDataList().size() - 1, 0);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @NonNull
    @Override
    protected Observable<? extends PagedListResult<DataModel>> getUpdatingListByLastEntityObservable(@Nullable DataModel dataModel, int itemsCount, boolean fromPullToRefresh) {
        final ListFilter.Builder<DataModel, QUERY_FILTER> builder = mFilter.queryBuilder();
        if (builder instanceof AnchorPositionQueryBuilder) {
            final int from;
            if (dataModel == null) {
                from = mDataListOffset;
            } else {
                from = getOlderPageFromIndex();
            }
            ((AnchorPositionQueryBuilder) builder).from(from);
        }
        if (dataModel == null) {
            // Обновление идет с нулевой позиции (начала списка).
            // В случае если в списке на текущий момент не хватает данных до полной страницы, этот запрос их дополнит, поэтому флаг сбрасываем
            mRefreshOlderPage = false;
            mRefreshNewerPage = false;
            if (fromPullToRefresh) {
                mWaitingForOlderRefreshCallback = true;
            }
        }
        builder.anchorModel(dataModel)
                .itemsCount(itemsCount)
                .direction(dataModel == null ? QueryDirection.TO_OLDER : QueryDirection.TO_NEWER)
                .fromPullRefresh(fromPullToRefresh)
                .inclusive(true);
        configureListQuery(builder);
        if (fromPullToRefresh) {
            return getListObservableCommand().list(builder.build());
        } else {
            return getListObservableCommand().refresh(builder.build());
        }
    }

    @NonNull
    protected abstract BaseListObservableCommand<? extends PagedListResult<DataModel>, QUERY_FILTER, DATA_REFRESH_CALLBACK> getListObservableCommand();

    protected void configureSubscriptions(@NonNull SubscriptionManager.Batch batch) {
        // override this method to extend subscription logic before data request
    }

    protected void configureListQuery(@NonNull ListFilter.Builder<DataModel, QUERY_FILTER> queryBuilder) {
        // override this method to extend the request to the controller
    }

    @NonNull
    protected abstract DATA_REFRESH_CALLBACK getDataRefreshCallback();

    @NonNull
    protected Observable<Subscription> getRefreshCallbackSubscription() {
        return getListObservableCommand().subscribeDataRefreshedEvent(getDataRefreshCallback());
    }

    @CallSuper
    protected void onRefreshCallback(@Nullable HashMap<String, String> params) {
        updateListOnRefreshCallback();
    }

    @Override
    protected final void onNetworkConnected() {
        mWaitingForOlderRefreshCallback = false;
        mRefreshOlderPage = false;
        mRefreshNewerPage = false;
        mNetworkAvailableRequest = true;
        updateDataListAfterNetworkConnected();
        mNetworkAvailableRequest = false;
    }

    protected void updateDataListAfterNetworkConnected() {
        if (getDataList().size() <= getPageSize()) {
            updateDataList(true);
        } else {
            loadOlderPageAutomatically();
        }
    }

    protected final void notifySyncFailed(@NonNull String message) {
        notifySyncFailed(message, false);
    }

    /**
     * Метод нужен в случае, если нужно обработать ошибку синхронизации,
     * но не показывать уведомление с сообщением об ошибке
     *
     * @param isNetworkError флаг ошибки сети
     */
    protected final void notifySyncFailed(boolean isNetworkError) {
        notifySyncFailed(null, null, isNetworkError);
    }

    protected final void notifySyncFailed(@NonNull String message, boolean isNetworkError) {
        notifySyncFailed(message, DisplayErrorDelegate::showLoadingError, isNetworkError);
    }

    protected final void notifySyncFailed(@StringRes int messageResId) {
        notifySyncFailed(messageResId, false);
    }

    protected final void notifySyncFailed(@StringRes int messageResId, boolean isNetworkError) {
        notifySyncFailed(messageResId, DisplayErrorDelegate::showLoadingError, isNetworkError);
    }

    private <M> void notifySyncFailed(M message, @Nullable BiConsumer<VIEW, M> messageConsumer, boolean isNetworkError) {
        runOnUiThread(() -> {
            if (processErrorImmediately()) {
                if (isNetworkError) {
                    processNetworkError();
                } else {
                    processLoadingError();
                }
            } else {
                mMissedPaginationEvent = isNetworkError ? PaginationEvent.NETWORK_ERROR : PaginationEvent.LOADING_ERROR;
            }
            if (mView != null && messageConsumer != null) {
                try {
                    messageConsumer.accept(mView, message);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
    }

    /**
     * @return true - если необходимо обработать ошибку сразу как только она возникла,
     * false - ошибка будет записана в missedPaginationEvent
     */
    protected boolean processErrorImmediately() {
        return mLoadingState == LoadingState.NOT_LOADING;
    }

    @Override
    protected final void processLoadingError() {
        handleSyncError();
        super.processLoadingError();
    }

    @Override
    protected final void processNetworkError() {
        handleSyncError();
        super.processNetworkError();
    }

    /**
     * @return true если список пустой и находится в режиме ожидания данных, false иначе
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    final boolean isEmptyLoadingState() {
        return getDataList().isEmpty() && (mLoadingState != LoadingState.NOT_LOADING || mWaitingForOlderRefreshCallback);
    }

    @SuppressWarnings("deprecation")
    protected final void runOnUiThread(@NonNull Runnable runnable) {
        mUiActionDisposable.add(
                Completable.fromRunnable(runnable)
                        // Гонка процессов происходит из за того что поток AndroidSchedulers.mainThread() содержит
                        // деффект из за которого нет гарантии очереди исполнения. При частых запросах порядок меняется.
                        // TensorSchedulers.INSTANCE.getAndroidUiScheduler() решает эту проблему.
                        // Активнее всего проблема проявляется на сервисных сообщениях диалогов и чатов.
                        .subscribeOn(TensorSchedulers.INSTANCE.getAndroidUiScheduler())
                        .subscribe(Functions.EMPTY_ACTION, FallbackErrorConsumer.DEFAULT)
        );
    }

    private void handleSyncError() {
        // если данные есть, то флаг не сбрасываем для исключения зацикливания при подходе к границе
        // когда следующей страницы в кеше нет и hasMore = true
        if (getDataList().isEmpty()) {
            mWaitingForOlderRefreshCallback = false;
        }
        mRefreshOlderPage = false;
        mRefreshNewerPage = false;
    }

    private void updateListOnRefreshCallback() {
        runOnUiThread(() -> {
            if (mWaitingForOlderRefreshCallback) {
                mWaitingForOlderRefreshCallback = false;
                if (mShowOlderProgress && mLoadingState != LoadingState.TO_OLDER) {
                    mHasOlderPage = true; // для дозагрузки нижней страницы после update
                }
            }
            mAutomaticallyLoadNextPage = true;
            updateDataList(false);
        });
    }
}
