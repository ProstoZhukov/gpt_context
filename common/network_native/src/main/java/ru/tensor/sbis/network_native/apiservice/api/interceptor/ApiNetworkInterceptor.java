package ru.tensor.sbis.network_native.apiservice.api.interceptor;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import ru.tensor.sbis.network_native.apiservice.api.HeadersContract;
import ru.tensor.sbis.network_native.httpclient.Server;

/**
 * Interceptor для добавления необходимых данных в заголовкки сетевого запроса
 *
 * @author am.boldinov
 */
public class ApiNetworkInterceptor implements Interceptor {

    @Override
    public @NotNull Response intercept(@NonNull Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();
        for (HeadersContract header : HeadersContract.values()) {
            final String value = originalRequest.header(header.getOverrideName());
            if (value != null) {
                requestBuilder.removeHeader(header.getOverrideName());
                if (header.getInterceptorDelegate() != null) {
                    header.getInterceptorDelegate().intercept(chain);
                } else {
                    requestBuilder.header(header.toString(), value);
                }
            }
        }
        requestBuilder.removeHeader(HeadersContract.USER_AGENT.toString())
                .addHeader(HeadersContract.USER_AGENT.toString(), Server.getUserAgent());
        return chain.proceed(requestBuilder.build());
    }
}