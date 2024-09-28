package ru.tensor.sbis.network_native.apiservice.api;

import static ru.tensor.sbis.network_native.apiservice.contract.ApiServiceConstKt.NETWORK_ERROR;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Класс, представляющий Exception для работы с сетью
 * <p>
 * Created by kabramov on 07.03.17.
 */
@SuppressWarnings("unused")
public class APIException extends RuntimeException {

    public static final int NO_ERROR_CODE = -1;

    private final int mCode;
    private final RequestResult.Error mApiError;

    public APIException(@Nullable String detailMessage) {
        this(detailMessage, null);
    }

    public APIException(@Nullable Throwable throwable) {
        this(null, throwable);
    }

    public APIException(@Nullable String detailMessage,
                        @Nullable Throwable throwable) {
        super(detailMessage, throwable);
        mCode = NO_ERROR_CODE;
        mApiError = null;
    }

    public APIException(@NonNull RequestResult.Error apiError) {
        super(apiError.errorBodyMessage == null ? apiError.errorMessage
                        : apiError.errorBodyMessage,
                apiError.throwable);
        mCode = apiError.code;
        mApiError = apiError;
    }

    public boolean hasErrorCode() {
        return mCode > 0;
    }

    public int getCode() {
        return mCode;
    }

    public RequestResult.Error getError() {
        return mApiError;
    }

    public Throwable getThrowable() {
        return mApiError != null ? mApiError.throwable : null;
    }

    public static class RecoverableNetworkException extends APIException {

        public RecoverableNetworkException(@NonNull Throwable throwable) {
            this(null, throwable);
        }

        public RecoverableNetworkException(@Nullable String detailMessage,
                                           @Nullable Throwable throwable) {
            super(detailMessage, throwable);
        }

    }

    public static class UnrecoverableNetworkException extends APIException {

        public UnrecoverableNetworkException(@Nullable Throwable throwable) {
            this(throwable != null && throwable.getMessage() != null
                    ? throwable.getMessage() : NETWORK_ERROR, throwable);
        }

        public UnrecoverableNetworkException(@Nullable String detailMessage,
                                             @Nullable Throwable throwable) {
            super(detailMessage, throwable);
        }

        public UnrecoverableNetworkException() {
            super(NETWORK_ERROR);
        }

    }

    public static class UnknownException extends APIException {

        public UnknownException(@NonNull Throwable throwable) {
            super(throwable);
        }

    }

}
