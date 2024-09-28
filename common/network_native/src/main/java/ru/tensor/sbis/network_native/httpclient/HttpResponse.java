package ru.tensor.sbis.network_native.httpclient;

/**
 * Класс для работы с ответом сервера.
 */
public class HttpResponse {

    private String body;
    private int statusCode;

    public HttpResponse(String body, int statusCode) {
        setBody(body);
        setStatusCode(statusCode);
    }

    private void setBody(String body) {
        this.body = body;
    }

    private void setStatusCode(int code) {
        this.statusCode = code;
    }

    public String getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
