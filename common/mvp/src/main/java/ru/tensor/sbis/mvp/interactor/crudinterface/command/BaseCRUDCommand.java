package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository;

/**
 * Базовая реализация команды для CRUD
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public abstract class BaseCRUDCommand<ENTITY> extends BaseCommand {

    @NonNull
    protected final CRUDRepository<ENTITY> mRepository;

    public BaseCRUDCommand(@NonNull CRUDRepository<ENTITY> repository) {
        super();
        mRepository = repository;
    }
}
