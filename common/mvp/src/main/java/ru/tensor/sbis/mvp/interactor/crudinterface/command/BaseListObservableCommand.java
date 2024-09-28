package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import ru.tensor.sbis.platform.generated.Subscription;

/**
 * Моковый подписчик
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
class MockSubscription extends Subscription {
    @Override
    public void enable() {
    }

    /**
     * @SelfDocumented
     */
    public void disable() {

    }
}

/**
 * Базовая реализация команды списка с RX
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface BaseListObservableCommand<ENTITY_LIST, ENTITY_FILTER, DATA_REFRESH_CALLBACK> {

    @NonNull
    Observable<ENTITY_LIST> list(@NonNull ENTITY_FILTER filter);

    @NonNull
    Observable<ENTITY_LIST> refresh(@NonNull ENTITY_FILTER filter);

    @NonNull
    default Observable<Subscription> setDataRefreshCallback(@NonNull DATA_REFRESH_CALLBACK callback) {
        return Observable.just(new MockSubscription());
    }

    @NonNull
    default Observable<Subscription> subscribeDataRefreshedEvent(@NonNull DATA_REFRESH_CALLBACK callback) {
        return Observable.just(new MockSubscription());
    }
}
