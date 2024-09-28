package ru.tensor.sbis.network_native.apiservice.event;

import android.os.Bundle;

/**
 * Legacy-код
 * <p>
 * Created by ss.buvaylink on 14.12.2015.
 */
public class RequestIsDoneEvent {

    private final int mResultCode;
    private final Bundle mBundle;

    public RequestIsDoneEvent(int resultCode, Bundle bundle) {
        mResultCode = resultCode;
        mBundle = bundle;
    }

    public int getResultCode() {
        return mResultCode;
    }

    @SuppressWarnings("unused")
    public Bundle getBundle() {
        return mBundle;
    }
}
