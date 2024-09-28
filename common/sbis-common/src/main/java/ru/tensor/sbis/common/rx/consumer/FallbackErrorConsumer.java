package ru.tensor.sbis.common.rx.consumer;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * Обработчик ошибок, предоставляющий возможность определения действия
 * при возникновении непредвиденных ошибкок в релизной версии приложения.
 *
 * Данный класс потерял актуальность
 *
 * @author am.boldinov
 */
@Deprecated
public class FallbackErrorConsumer extends KFallbackErrorConsumer {

    @NonNull
    public static final KFallbackErrorConsumer DEFAULT = KFallbackErrorConsumer.DEFAULT;

    public FallbackErrorConsumer() {
        super(null);
    }

    public FallbackErrorConsumer(@Nullable String errorMessage) {
        super(errorMessage);
    }
}
