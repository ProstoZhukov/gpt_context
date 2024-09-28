package ru.tensor.sbis.network_native.httpclient;

import ru.tensor.sbis.network_native.error.ISbisError;

public interface HttpResponseCallback {

    void onFailure(ISbisError error);

    void onResponse(HttpResponse response);
}