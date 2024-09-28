package ru.tensor.sbis.mvp.interactor.crudinterface;

import java.util.UUID;

/**
 * CRUD репозиторий с идентификатором сущности типа {@link UUID}
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface CRUDRepository<ENTITY> extends BaseCRUDRepository<ENTITY, UUID> {
}
