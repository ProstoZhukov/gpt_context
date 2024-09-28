package ru.tensor.sbis.network_native.httpclient;

/**
 * Протокол передачи данных
 */
public enum HttpProtocol {
    HTTP("http"),
    HTTPS("https");

    final String protocolName;

    HttpProtocol(String name) {
        protocolName = name;
    }

    public String getProtocolName() {
        return protocolName;
    }
}
