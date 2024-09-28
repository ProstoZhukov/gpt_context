package ru.tensor.sbis.network_native.apiservice.api.interceptor;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;

/**
 * Интерфейс для модификации заголовков запроса/ответа
 *
 * @author am.boldinov
 */
public interface InterceptorDelegate {

    /**
     * Перехватывает запрос/ответ
     *
     * @param chain запроса/ответа
     */
    void intercept(@NonNull Interceptor.Chain chain);
}
