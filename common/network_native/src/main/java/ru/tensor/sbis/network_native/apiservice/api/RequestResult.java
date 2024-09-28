package ru.tensor.sbis.network_native.apiservice.api;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Legacy-код
 * <p>
 * Результат выполнения сетевого запроса
 * <p>
 * Created by ss.buvaylink on 24.10.2016.
 */
@SuppressWarnings("unused")
public class RequestResult<Data> {

    public Data result;
    public Bundle error;
    public Error errorData;
    private boolean hasMore;

    public RequestResult() {
    }

    public RequestResult(@Nullable Data result,
                         @NonNull Bundle error,
                         boolean hasMore) {
        this.result = result;
        this.error = error;
        this.hasMore = hasMore;
    }

    public RequestResult(@Nullable Data result,
                         @NonNull Bundle error) {
        this.result = result;
        this.error = error;
    }

    public RequestResult(@Nullable Data result,
                         boolean hasMore) {
        this.result = result;
        this.hasMore = hasMore;
    }

    public RequestResult(@Nullable Data result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return error == null && errorData == null;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public boolean hasThrowable() {
        return errorData != null && errorData.throwable != null;
    }

    public <Result> RequestResult<Result> copyToAnotherResult(@NonNull Result newResultData, boolean hasMore) {
        return new RequestResult<>(newResultData, error, hasMore);
    }

    public <Result> RequestResult<Result> copyToAnotherResult(@NonNull Result newResultData) {
        return new RequestResult<>(newResultData, error, hasMore);
    }

    public static class Error {

        public int code;
        public String errorMessage;

        public int bodyCode;
        public String errorBodyMessage;
        public String errorBodyMessageDetails;

        public Throwable throwable;

        public Error(int code) {
            this.code = code;
        }

        public Error(int code, @Nullable String errorMessage) {
            this.code = code;
            this.errorMessage = errorMessage;
        }

        public Error(int code, @NonNull Throwable throwable) {
            this.code = code;
            this.errorMessage = throwable.getMessage();
            this.throwable = throwable;
        }

        public boolean hasBodyError() {
            return bodyCode != 0 || errorBodyMessage != null || errorBodyMessageDetails != null;
        }


    }

}
