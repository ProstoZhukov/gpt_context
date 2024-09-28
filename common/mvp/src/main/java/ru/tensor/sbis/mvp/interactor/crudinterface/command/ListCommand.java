package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import io.reactivex.functions.Function;
import ru.tensor.sbis.crud.generated.DataRefreshCallback;
import ru.tensor.sbis.mvp.data.model.PagedListResult;
import ru.tensor.sbis.mvp.interactor.crudinterface.ListRepository;

/**
 * Реализация команды списка
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public class ListCommand<VM, ENTITY_LIST, ENTITY_FILTER>
        extends BaseListCommand<VM, ENTITY_LIST, ENTITY_FILTER, DataRefreshCallback>
        implements ListObservableCommand<PagedListResult<VM>, ENTITY_FILTER> {

    public ListCommand(@NonNull ListRepository<ENTITY_LIST, ENTITY_FILTER> repository,
                       @NonNull Function<ENTITY_LIST, PagedListResult<VM>> listMapper) {
        super(repository, listMapper);
    }

}
