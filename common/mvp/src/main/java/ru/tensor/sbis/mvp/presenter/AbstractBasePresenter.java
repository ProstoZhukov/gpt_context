package ru.tensor.sbis.mvp.presenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.disposables.Disposable;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("RedundantSuppression")
public abstract class AbstractBasePresenter<VIEW, EventData> implements BasePresenter<VIEW> {

    @Nullable
    private final EventManagerSubscriber<EventData> mEventManagerSubscriber;

    @Nullable
    protected Disposable mEventManagerSubscription;

    @Nullable
    protected VIEW mView;

    protected AbstractBasePresenter(@Nullable EventManagerSubscriber<EventData> eventManagerSubscriber) {
        mEventManagerSubscriber = eventManagerSubscriber;
    }

    /**
     * @param eventManagerSubscriber подписчик на события
     * @param subscribeOnInit        true если необходимо опдписаться сразу при создании презентера
     */
    protected AbstractBasePresenter(@Nullable EventManagerSubscriber<EventData> eventManagerSubscriber,
                                    boolean subscribeOnInit) {
        this(eventManagerSubscriber);
        if (eventManagerSubscriber != null && subscribeOnInit) {
            subscribeEventManager();
        }
    }

    @Override
    public void attachView(@NonNull VIEW view) {
        mView = view;

        if (isNeedToDisplayViewState()) {
            displayViewState(view);
        }
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        if (mEventManagerSubscription != null) {
            mEventManagerSubscription.dispose();
        }
    }

    //region EventManager

    /**
     * Подписка на события (по умолчанию)
     */
    protected final void subscribeEventManager() {
        subscribeEventManager(getEventManagerKey(), getEventManagerRelevantEntity());
    }

    /**
     * Подписка на события (по требованию)
     *
     * @param eventListenerKey ключ-идентификатор подписчика
     * @param relevantEntity   идентификатор объекта, чьи события слушаем
     */
    protected void subscribeEventManager(@Nullable String eventListenerKey, @Nullable String relevantEntity) {
        if (mEventManagerSubscription != null) {
            mEventManagerSubscription.dispose();
        }

        //noinspection ConstantConditions
        if (eventListenerKey == null) {
            throw new IllegalStateException("key must not be null if getEventManagerRegistrar is implemented");
        }

        if (relevantEntity == null || relevantEntity.isEmpty()) {
            throw new IllegalStateException("relevant entity can not be empty or null");
        }

        //noinspection ConstantConditions
        mEventManagerSubscription = mEventManagerSubscriber.subscribe(
                eventListenerKey,
                relevantEntity,
                this::onEvent
        );
    }

    /**
     * Use this if you want to unsubscribe manually
     * By default a disposing of subscription is made in onDestroy
     */
    @SuppressWarnings("unused")
    protected void unsubscribeEventManager() {
        if (mEventManagerSubscription != null) {
            mEventManagerSubscription.dispose();
        }
    }

    @NonNull
    protected String getEventManagerKey() {
        return getClass().getSimpleName();
    }

    @Nullable
    protected String getEventManagerRelevantEntity() {
        return null;
    }

    protected void onEvent(@NonNull EventData eventData) {
        //no base impl
    }

    //endregion

    //region Displaying Data

    protected boolean isNeedToDisplayViewState() {
        return false;
    }

    protected void displayViewState(@NonNull VIEW view) {
        //no base impl
    }

    //endregion

}
