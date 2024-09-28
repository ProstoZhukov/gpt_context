package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import io.reactivex.Single;

/**
 * Команда создания
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface CreateObservableCommand<ENTITY> {

    /**
     * @SelfDocumented
     */
    @NonNull
    Single<ENTITY> create();

}
