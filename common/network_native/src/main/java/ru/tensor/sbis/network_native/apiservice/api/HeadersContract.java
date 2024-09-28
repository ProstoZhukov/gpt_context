package ru.tensor.sbis.network_native.apiservice.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import ru.tensor.sbis.network_native.apiservice.api.interceptor.InterceptorDelegate;


/**
 * Перечисление с хедерами для сетевых запросов/ответов
 *
 * @author am.boldinov
 */
public enum HeadersContract {

    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_DISPOSITION("Content-Disposition"),
    USER_AGENT("User-Agent"),
    TIMEOUT("Timeout");

    private final String value;
    private InterceptorDelegate interceptorDelegate;

    HeadersContract(String value) {
        this.value = value;
    }

    @NonNull
    public String getOverrideName() {
        return "@:" + value;
    }

    @Nullable
    public InterceptorDelegate getInterceptorDelegate() {
        return interceptorDelegate;
    }

    @Override
    public String toString() {
        return value;
    }

    @NonNull
    public HashMap<String, String> singletonHeader(@NonNull String value) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(getOverrideName(), value);
        return map;
    }

    @SuppressWarnings("unused")
    @NonNull
    public HashMap<String, String> singletonInterceptorHeader(@NonNull InterceptorDelegate delegate) {
        this.interceptorDelegate = delegate;
        return singletonHeader(getOverrideName());
    }
}
