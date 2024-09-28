package ru.tensor.sbis.common.rx.consumer;

import io.reactivex.functions.Consumer;
import ru.tensor.sbis.common.exceptions.LoadDataException;

/**
 * Абстрактный класс для обработки исключений при процессе загрузки данных.
 *
 * @author am.boldinov
 */
public abstract class LoadingErrorConsumer implements Consumer<Throwable> {

    /**
     * Обработать ожидаемую ошибку загрузки.
     * @param exception - исключение, произошедшее при загрузке
     */
    protected abstract void onLoadException(@androidx.annotation.NonNull LoadDataException exception);

    /**
     * Обработать неожидаемую ошибку загрузки.
     * @param throwable - исключение, произошедшее при загрузке
     */
    protected void onUncheckedException(@androidx.annotation.NonNull Throwable throwable) {
        // ignore by default
    }

    @Override
    public void accept(@androidx.annotation.NonNull Throwable throwable) throws Exception {
        if (throwable instanceof LoadDataException) {
            onLoadException((LoadDataException) throwable);
        } else {
            onUncheckedException(throwable);
        }
    }
}
