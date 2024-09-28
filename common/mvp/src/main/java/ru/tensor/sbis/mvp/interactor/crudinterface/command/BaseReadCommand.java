package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import ru.tensor.sbis.common.exceptions.LoadDataException;
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseCRUDRepository;

/**
 * Реализация команды чтения из репозитория.
 * <p>
 * {@link ENTITY} тип сущности
 * {@link UUID} уникальный идентификатор для чтения сущности
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public class BaseReadCommand<VM, ENTITY, UUID> extends BaseFetchCommand<ENTITY, VM>
        implements BaseReadObservableCommand<VM, UUID> {

    @NonNull
    protected final BaseCRUDRepository<ENTITY, UUID> mRepository;
    @NonNull
    private final Function<ENTITY, VM> mMapper;

    public BaseReadCommand(@NonNull BaseCRUDRepository<ENTITY, UUID> repository,
                           @NonNull Function<ENTITY, VM> mapper) {
        mRepository = repository;
        mMapper = mapper;
    }

    @NonNull
    @Override
    protected Function<ENTITY, VM> getMapper(boolean refresh) {
        return mMapper;
    }

    @SuppressWarnings("Convert2Lambda")
    @NonNull
    @Override
    public Observable<VM> read(@NonNull UUID uuid) {
        return fetch(Observable.fromCallable(
                new Callable<ENTITY>() {
                    @Override
                    public ENTITY call() {
                        final ENTITY result = mRepository.read(uuid);
                        if (result == null) {
                            throw new LoadDataException(LoadDataException.Type.NOT_LOADED_YET);
                        }
                        return result;
                    }
                }
        ), false);
    }

    @SuppressWarnings("Convert2Lambda")
    @NonNull
    @Override
    public Observable<VM> refresh(@NonNull UUID uuid) {
        return fetch(Observable.fromCallable(
                new Callable<ENTITY>() {
                    @Override
                    public ENTITY call() {
                        final ENTITY result = mRepository.readFromCache(uuid);
                        if (result == null) {
                            throw new LoadDataException(LoadDataException.Type.NOT_FOUND);
                        }
                        return result;
                    }
                }
        ), true);
    }
}
