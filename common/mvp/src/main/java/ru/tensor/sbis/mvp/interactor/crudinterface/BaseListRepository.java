package ru.tensor.sbis.mvp.interactor.crudinterface;

import androidx.annotation.NonNull;

import ru.tensor.sbis.platform.generated.Subscription;

/**
 * Базовый репозиторий списка
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public interface BaseListRepository<ENTITY_LIST, ENTITY_FILTER, DATA_REFRESH_CALLBACK> {

    /**
     * @SelfDocumented
     */
    @NonNull
    ENTITY_LIST list(@NonNull ENTITY_FILTER filter);

    /**
     * @SelfDocumented
     */
    @NonNull
    ENTITY_LIST refresh(@NonNull ENTITY_FILTER filter);

    @NonNull
    default ru.tensor.sbis.platform.generated.Subscription setDataRefreshCallback(@NonNull DATA_REFRESH_CALLBACK callback) {
        throw new RuntimeException("Not implemented");
    }

    @NonNull
    default Subscription subscribeDataRefreshedEvent(@NonNull DATA_REFRESH_CALLBACK callback) {
        throw new RuntimeException("Not implemented");
    }

}
