package ru.tensor.sbis.network_native.apiservice.api.interceptor;

import androidx.annotation.NonNull;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Interceptor;
import timber.log.Timber;

/**
 * Interceptor для установки таймаута сокету
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public class TimeoutInterceptor implements InterceptorDelegate {

    private final int timeout;

    public TimeoutInterceptor(int timeout, @NonNull TimeUnit unit) {
        this.timeout = (int) unit.toMillis(timeout);
    }

    @Override
    public void intercept(@NonNull Interceptor.Chain chain) {
        final Connection connection = chain.connection();
        if (connection != null) {
            try {
                connection.socket().setSoTimeout(timeout);
            } catch (SocketException e) {
                Timber.e(e);
            }
        }
    }
}
