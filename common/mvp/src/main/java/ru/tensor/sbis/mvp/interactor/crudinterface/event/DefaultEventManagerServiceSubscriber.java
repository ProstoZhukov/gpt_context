package ru.tensor.sbis.mvp.interactor.crudinterface.event;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.tensor.sbis.common.data.DependencyCreator;
import ru.tensor.sbis.common.data.DependencyProvider;
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer;
import ru.tensor.sbis.platform.generated.EventCallback;
import ru.tensor.sbis.platform.generated.EventManagerService;
import ru.tensor.sbis.platform.generated.Subscription;
import timber.log.Timber;

/**
 * Дефолтная реализация менеджера событий
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"unused", "RedundantSuppression"})
public class DefaultEventManagerServiceSubscriber implements EventManagerServiceSubscriber {

    @SuppressWarnings("SpellCheckingInspection")
    private enum State {
        SUBSCRIBING,
        SUBSCRIBED,
        UNSUBSCRIBING,
        UNSUBSCRIBED
    }

    @NonNull
    private final PublishSubject<EventData> mExternalEventSubject = PublishSubject.create();
    @NonNull
    private final PublishSubject<StateEvent> mStateEventSubject = PublishSubject.create();
    @NonNull
    private final DependencyProvider<EventManagerService> mEventService;
    private final Map<String, Subscription> mSubscriptionStore = new ConcurrentHashMap<>();
    @Nullable
    private Disposable mEventStateDisposable;

    @SuppressWarnings({"deprecation", "Convert2Lambda", "Anonymous2MethodRef"})
    public DefaultEventManagerServiceSubscriber(@NonNull Context context) {
        mEventService = DependencyProvider.create(new DependencyCreator<EventManagerService>() {
            @NonNull
            @Override
            public EventManagerService create() {
                return EventManagerService.instance();
            }
        });
        mEventStateDisposable = mStateEventSubject.observeOn(Schedulers.single())
                .filter(event -> event.getState() == State.SUBSCRIBING || event.getState() == State.UNSUBSCRIBING)
                .subscribe(new Consumer<StateEvent>() {
                    @SuppressWarnings("RedundantThrows")
                    @Override
                    public void accept(StateEvent stateEvent) throws Exception {
                        if (stateEvent.getState() == State.SUBSCRIBING) {
                            subscribeOnEvents(stateEvent.getEvents());
                            mStateEventSubject.onNext(new StateEvent(State.SUBSCRIBED, stateEvent.getEvents()));
                        } else {
                            unsubscribeFromEvents(stateEvent.getEvents());
                            mStateEventSubject.onNext(new StateEvent(State.UNSUBSCRIBED, stateEvent.getEvents()));
                        }
                        if (isDisposed()) {
                            dispose();
                        }
                    }
                }, FallbackErrorConsumer.DEFAULT);
    }

    @Override
    public void subscribe(@NonNull String... events) {
        mStateEventSubject.onNext(new StateEvent(State.SUBSCRIBING, events));
    }

    @Override
    public void unsubscribe(@NonNull String... events) {
        mStateEventSubject.onNext(new StateEvent(State.UNSUBSCRIBING, events));
    }

    @NonNull
    @Override
    public Completable waitForSubscription(@NonNull String eventName) {
        return mStateEventSubject
                // Обрабатываем только SUBSCRIBED
                .filter(e -> e.getState() == State.SUBSCRIBED)
                // Проверяем наличие названия события в подписанных
                .filter(e -> contains(e.getEvents(), eventName))
                .take(1)
                .ignoreElements()
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void dispose() {
        mStateEventSubject.onComplete();
        mExternalEventSubject.onComplete();
        if (mEventStateDisposable != null) {
            mEventStateDisposable.dispose();
            mEventStateDisposable = null;
        }
        mSubscriptionStore.clear();
    }

    @Override
    public boolean isDisposed() {
        return mEventStateDisposable == null || mEventStateDisposable.isDisposed();
    }

    @NonNull
    @Override
    public Observable<EventData> getEventDataObservable() {
        return mExternalEventSubject.observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Выполнить подписку на события.
     *
     * @param events - названия событий
     */
    private void subscribeOnEvents(@NonNull String[] events) {
        for (String event : events) {
            final Subscription subscription = mSubscriptionStore.get(event);
            if (subscription == null) {
                subscribeOnEvent(event, mEventService.get());
            } else {
                subscription.enable();
            }
        }
    }

    /**
     * Выполнить отписку от событий.
     *
     * @param events - названия событий
     */
    private void unsubscribeFromEvents(@NonNull String[] events) {
        for (String event : events) {
            final Subscription subscription = mSubscriptionStore.get(event);
            if (subscription != null) {
                subscription.disable();
            }
        }
    }

    private void subscribeOnEvent(@NonNull String event, @NonNull EventManagerService eventManagerService) {
        mSubscriptionStore.put(event, eventManagerService.addEventCallback(event, new EventCallback() {
            @Override
            public void onEvent(String s, HashMap<String, String> hashMap) {
                try {
                    mExternalEventSubject.onNext(new EventData(s, hashMap));
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }));
    }

    private static boolean contains(@NonNull String[] array, @NonNull String target) {
        for (String item : array) {
            if (target.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private static final class StateEvent {
        @NonNull
        private final String[] mEvents;
        @NonNull
        private final State mState;

        StateEvent(@NonNull State state, @NonNull String... events) {
            mEvents = events;
            mState = state;
        }

        @NonNull
        public State getState() {
            return mState;
        }

        @NonNull
        public String[] getEvents() {
            return mEvents;
        }
    }
}
