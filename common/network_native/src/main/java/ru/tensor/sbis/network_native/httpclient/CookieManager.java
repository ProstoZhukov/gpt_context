package ru.tensor.sbis.network_native.httpclient;

import androidx.annotation.Nullable;

import okhttp3.CookieJar;
import ru.tensor.sbis.plugin_struct.feature.Feature;

/**
 * Интерфейс для управления куками
 */
public interface CookieManager extends CookieJar, Feature {

    /**
     * Получить куки
     *
     * @return строка с куками
     */
    @Nullable
    String getFormattedCookie();

    /**
     * Получить токен
     *
     * @return строка с токеном
     */
    @Nullable
    String getTokenId();

}
