package ru.tensor.sbis.mvp.interactor.crudinterface.event;

import androidx.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Менеджер событий
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public interface EventManagerServiceSubscriber extends Disposable {

    /**
     * Подписаться на события.
     *
     * @param events - названия событий
     */
    void subscribe(@NonNull String... events);

    /**
     * Отписаться от событий
     *
     * @param events - названия событий
     */
    void unsubscribe(@NonNull String... events);

    /**
     * Получить completable для ожидания подписки
     * на событие с указанным названием.
     *
     * @param eventName - событие, подписка на которое ожидается
     */
    @NonNull
    Completable waitForSubscription(@NonNull String eventName);

    /**
     * Получить observable для происходящих событий.
     */
    @NonNull
    Observable<EventData> getEventDataObservable();
}
