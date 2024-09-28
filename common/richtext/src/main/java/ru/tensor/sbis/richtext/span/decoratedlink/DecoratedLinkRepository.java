package ru.tensor.sbis.richtext.span.decoratedlink;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

/**
 * Репозиторий для получения данных о декорированной ссылке
 * <p>
 * @author am.boldinov
 */
public interface DecoratedLinkRepository extends DecoratedLinkDataSubscriber {

    /**
     * Возвращает данные для декорации ссылки
     */
    @WorkerThread
    @NonNull
    DecoratedLinkData getLinkData(@NonNull String url);

    /**
     * Возвращает данные для декорации ссылки в тексте (внутри строки)
     */
    @WorkerThread
    @NonNull
    DecoratedLinkData getInlineLinkData(@NonNull String url, @NonNull String decorationJson);
}
