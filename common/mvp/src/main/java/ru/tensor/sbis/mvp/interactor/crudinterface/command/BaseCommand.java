package ru.tensor.sbis.mvp.interactor.crudinterface.command;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import ru.tensor.sbis.mvp.interactor.BaseInteractor;

/**
 * Базовая команда
 *
 * @author am.boldinov
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("RedundantThrows")
public abstract class BaseCommand extends BaseInteractor {

    @NonNull
    private final AtomicBoolean mIsLoading = new AtomicBoolean();

    @NonNull
    protected <T> Observable<T> performAction(@NonNull Observable<T> source) {
        return source.doOnSubscribe(getStartAction())
                .doFinally(getFinishAction());
    }

    @NonNull
    protected <T> Single<T> performAction(@NonNull Single<T> source) {
        return source.doOnSubscribe(getStartAction())
                .doFinally(getFinishAction());
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    @NonNull
    protected Completable performAction(@NonNull Completable source) {
        return source.doOnSubscribe(getStartAction())
                .doFinally(getFinishAction());
    }

    @CallSuper
    protected void onStartCommand() {
        mIsLoading.set(true);
    }

    @CallSuper
    protected void onFinishCommand() {
        mIsLoading.set(false);
    }

    /**
     * @SelfDocumented
     */
    @SuppressWarnings("JavaDoc")
    public boolean isLoading() {
        return mIsLoading.get();
    }

    @SuppressWarnings("Convert2Lambda")
    @NonNull
    private Consumer<Disposable> getStartAction() {
        return new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                onStartCommand();
            }
        };
    }

    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    @NonNull
    private Action getFinishAction() {
        return new Action() {
            @Override
            public void run() throws Exception {
                onFinishCommand();
            }
        };
    }
}
