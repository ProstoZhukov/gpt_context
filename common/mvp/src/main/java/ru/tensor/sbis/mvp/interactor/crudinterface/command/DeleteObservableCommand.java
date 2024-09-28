package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import java.util.UUID;

import io.reactivex.Single;

/**
 * Команда удаления
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface DeleteObservableCommand {

    @NonNull
    Single<Boolean> delete(@NonNull UUID uuid);
}
