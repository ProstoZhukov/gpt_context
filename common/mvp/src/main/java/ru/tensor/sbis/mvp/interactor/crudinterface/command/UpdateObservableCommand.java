package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import io.reactivex.Single;

/**
 * Команда обновления
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface UpdateObservableCommand<ENTITY> {

    @NonNull
    Single<ENTITY> update(@NonNull ENTITY entity);
}
