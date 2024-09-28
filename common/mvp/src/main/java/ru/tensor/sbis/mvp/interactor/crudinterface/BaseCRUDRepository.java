package ru.tensor.sbis.mvp.interactor.crudinterface;

import androidx.annotation.NonNull;

/**
 * Интерфейс CRUD репозитория
 * <p>
 * {@link ENTITY} тип сущности
 * {@link UUID} уникальный идентификатор для работы с сущностью
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface BaseCRUDRepository<ENTITY, UUID> {

    /**
     * @SelfDocumented
     */
    ENTITY create();

    /**
     * @SelfDocumented
     */
    ENTITY read(@NonNull UUID uuid);

    // todo rename to refresh like term

    /**
     * @SelfDocumented
     */
    ENTITY readFromCache(@NonNull UUID uuid);

    /**
     * @SelfDocumented
     */
    ENTITY update(@NonNull ENTITY entity);

    /**
     * @SelfDocumented
     */
    Boolean delete(@NonNull UUID uuid);

}
