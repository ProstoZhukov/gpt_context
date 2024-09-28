package ru.tensor.sbis.network_native.event;

/**
 * Legacy-код
 * <p>
 * Created by ss.buvaylink on 14.04.2016.
 */
public class RequestAuthTokenEvent {

    @SuppressWarnings("EmptyMethod")
    public interface AuthListener {
        void onSuccess();

        void onFailure();
    }

    public final AuthListener authListener;

    public RequestAuthTokenEvent(AuthListener authListener) {
        this.authListener = authListener;
    }
}
