package ru.tensor.sbis.mvp.interactor;

import androidx.annotation.NonNull;

import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Базовый класс для интеракторов
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public abstract class BaseInteractor {

    @NonNull
    protected <T> ObservableTransformer<T, T> getObservableBackgroundSchedulers() {
        return getObservableBackgroundSchedulers(false);
    }

    @NonNull
    protected <T> ObservableTransformer<T, T> getObservableBackgroundSchedulersDelayError() {
        return getObservableBackgroundSchedulers(true);
    }

    @NonNull
    protected <T> ObservableTransformer<T, T> getObservableBackgroundSchedulers(boolean delayError) {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), delayError);
    }

    @NonNull
    protected <T> SingleTransformer<T, T> getSingleBackgroundSchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    protected CompletableTransformer getCompletableBackgroundSchedulers() {
        return completable -> completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    protected <T> ObservableTransformer<T, T> getObservableComputationScheduler() {
        return observable -> observable
                .observeOn(Schedulers.computation());
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    @NonNull
    protected <T> SingleTransformer<T, T> getSingleComputationScheduler() {
        return observable -> observable
                .observeOn(Schedulers.computation());
    }

}
