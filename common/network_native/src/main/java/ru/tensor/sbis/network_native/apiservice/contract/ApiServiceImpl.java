package ru.tensor.sbis.network_native.apiservice.contract;

import static ru.tensor.sbis.network_native.apiservice.contract.ApiServiceConstKt.FRESCO_READ_TIMEOUT_SECS;
import static ru.tensor.sbis.network_native.apiservice.contract.ApiServiceConstKt.NETWORK_ERROR;
import static ru.tensor.sbis.network_native.apiservice.contract.ApiServiceConstKt.SERVER_CONNECT_ERROR;
import static ru.tensor.sbis.network_native.apiservice.contract.ApiServiceConstKt.TIMEOUT_SECS;
import static ru.tensor.sbis.network_native.apiservice.contract.ApiServiceConstKt.UNKNOWN_ERROR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import kotlin.Lazy;
import kotlin.LazyKt;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.StreamResetException;
import okio.Buffer;
import ru.tensor.sbis.network_native.apiservice.WorkerService;
import ru.tensor.sbis.network_native.apiservice.api.APIException;
import ru.tensor.sbis.network_native.apiservice.api.MethodRequestBody;
import ru.tensor.sbis.network_native.apiservice.api.RequestResult;
import ru.tensor.sbis.network_native.apiservice.api.certificate.SSLCertificateResolver;
import ru.tensor.sbis.network_native.apiservice.api.certificate.SSLCertificateUtil;
import ru.tensor.sbis.network_native.apiservice.api.interceptor.ApiNetworkInterceptor;
import ru.tensor.sbis.network_native.httpclient.CookieManager;
import ru.tensor.sbis.network_native.httpclient.Server;
import ru.tensor.sbis.network_native.parser.BaseModelListDeserializer;
import ru.tensor.sbis.network_native.parser.model.BaseModelList;
import ru.tensor.sbis.network_native.parser.model.SingleResult;
import timber.log.Timber;

/**
 * Legacy-код
 * <p>
 * Сервис для работы с сетевыми запросами
 * <p>
 * Created by ss.buvaylink on 06.10.2015.
 * NOTE: do not refactor API as we will migrate to C++ core
 */
@SuppressWarnings("unused")
class ApiServiceImpl implements ApiService {

    private static final boolean DEBUG_REQUEST = false;

    private static volatile ApiServiceImpl sApiService;

    @NonNull
    private final Gson mGson;

    @NonNull
    private final SSLCertificateResolver mCertificateResolver;

    private String mBaseUrl;

    private CookieManager mCookieManager;

    @NonNull
    private final Lazy<OkHttpClient> mOkHttpClient =
            LazyKt.lazy(() -> createClient(mCookieManager, true));

    @NonNull
    private final Lazy<OkHttpClient> mOkHttpClientWithoutTokenId =
            LazyKt.lazy(() -> createClient(mCookieManager, false));

    @NonNull
    private final Lazy<OkHttpClient.Builder> mBuilderWithInjectedSSLCertificate =
            LazyKt.lazy(this::createOkHttpClientBuilder);

    ApiServiceImpl(@NonNull Context context, @NonNull CookieManager cookieManager) {
        mGson = createGson();
        mCertificateResolver = SSLCertificateUtil.getCertificateResolver(context);
        mCookieManager = cookieManager;
    }

    @NotNull
    @Override
    public SSLCertificateResolver getCertificateResolver() {
        return mCertificateResolver;
    }

    @NotNull
    @Override
    public OkHttpClient.Builder getFrescoOkHttpClient() {
        OkHttpClient.Builder builder = getOkHttpClientBuilder();
        builder.readTimeout(FRESCO_READ_TIMEOUT_SECS, TimeUnit.SECONDS);
        if (DEBUG_REQUEST) {
            builder.addInterceptor(new ApiServiceImpl.LoggingInterceptor());
        }
        builder.addNetworkInterceptor(new ApiNetworkInterceptor());
        return builder;
    }

    @NotNull
    @Override
    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return mBuilderWithInjectedSSLCertificate.getValue();
    }

    @NotNull
    @Override
    public OkHttpClient getHttpClient() {
        return mOkHttpClient.getValue();
    }

    @Override
    public void setBaseUrl(@Nullable String baseUrl) {
        mBaseUrl = baseUrl;
    }

    @Override
    public boolean isPrimed() {
        return mBaseUrl != null && !mBaseUrl.isEmpty();
    }

    @Override
    public <T> void request(@NonNull Call call, @NonNull Class<T> clazz, @NonNull ResultListener<T> callback) {
        checkCallback(callback);
        ResponseBody responseBody = null;
        int code = 0;
        try {
            Response response = call.execute();

            responseBody = response.body();
            code = response.code();
            String responseBodyString = responseBody != null ? responseBody.string() : null;
            if (response.isSuccessful() || response.message().equals("Validation required")) {
                JsonObject jsonObject = getErrorBodyObject(responseBodyString);
                if (jsonObject == null) {
                    T result = mGson.fromJson(responseBodyString, clazz);
                    callback.onSuccess(result);
                } else {
                    callback.onFailure(parseError(jsonObject, response.message(), code));
                }
            } else {
                callback.onFailure(parseError(getErrorBodyObject(responseBodyString), response.message(), code));
            }
        } catch (UnknownHostException | InterruptedIOException | StreamResetException e) {
            callback.onFailure(networkErrorBundle(code));
            Timber.d(e);
        } catch (IOException e) {
            callback.onFailure(serverErrorBundle(code));
            Timber.d(e);
        } catch (Throwable e) {
            callback.onFailure(unknownErrorBundle(code));
            Timber.e(e);
        } finally {
            closeBody(responseBody);
        }
    }

    @Override
    public void request(@NonNull Call call, @NonNull ResultListener<String> callback) {
        checkCallback(callback);
        ResponseBody responseBody = null;
        int code = 0;
        try {
            Response response = call.execute();
            responseBody = response.body();
            code = response.code();
            String responseBodyString = responseBody != null ? responseBody.string() : null;
            if (response.isSuccessful() || response.message().equals("Validation required")) {
                JsonObject jsonObject = getErrorBodyObject(responseBodyString);
                if (jsonObject == null) {
                    callback.onSuccess(responseBodyString);
                } else {
                    callback.onFailure(parseError(jsonObject, response.message(), code));
                }
            } else {
                callback.onFailure(parseError(getErrorBodyObject(responseBodyString), response.message(), code));
            }
        } catch (UnknownHostException | InterruptedIOException | StreamResetException e) {
            callback.onFailure(networkErrorBundle(code));
            Timber.d(e);
        } catch (IOException e) {
            callback.onFailure(serverErrorBundle(code));
            Timber.d(e);
        } catch (Throwable e) {
            callback.onFailure(unknownErrorBundle(code));
            Timber.e(e);
        } finally {
            closeBody(responseBody);
        }
    }

    @Nullable
    @Override
    public <T> T syncRequest(@NonNull Call call, @NonNull Class<T> clazz) {
        T result = null;
        ResponseBody responseBody = null;
        try {
            Response response = call.execute();
            responseBody = response.body();
            if (response.isSuccessful() || response.message().equals("Validation required")) {
                result = responseBody != null ? mGson.fromJson(responseBody.string(), clazz) : null;
            }
        } catch (IOException e) {
            Timber.d(e);
        } finally {
            closeBody(responseBody);
        }
        return result;
    }

    @WorkerThread
    @NonNull
    @Override
    public RequestResult<BaseModelList> requestList(@NonNull MethodRequestBody requestBody, @NonNull String urlPath) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        return request(call, BaseModelList.class);
    }

    @WorkerThread
    @NonNull
    @Override
    public RequestResult<SingleResult> requestResult(@NonNull MethodRequestBody requestBody, @NonNull String urlPath) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        return request(call, SingleResult.class);
    }

    @WorkerThread
    @NonNull
    @Override
    public RequestResult<ResponseBody> requestRaw(@NonNull MethodRequestBody requestBody, @NonNull String urlPath) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        return requestRaw(call);
    }

    @WorkerThread
    @NonNull
    @Override
    public RequestResult<ResponseBody> requestRaw(@NonNull RequestBody requestBody, @NonNull String urlPath) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        return requestRaw(call);
    }

    @Override
    public void requestSingleResult(MethodRequestBody requestBody, String urlPath, @NonNull ResultListener<SingleResult> callback) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        request(call, SingleResult.class, callback);
    }

    @Override
    public void requestSingleResult(@Nullable Map<String, String> headers, MethodRequestBody requestBody, String urlPath, @NonNull ResultListener<SingleResult> callback) {
        if (headers != null) {
            Call call = createCallPost(Headers.of(headers), requestBody, getRequestUrl(urlPath));
            request(call, SingleResult.class, callback);
        } else {
            requestSingleResult(requestBody, urlPath, callback);
        }
    }

    @Nullable
    @Override
    public SingleResult syncRequestSingleResult(MethodRequestBody requestBody, String urlPath) {
        return syncRequestSingleResult(null, requestBody, urlPath);
    }

    @Nullable
    @Override
    public SingleResult syncRequestSingleResult(@Nullable Map<String, String> headers, MethodRequestBody requestBody, String urlPath) {
        final Call call;
        if (headers != null) {
            call = createCallPost(Headers.of(headers), requestBody, getRequestUrl(urlPath));
        } else {
            call = createCallPost(requestBody, getRequestUrl(urlPath));
        }
        return syncRequest(call, SingleResult.class);
    }

    @Override
    public void requestRawResult(MethodRequestBody requestBody, String urlPath, @NonNull ResultListener<String> callback) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        request(call, callback);
    }

    @Override
    public void requestRawResult(@NonNull MethodRequestBody requestBody, @NonNull String urlPath, @NonNull ResultListener<String> callback, @NonNull Server.Host host) {
        String requestUrl = host.getFullHostUrl() + "/" + urlPath;
        RequestBody body = createJsonRequestBody(requestBody);
        Call call = createCallPostWithoutToken(body, requestUrl);
        request(call, callback);
    }

    @Override
    public void requestList(MethodRequestBody requestBody, String urlPath, @NonNull ResultListener<BaseModelList> callback) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        request(call, BaseModelList.class, callback);
    }

    @Override
    public void requestList(@Nullable Map<String, String> headers, MethodRequestBody requestBody, String urlPath, @NonNull ResultListener<BaseModelList> callback) {
        if (headers != null) {
            Call call = createCallPost(Headers.of(headers), requestBody, getRequestUrl(urlPath));
            request(call, BaseModelList.class, callback);
        } else {
            requestList(requestBody, urlPath, callback);
        }
    }

    @Nullable
    @Override
    public BaseModelList syncRequestList(MethodRequestBody requestBody, String urlPath) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        return syncRequest(call, BaseModelList.class);
    }

    @Nullable
    @Override
    public <T> T syncRequest(MethodRequestBody requestBody, String urlPath, @NonNull Class<T> clazz) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        return syncRequest(call, clazz);
    }

    @Nullable
    @Override
    public SingleResult syncRequestList(RequestBody requestBody, String urlPath) {
        Call call = createCallPost(requestBody, getRequestUrl(urlPath));
        return syncRequest(call, SingleResult.class);
    }

    private OkHttpClient.Builder createOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        mCertificateResolver.inject(builder);
        return builder;
    }

    private String getRequestUrl(String path) {
        return mBaseUrl + path;
    }

    private Call createCallPost(MethodRequestBody requestBody, String requestUrl) {
        return createCallPost(null, requestBody, requestUrl);
    }

    private Call createCallPost(@Nullable Headers headers, MethodRequestBody requestBody, String requestUrl) {
        RequestBody body = createJsonRequestBody(requestBody);
        return createCallPost(headers, body, requestUrl);
    }

    private RequestBody createJsonRequestBody(MethodRequestBody requestBody) {
        MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(JSON_MEDIA_TYPE, mGson.toJson(requestBody));
    }

    private Call createCallPost(RequestBody requestBody, String requestUrl) {
        return createCallPost(null, requestBody, requestUrl);
    }

    private Call createCallPost(@Nullable Headers headers, RequestBody requestBody, String requestUrl) {
        Request.Builder builder = new Request.Builder()
                .url(requestUrl)
                .post(requestBody);
        if (headers != null) {
            builder.headers(headers);
        }
        return mOkHttpClient.getValue().newCall(builder.build());
    }

    private Call createCallPostWithoutToken(RequestBody requestBody, String requestUrl) {
        Request.Builder builder = new Request.Builder()
                .url(requestUrl)
                .post(requestBody);
        return mOkHttpClientWithoutTokenId.getValue().newCall(builder.build());
    }

    private OkHttpClient createClient(@NonNull CookieManager cookieManager, Boolean withTokenId) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(cookieManager)
                .followRedirects(true)
                .connectTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false);
        mCertificateResolver.inject(builder);
        if (DEBUG_REQUEST) {
            builder.addInterceptor(new LoggingInterceptor());
        }
        if (withTokenId) {
            builder.addInterceptor(chain -> chain.proceed(
                    buildRequestWithTokenId(chain, cookieManager)
            ));
        }
        builder.addNetworkInterceptor(new ApiNetworkInterceptor());
        return builder.build();
    }

    private Request buildRequestWithTokenId(@NonNull Interceptor.Chain chain, @NonNull CookieManager cookieManager) {
        Request.Builder requestBuilder = chain.request().newBuilder();
        String tokenId = cookieManager.getTokenId();
        if (tokenId != null) {
            requestBuilder.addHeader("X-SBISAccessToken", tokenId);
        }
        return requestBuilder.build();
    }

    private Gson createGson() {
        return new GsonBuilder().serializeNulls().registerTypeAdapter(BaseModelList.class, new BaseModelListDeserializer()).create();
    }

    @NonNull
    private Bundle serverErrorBundle(int code) {
        return serverErrorBundle(code, null);
    }

    @NonNull
    private Bundle serverErrorBundle(int code, @Nullable RequestResult.Error errorData) {
        Bundle result = new Bundle();
        result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, SERVER_CONNECT_ERROR);
        result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE_DETAILS, SERVER_CONNECT_ERROR);
        result.putInt(WorkerService.EXTRA_ERROR_CODE, code);
        if (errorData != null) {
            errorData.code = code;
            errorData.errorBodyMessage = errorData.errorBodyMessageDetails = SERVER_CONNECT_ERROR;
        }
        return result;
    }

    @NonNull
    private Bundle networkErrorBundle(int code) {
        return networkErrorBundle(code, null);
    }

    @NonNull
    private Bundle networkErrorBundle(int code, @Nullable RequestResult.Error errorData) {
        Bundle result = new Bundle();
        result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, NETWORK_ERROR);
        result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE_DETAILS, NETWORK_ERROR);
        result.putInt(WorkerService.EXTRA_ERROR_CODE, code);
        if (errorData != null) {
            errorData.code = code;
            errorData.errorBodyMessage = errorData.errorBodyMessageDetails = NETWORK_ERROR;
        }
        return result;
    }

    @NonNull
    private Bundle unknownErrorBundle(int code) {
        return unknownErrorBundle(code, null);
    }

    @NonNull
    private Bundle unknownErrorBundle(int code, @Nullable RequestResult.Error errorData) {
        Bundle result = new Bundle();
        result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, UNKNOWN_ERROR);
        result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE_DETAILS, UNKNOWN_ERROR);
        result.putInt(WorkerService.EXTRA_ERROR_CODE, code);
        if (errorData != null) {
            errorData.code = code;
            errorData.errorBodyMessage = errorData.errorBodyMessageDetails = UNKNOWN_ERROR;
        }
        return result;
    }

    @Nullable
    private JsonObject getErrorBodyObject(@Nullable ResponseBody responseBody) throws IOException {
        if (responseBody == null) {
            return null;
        }
        return getErrorBodyObject(responseBody.string());
    }

    @Nullable
    private JsonObject getErrorBodyObject(@Nullable String errorString) {
        JsonObject result = null;
        if (errorString != null) {
            try {
                JsonElement jsonElement = mGson.fromJson(errorString, JsonElement.class);
                if (jsonElement != null && jsonElement.isJsonObject()) {
                    JsonElement errorElement = jsonElement.getAsJsonObject().get("error");
                    if (errorElement != null && errorElement.isJsonObject()) {
                        result = errorElement.getAsJsonObject();
                    }
                }
            } catch (JsonSyntaxException e) {
                return null;
            }
        }
        return result;
    }

    @NonNull
    private Bundle parseError(@Nullable JsonObject errorObject, @Nullable String errorMessage, int errorCode) {
        Bundle result = parseJsonError(errorObject);
        if (result == null) {
            result = new Bundle();
            result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, UNKNOWN_ERROR);
        }
        if (errorMessage != null) {
            result.putString(WorkerService.EXTRA_ERROR_MESSAGE, errorMessage);
        }
        result.putInt(WorkerService.EXTRA_ERROR_CODE, errorCode);
        return result;
    }

    @NonNull
    private Bundle parseErrorData(@Nullable JsonObject errorObject,
                                  @NonNull RequestResult.Error errorData) {
        Bundle result = parseJsonErrorWithData(errorObject, errorData);
        if (result == null) {
            result = new Bundle();
            result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, UNKNOWN_ERROR);
            errorData.errorBodyMessage = UNKNOWN_ERROR;
        }
        if (errorData.errorMessage != null) {
            result.putString(WorkerService.EXTRA_ERROR_MESSAGE, errorData.errorMessage);
        }
        result.putInt(WorkerService.EXTRA_ERROR_CODE, errorData.code);
        return result;
    }

    @Nullable //todo will be @Deprecated in future
    private Bundle parseJsonError(@Nullable JsonObject errorObject) {
        if (errorObject == null) {
            return null;
        }
        Bundle result = new Bundle();
        JsonElement jsonElement = errorObject.get("message");
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, jsonElement.getAsString());
        }
        jsonElement = errorObject.get("details");
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE_DETAILS, jsonElement.getAsString());
        }
        jsonElement = errorObject.get("data");
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject dataObject = jsonElement.getAsJsonObject();
            JsonElement addInfoElement = dataObject.get("addinfo");
            if (addInfoElement != null && addInfoElement.isJsonObject()) {
                Set<Map.Entry<String, JsonElement>> addInfoSet = addInfoElement.getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> addInfoEntry : addInfoSet) {
                    String key = addInfoEntry.getKey();
                    String value = null;
                    JsonElement valueElement = addInfoEntry.getValue();
                    if (valueElement != null && valueElement.isJsonPrimitive()) {
                        value = valueElement.getAsJsonPrimitive().getAsString();
                    }
                    result.putString(key, value);
                }
                result.putBoolean(WorkerService.EXTRA_HAS_ERROR_BODY_DATA, true);
            }
            JsonElement errorCodeElement = dataObject.get("error_code");
            if (errorCodeElement != null && errorCodeElement.isJsonPrimitive()) {
                result.putInt(WorkerService.EXTRA_ERROR_BODY_CODE, errorCodeElement.getAsJsonPrimitive().getAsInt());
            }
        }
        return result;
    }

    @Nullable
    private Bundle parseJsonErrorWithData(@Nullable JsonObject errorObject,
                                          @NonNull RequestResult.Error errorData) {
        if (errorObject == null) {
            return null;
        }
        Bundle result = new Bundle();
        JsonElement jsonElement = errorObject.get("message");
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            String errorBodyMessage = jsonElement.getAsString();
            errorData.errorBodyMessage = errorBodyMessage;
            result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE, errorBodyMessage);
        }
        jsonElement = errorObject.get("details");
        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            String errorBodyMessageDetails = jsonElement.getAsString();
            errorData.errorBodyMessageDetails = errorBodyMessageDetails;
            result.putString(WorkerService.EXTRA_ERROR_BODY_MESSAGE_DETAILS, errorBodyMessageDetails);
        }
        jsonElement = errorObject.get("data");
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject dataObject = jsonElement.getAsJsonObject();
            JsonElement addInfoElement = dataObject.get("addinfo");
            if (addInfoElement != null && addInfoElement.isJsonObject()) {
                Set<Map.Entry<String, JsonElement>> addInfoSet = addInfoElement.getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> addInfoEntry : addInfoSet) {
                    String key = addInfoEntry.getKey();
                    String value = null;
                    JsonElement valueElement = addInfoEntry.getValue();
                    if (valueElement != null && valueElement.isJsonPrimitive()) {
                        value = valueElement.getAsJsonPrimitive().getAsString();
                    }
                    result.putString(key, value);
                }
                result.putBoolean(WorkerService.EXTRA_HAS_ERROR_BODY_DATA, true);
            }
            JsonElement errorCodeElement = dataObject.get("error_code");
            if (errorCodeElement != null && errorCodeElement.isJsonPrimitive()) {
                int bodyCode = errorCodeElement.getAsJsonPrimitive().getAsInt();
                errorData.bodyCode = bodyCode;
                result.putInt(WorkerService.EXTRA_ERROR_BODY_CODE, bodyCode);
            }
        }
        return result;
    }

    @WorkerThread
    @NonNull
    private <T> RequestResult<T> request(@NonNull Call call, @NonNull Class<T> clazz) {
        RequestResult<T> result = new RequestResult<>();
        ResponseBody responseBody = null;
        int code = 0;
        try {
            Response response = call.execute();
            responseBody = response.body();
            code = response.code();
            if (response.isSuccessful() || response.message().equals("Validation required")) {
                String responseBodyString = responseBody != null ? responseBody.string() : null;
                JsonObject jsonObject = getErrorBodyObject(responseBodyString);
                if (jsonObject == null) {
                    result.result = mGson.fromJson(responseBodyString, clazz);
                } else {
                    result.errorData = new RequestResult.Error(code, response.message());
                    result.error = parseErrorData(jsonObject, result.errorData);
                }
            } else {
                result.errorData = new RequestResult.Error(code, response.message());
                result.error = parseErrorData(getErrorBodyObject(responseBody), result.errorData);
            }
        } catch (UnknownHostException | InterruptedIOException | StreamResetException e) {
            result.errorData = new RequestResult.Error(
                    code, new APIException.UnrecoverableNetworkException(e)
            );
            result.error = networkErrorBundle(code, result.errorData);
            Timber.d(e);
        } catch (IOException e) {
            result.errorData = new RequestResult.Error(
                    code, new APIException.RecoverableNetworkException(e)
            );
            result.error = serverErrorBundle(code, result.errorData);
            Timber.d(e);
        } catch (Throwable e) {
            result.errorData = new RequestResult.Error(
                    code, new APIException.UnknownException(e)
            );
            result.error = unknownErrorBundle(code, result.errorData);
            Timber.e(e);
        } finally {
            closeBody(responseBody);
        }
        return result;
    }

    @WorkerThread
    @NonNull
    private RequestResult<ResponseBody> requestRaw(@NonNull Call call) {
        RequestResult<ResponseBody> result = new RequestResult<>();
        ResponseBody responseBody = null;
        int code = 0;
        try {
            Response response = call.execute();
            responseBody = response.body();
            code = response.code();
            if (response.isSuccessful() || response.message().equals("Validation required")) {
                if (responseBody != null) {
                    result.result = responseBody;
                } else {
                    result.errorData = new RequestResult.Error(code, response.message());
                    result.error = parseError(null, response.message(), code);
                }
            } else {
                result.error = parseError(getErrorBodyObject(responseBody), response.message(), code);
            }
        } catch (UnknownHostException | InterruptedIOException | StreamResetException e) {
            result.error = networkErrorBundle(code);
            Timber.d(e);
        } catch (IOException e) {
            result.error = serverErrorBundle(code);
            Timber.d(e);
        } catch (Throwable e) {
            result.error = unknownErrorBundle(code);
            Timber.e(e);
        } finally {
            if (result.result == null) {
                closeBody(responseBody);
            }
        }
        return result;
    }

    private void checkCallback(@SuppressWarnings("rawtypes") @Nullable ResultListener callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback can't be null! Use sync command instead");
        }
    }

    private void closeBody(@Nullable ResponseBody responseBody) {
        if (responseBody != null) {
            responseBody.close();
        }
    }

    private static final class LoggingInterceptor implements Interceptor {

        @SuppressLint("StringFormatInTimber")
        @Override
        public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            final Buffer buffer = new Buffer();
            RequestBody body = request.body();
            if (body != null) {
                body.writeTo(buffer);
            }
            Timber.d(String.format("Sending request %s, %s", request.url(), buffer.readUtf8()));

            okhttp3.Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Buffer responseBuffer = new Buffer();
            if (body != null) {
                body.writeTo(responseBuffer);
            }
            Timber.d(
                    String.format(
                            Locale.getDefault(), "Received response for %s in %.1fms%n%s%n%s",
                            response.request().url(), (t2 - t1) / 1e6d, response.headers(), responseBuffer.readUtf8()
                    )
            );

            return response;
        }

    }

}