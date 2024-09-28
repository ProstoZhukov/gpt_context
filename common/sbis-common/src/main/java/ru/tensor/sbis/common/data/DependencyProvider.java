package ru.tensor.sbis.common.data;

import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import java.lang.Exception;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author am.boldinov
 */
public final class DependencyProvider<T> {

    @NonNull
    public static <T> DependencyProvider<T> create(@NonNull DependencyCreator<T> provider) {
        return new DependencyProvider<>(provider);
    }

    @NonNull
    private final DependencyCreator<T> mDependencyCreator;
    private volatile T mDependency;

    private DependencyProvider(@NonNull DependencyCreator<T> provider) {
        mDependencyCreator = provider;
    }

    @WorkerThread
    @NonNull
    public T get() {
        if (mDependency == null) {
            synchronized (this) {
                if (mDependency == null) {
                    ensureWorkerThread();
                    mDependency = mDependencyCreator.create();
                }
            }
        }
        return mDependency;
    }

    @UiThread
    @NonNull
    public Observable<T> getAsync() {
        Observable<T> observable;
        if (mDependency != null) {
            observable = Observable.just(mDependency);
        } else {
            observable = Observable.fromCallable(this::get);
        }
        return observable.subscribeOn(Schedulers.io());
    }

    @WorkerThread
    @Deprecated
    //Метод временный, в 1.13 будет удален
    public void destroyDependency() {
        ensureWorkerThread();
        if (mDependency != null) {
            synchronized (this) {
                mDependency = null;
            }
        }
    }

    private static void ensureWorkerThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new EnsureWorkerThreadException();
        }
    }
}
