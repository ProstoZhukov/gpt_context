package ru.tensor.sbis.network_native.apiservice.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Legacy-код
 * <p>
 * Класс, представляющий тело сетевого запроса
 * <p>
 * Created by ss.buvaylink on 12.11.2015.
 */
public class MethodRequestBody {

    private static final Object EMPTY_OBJECT = new Object();

    @NonNull
    public final String jsonrpc;
    public final int protocol;
    public final String method;
    @NonNull
    public final Object params;
    public int id = 1;

    public MethodRequestBody(String method, @Nullable Object params) {
        this.jsonrpc = "2.0";
        this.protocol = 4;
        this.method = method;
        this.params = params != null ? params : EMPTY_OBJECT;
    }
}
