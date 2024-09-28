package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository;
import ru.tensor.sbis.platform.generated.Subscription;

/**
 * Базовая реализация команды списка
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression", "RedundantThrows"})
public class BaseListCommand<VM, ENTITY_LIST, ENTITY_FILTER, DATA_REFRESH_CALLBACK>
        extends BaseFetchCommand<ENTITY_LIST, PagedListResult<VM>>
        implements BaseListObservableCommand<PagedListResult<VM>, ENTITY_FILTER, DATA_REFRESH_CALLBACK> {

    @NonNull
    protected final BaseListRepository<ENTITY_LIST, ENTITY_FILTER, DATA_REFRESH_CALLBACK> mRepository;
    @NonNull
    protected final Function<ENTITY_LIST, PagedListResult<VM>> mListMapper;

    public BaseListCommand(@NonNull BaseListRepository<ENTITY_LIST, ENTITY_FILTER, DATA_REFRESH_CALLBACK> repository,
                           @NonNull Function<ENTITY_LIST, PagedListResult<VM>> listMapper) {
        super();
        mRepository = repository;
        mListMapper = listMapper;
    }

    @NonNull
    @Override
    protected Function<ENTITY_LIST, PagedListResult<VM>> getMapper(boolean refresh) {
        return mListMapper;
    }

    @SuppressWarnings("Convert2Lambda")
    @NonNull
    @Override
    public Observable<PagedListResult<VM>> list(@NonNull ENTITY_FILTER filter) {
        return fetch(Observable.fromCallable(
                new Callable<ENTITY_LIST>() {
                    @Override
                    public ENTITY_LIST call() throws Exception {
                        return mRepository.list(filter);
                    }
                }
        ), false);
    }

    @SuppressWarnings("Convert2Lambda")
    @NonNull
    @Override
    public Observable<PagedListResult<VM>> refresh(@NonNull ENTITY_FILTER filter) {
        return fetch(Observable.fromCallable(
                new Callable<ENTITY_LIST>() {
                    @Override
                    public ENTITY_LIST call() throws Exception {
                        return mRepository.refresh(filter);
                    }
                }
        ), true);
    }

    @NonNull
    @Override
    public Observable<Subscription> setDataRefreshCallback(@NonNull DATA_REFRESH_CALLBACK callback) {
        return Observable.fromCallable(() -> mRepository.subscribeDataRefreshedEvent(callback))
                .compose(getObservableBackgroundSchedulers());
    }

    @NonNull
    @Override
    public Observable<Subscription> subscribeDataRefreshedEvent(@NonNull DATA_REFRESH_CALLBACK callback) {
        return Observable.fromCallable(() -> mRepository.subscribeDataRefreshedEvent(callback))
                .compose(getObservableBackgroundSchedulers());
    }

}

