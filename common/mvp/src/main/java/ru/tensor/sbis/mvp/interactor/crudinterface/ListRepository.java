package ru.tensor.sbis.mvp.interactor.crudinterface;

import ru.tensor.sbis.crud.generated.DataRefreshCallback;

/**
 * Репозиторий списка
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface ListRepository<ENTITY_LIST, ENTITY_FILTER> extends BaseListRepository<ENTITY_LIST, ENTITY_FILTER, DataRefreshCallback> {
}
