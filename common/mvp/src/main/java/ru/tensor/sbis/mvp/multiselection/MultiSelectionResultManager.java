package ru.tensor.sbis.mvp.multiselection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"JavaDoc", "CanBeFinal"})
public abstract class MultiSelectionResultManager<T> {

    protected BehaviorSubject<T> mSelectionSubject;

    protected Observable<T> mSelectionObservable;

    public MultiSelectionResultManager() {
        mSelectionSubject = BehaviorSubject.create();
        mSelectionObservable = mSelectionSubject
                .distinctUntilChanged()
                .doOnDispose(this::clearSelectionResult)
                .replay(1)
                .autoConnect();
    }

    /**
     * @SelfDocumented
     */
    public Observable<T> getSelectionDoneObservable() {
        return mSelectionObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .skip(1);
    }

    /**
     * @SelfDocumented
     */
    public void putNewData(@Nullable T selectionResult) {
        if (selectionResult == null) {
            clearSelectionResult();
        } else {
            mSelectionSubject.onNext(selectionResult);
        }
    }

    /**
     * @SelfDocumented
     */
    @NonNull
    public T getSelectionResult() {
        return mSelectionSubject.getValue();
    }

    /**
     * @SelfDocumented
     */
    public abstract void clearSelectionResult();

}
