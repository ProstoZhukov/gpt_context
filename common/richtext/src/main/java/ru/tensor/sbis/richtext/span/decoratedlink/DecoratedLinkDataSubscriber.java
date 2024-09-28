package ru.tensor.sbis.richtext.span.decoratedlink;

import androidx.annotation.NonNull;

/**
 * Интерфейс для установки колбэка обновления данных по декорированной ссылке
 *
 * @author am.boldinov
 */
@SuppressWarnings("WeakerAccess")
public interface DecoratedLinkDataSubscriber {

    interface DataRefreshCallback {
        void onRefresh(@NonNull DecoratedLinkData linkData);
    }

    /**
     * Присоединяет колбэк обновления данных по ссылке, потокобезопасный.
     * @param url ссылка
     * @param dataRefreshCallback колбэк
     * @param checkMissedEvent true если необходимо проверить кеш пропущенных событий по данному url
     */
    void attachDataRefreshCallback(@NonNull String url, @NonNull DataRefreshCallback dataRefreshCallback, boolean checkMissedEvent);

    /**
     * Отсоединяет колбэк обновления данных по ссылке, потокобезопасный.
     * @param url ссылка
     * @param dataRefreshCallback колбэк
     */
    void detachDataRefreshCallback(@NonNull String url, @NonNull DataRefreshCallback dataRefreshCallback);
}
