package ru.tensor.sbis.common.rx.subscribe;

import androidx.annotation.NonNull;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

/**
 * Обертка для безопасной обработки {@link Exception} после отписки от источника
 *
 * @author am.boldinov
 */
public abstract class SafeObservableOnSubscribe<T> implements ObservableOnSubscribe<T> {

    @Override
    public final void subscribe(ObservableEmitter<T> e) throws Exception {
        try {
            process(e);
        } catch (Exception ex) {
            onError(ex, e);
        }
    }

    protected abstract void process(@NonNull ObservableEmitter<T> e) throws Exception;

    protected void onError(@NonNull Exception ex, @NonNull ObservableEmitter<T> e) {
        if (!e.isDisposed()) {
            e.onError(ex);
        } else {
            Timber.w(ex);
        }
    }
}
