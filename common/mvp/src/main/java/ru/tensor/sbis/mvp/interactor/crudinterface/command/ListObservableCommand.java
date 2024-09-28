package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import ru.tensor.sbis.crud.generated.DataRefreshCallback;

/**
 * Команда списка
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface ListObservableCommand<ENTITY_LIST, ENTITY_FILTER> extends BaseListObservableCommand<ENTITY_LIST, ENTITY_FILTER, DataRefreshCallback> {
}
