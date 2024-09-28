package ru.tensor.sbis.network_native.httpclient;

import androidx.annotation.NonNull;

/**
 * Класс для формирования параметров запроса к серверу: хост, проктокол и т.п.
 */
public class HttpRequestParams implements IHttpRequestParams {

    private String host;
    private String service;
    private HttpProtocol protocol;

    /**
     * Конструктор
     *
     * @param host    адрес сервера
     * @param service адрес сервиса
     */
    public HttpRequestParams(String host, String service) {
        this(host, service, HttpProtocol.HTTPS);
    }

    /**
     * Конструктор
     *
     * @param host     адрес сервера
     * @param service  адрес сервиса
     * @param protocol протокол обмена
     */
    public HttpRequestParams(String host, String service, HttpProtocol protocol) {
        this.host = host;
        this.service = service;
        this.protocol = protocol;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public void setService(String service) {
        this.service = service;
    }

    @Override
    public HttpProtocol getProtocol() {
        return protocol;
    }

    @SuppressWarnings("unused")
    @Override
    public void setProtocol(@NonNull HttpProtocol protocol) {
        switch (protocol) {
            case HTTP:
                this.protocol = protocol;
                break;
            case HTTPS:
            default:
                this.protocol = HttpProtocol.HTTPS;
        }
    }
}
