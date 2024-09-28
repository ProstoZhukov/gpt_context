package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import java.util.UUID;

import io.reactivex.functions.Function;
import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository;

/**
 * Реализация команды чтения из репозитория по ключу типа {@link UUID}.
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"RedundantThrows", "unused", "RedundantSuppression"})
public class ReadCommand<VM, ENTITY> extends BaseReadCommand<VM, ENTITY, UUID> implements ReadObservableCommand<VM> {

    public ReadCommand(@NonNull CRUDRepository<ENTITY> repository,
                       @NonNull Function<ENTITY, VM> mapper) {
        super(repository, mapper);
    }
}
