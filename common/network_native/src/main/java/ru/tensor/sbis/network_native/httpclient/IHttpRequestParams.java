package ru.tensor.sbis.network_native.httpclient;

/**
 * Интерфейс описания запроса. Содержит информацию о том на какой сайт и его сервис отрпавить запрос
 */
public interface IHttpRequestParams {

    /**
     * Адрес сайта
     */
    String getHost();

    /**
     * Выставить адрес сайта
     *
     * @param host адрес сайта
     */
    @SuppressWarnings("unused")
    void setHost(String host);

    /**
     * Адрес сервиса
     */
    String getService();

    /**
     * Выставить адрес сервиса
     *
     * @param service адрес сервиса
     */
    @SuppressWarnings("unused")
    void setService(String service);

    /**
     * Протокол передачи данных для этого запроса
     */
    HttpProtocol getProtocol();

    /**
     * Выставить протокол передачи данных для этого запроса
     *
     * @param protocol протокол передачи данных
     */
    @SuppressWarnings("unused")
    void setProtocol(HttpProtocol protocol);
}
