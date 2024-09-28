package ru.tensor.sbis.network_native.httpclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kotlin.Lazy;
import kotlin.LazyKt;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.tensor.sbis.network_native.BuildConfig;
import ru.tensor.sbis.network_native.ServerUtil;
import ru.tensor.sbis.network_native.apiservice.api.HeadersContract;
import ru.tensor.sbis.network_native.apiservice.api.interceptor.ApiNetworkInterceptor;
import ru.tensor.sbis.network_native.apiservice.contract.ApiService;
import ru.tensor.sbis.network_native.error.SbisError;
import timber.log.Timber;

/**
 * Класс, содержащий набор функций по работе с сетью. Синглтон.
 */
@SuppressWarnings("unused")
public class Server {

    /**
     * Получение имени хоста для ссылки на внутренний ресурс
     *
     * @param url ссылка
     * @return имя хоста или null, если ссылка на внешний ресурс
     */
    @Nullable
    public static String getSbisHostUrl(@NonNull String url) {
        return ServerUtil.getSbisHostUrl(url);
    }

    private static final String SERVICE_URL = "service/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String HEADER_SBIS_ACCESS_TOKEN = "X-SBISAccessToken";

    private static Server instance;
    //вынесено из core/src/main/java/ru/tensor/sbis/core/util/DeviceUtils.java, чтобы развязать core, common и network
    private static String sUserAgent = "";

    @NonNull
    private Host serverHost = Host.TEST;
    private URI serverURI;

    @NonNull
    private ApiService mApiService;
    @NonNull
    private CookieManager mCookieManager;

    private final Lazy<OkHttpClient> httpClient = LazyKt.lazy(() ->
            mApiService.getOkHttpClientBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .followRedirects(true)
                    .cookieJar(mCookieManager)
                    .addNetworkInterceptor(new ApiNetworkInterceptor())
                    .build()
    );

    private final Lazy<OkHttpClient> httpFileLoaderClient = LazyKt.lazy(() ->
            mApiService.getOkHttpClientBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(200, TimeUnit.SECONDS)
                    .writeTimeout(200, TimeUnit.SECONDS)
                    .cookieJar(mCookieManager)
                    .followRedirects(true)
                    .build()
    );


    /**
     * Тип сервера, к которому подключается приложение.
     * Изменяется при помощи функции Server.getInstance().setHost().
     */
    @SuppressWarnings("unused")
    public enum Host {

        PROD(4, BuildConfig.PROD_SERVER_URL, BuildConfig.PROD_MIRROR_URL, true),
        RC(10, BuildConfig.RC_SERVER_URL, BuildConfig.RC_MIRROR_URL, true),
        FIX(3, BuildConfig.FIX_SERVER_URL, BuildConfig.FIX_MIRROR_URL, true),
        TEST(2, BuildConfig.TEST_SERVER_URL, BuildConfig.TEST_MIRROR_URL, true),
        PRETEST(1, BuildConfig.PRETEST_SERVER_URL, BuildConfig.PRETEST_MIRROR_URL, true),
        DEV(0, BuildConfig.DEV_SERVER_URL, BuildConfig.DEV_MIRROR_URL, true),
        CUSTOM(11, BuildConfig.CUSTOM_SERVER_URL, BuildConfig.CUSTOM_MIRROR_URL, false); // Конкретный адрес вводится в Runtime'e.
        // CUSTOM hostUrl не может иметь пустое дефолтное значение
        // https://online.sbis.ru/opendoc.html?guid=3e106ce7-141e-4c2f-8a4c-555de9bd6cd3

        private final int value;
        private String hostUrl;   // текущий url портала (main or mirror)
        private String main;      // основной url портала
        private String mirror;    // зеркало url портала
        @Nullable
        private final String demo;
        private final String socnetUrl; // url соцсети СБИС
        private static List<String> visibleHosts;
        private Boolean isSecure;

        Host(int value, String url, String mirror, Boolean isSecure) {
            this.value = value;
            this.hostUrl = url; // По умолчанию
            this.main = url;
            this.mirror = mirror;
            this.socnetUrl = hostUrl.replace("online", "n");
            this.demo = calcDemo(mirror);
            this.isSecure = isSecure;
        }

        @NonNull
        public String getHostUrl() {
            return hostUrl;
        }

        public void setHostUrl(@NonNull String hostUrl) {
            this.hostUrl = hostUrl;
        }

        @NonNull
        public String getMirror() {
            return mirror;
        }

        public void changeHostAndMirrorUrl(@NonNull String hostUrl, @NonNull String mirrorUrl) {
            this.hostUrl = hostUrl; // По умолчанию
            this.main = hostUrl;
            this.mirror = mirrorUrl;
        }

        @NonNull
        public String[] getSupportedSbisUrls() {
            if (demo == null) {
                return new String[]{main, mirror, socnetUrl};
            } else {
                return new String[]{main, mirror, socnetUrl, demo};
            }
        }

        public int getValue() {
            return value;
        }

        public boolean isMainHost() {
            return hostUrl.equals(main);
        }

        @NonNull
        public String getFullHostUrl() {
            String protocolName = "";
            if (isSecure) {
                protocolName = HttpProtocol.HTTPS.getProtocolName();
            } else  {
                protocolName = HttpProtocol.HTTP.getProtocolName();
            }
            return MessageFormat.format("{0}://{1}", protocolName, hostUrl);
        }

        public static List<String> hosts() {
            if (visibleHosts == null) {
                Host[] hosts = values();
                visibleHosts = new ArrayList<>();
                for (Host host : hosts) {
                    visibleHosts.add(host.name());
                }
            }
            return visibleHosts;
        }

        public static Host fromName(String name) {
            for (Host s : Host.values()) {
                if (s.name().equalsIgnoreCase(name)) {
                    return s;
                }
            }
            return null;// not found
        }

        public static void switchToMain() {
            for (Host host : values()) {
                if (host != CUSTOM) {
                    host.hostUrl = host.main;
                }
            }
        }

        public static void switchToMirror() {
            for (Host host : values()) {
                if (host != CUSTOM) {
                    host.hostUrl = host.mirror;
                }
            }
        }

        @Nullable
        public static Host fromValue(int value) {
            for (Host s : Host.values()) {
                if (s.value == value) {
                    return s;
                }
            }
            return null;
        }

        @Nullable
        private String calcDemo(String url) {
            StringBuilder sb = new StringBuilder(url);
            int lastDotPos = url.lastIndexOf(".");
            if (lastDotPos != -1) {
                sb.insert(lastDotPos, "d");
                return sb.toString();
            } else {
                return null;
            }
        }
    }

    /**
     * Получить экземпляр сервера.
     */
    @NonNull
    public static Server getInstance() {
        return instance;
    }

    public static void init(@NonNull CookieManager cookieManager, @NonNull ApiService apiService) {
        if (instance == null) {
            instance = new Server(cookieManager, apiService);
        }
    }

    @NonNull
    public static String getUserAgent() {
        if (TextUtils.isEmpty(sUserAgent)) {
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException("UserAgent не установлен");
            } else {
                Timber.e("Попытка использовать недействительный или пустой UserAgent: %s", sUserAgent);
            }
        }
        return sUserAgent;
    }

    public static void setUserAgent(@NotNull String userAgent) {
        sUserAgent = userAgent;
    }

    /**
     * Сохранить значение "кастомного" хоста в SharedPreferences.
     *
     * @param context контекст
     * @param host    хост
     */
    public static void saveCustomHost(@NonNull Context context, @NonNull Host host) {
        getPrefFile(context).edit().putString(Keys.SAVED_CUSTOM_HOST, host.getHostUrl()).apply();
    }

    /**
     * Получить значение "кастомного" хоста сохраненного в SharedPreferences.
     *
     * @param context контекст
     */
    public static String getSavedCustomHost(@NonNull Context context) {
        return getPrefFile(context).getString(Keys.SAVED_CUSTOM_HOST, Host.CUSTOM.getHostUrl());
    }

    /**
     * Сохранить значение хоста в SharedPreferences.
     *
     * @param context контекст
     * @param host    хост
     */
    public static void saveServerHost(@NonNull Context context, @NonNull Host host) {
        getPrefFile(context).edit()
                .putInt(Keys.SAVED_HOST, host.getValue())
                .putBoolean(Keys.IS_MIRROR_HOST, !host.isMainHost())
                .apply();
    }

    /**
     * Получить значение хоста сохраненного в SharedPreferences.
     * Данный метод должен вызываться только после [init] метода [Server]
     * Иначе для CUSTOM хоста будет NPE
     *
     * @param context контекст
     */
    @Nullable
    public static Host getSavedServerHost(@NonNull Context context) {
        SharedPreferences prefFile = getPrefFile(context);
        if (!prefFile.contains(Keys.SAVED_HOST)) {
            return null;
        }

        final int value = prefFile.getInt(Keys.SAVED_HOST, 0);
        if (value == Host.CUSTOM.getValue()) {
            return Server.updateCustomHostUrl(getSavedCustomHost(context));
        }

        if (prefFile.getBoolean(Keys.IS_MIRROR_HOST, false)) Host.switchToMirror();
        return Host.fromValue(value);
    }

    /** Проверить является ли сохраненный хост == PROD */
    public static Boolean isSavedHostProd(@NonNull Context context){
        SharedPreferences prefs = getPrefFile(context);
        if (!prefs.contains(Keys.SAVED_HOST)) {
            return false;
        }
        final int value = prefs.getInt(Keys.SAVED_HOST, 0);

        return Host.fromValue(value) == Host.PROD;
    }

    private static SharedPreferences getPrefFile(@NonNull Context context) {
        //Перенесено из AppConfig, необходима миграция для SharedPreferences при смене FileName
        String sPreferencesFileName = "SbisMobile";
        return context.getSharedPreferences(sPreferencesFileName, Context.MODE_PRIVATE);
    }


    private Server(@NonNull CookieManager cookieManager, @NonNull ApiService apiService) {
        mApiService = apiService;
        mCookieManager = cookieManager;
    }

    // Т.к. модуль "Login" сильно завязан на использовании
    // класса-перечисления "Host", было принято решение
    // добавить метод, который позволяет изменять значение
    // внутри перечисления - "CUSTOM". Решение обосновано тем,
    // что добавление возможности выполнять подключение к
    // "кастомному" серверу, потребовало бы большое количество
    // изменений, которые в конечном итоге могли бы повлиять на
    // работу приложений уже находящихся в продакшене. Также в
    // ближайшем будущем планируется переработка модуля "Login",
    // поэтому считаю эту временную меру оправданной.
    @SuppressWarnings("SameReturnValue")
    public static Host updateCustomHostUrl(String hostUrl) {
        Host.CUSTOM.setHostUrl(hostUrl);
        return Host.CUSTOM;
    }

    /**
     * Выставить хост, к которому будем отправлять запросы.
     *
     * @param host хост
     */
    public void setHost(@NonNull Host host) {
        serverHost = host;
        try {
            serverURI = new URI("http://" + host.getHostUrl());
        } catch (URISyntaxException e) {
            Timber.e(e);
        }
    }

    /**
     * Выставить хост, к которому будем отправлять запросы (https-протокол).
     *
     * @param host хост
     */
    public void setHttpsHost(@NonNull Host host) {
        serverHost = host;
        try {
            serverURI = new URI("https://" + host.getHostUrl());
        } catch (URISyntaxException e) {
            Timber.e(e);
        }
    }

    /**
     * Получить хост, на который сейчас настроен сервер.
     *
     * @return хост
     */
    @NonNull
    public Host getHost() {
        return serverHost;
    }

    @Nullable
    public URI getServerURI() {
        return serverURI;
    }

    private String generateServerUrl(@NonNull final HttpProtocol protocol, final String serverUrl, final String serviceName) {
        return String.format("%s://%s/%s/%s", protocol.getProtocolName(), serverUrl, serviceName, SERVICE_URL);
    }

    /**
     * Выполнить POST запрос к серверу.
     *
     * @param body     тело запроса - json
     * @param url      - полный адрес: сервер(хост) + имя сервиса
     * @param callback - результат выполнения запроса
     */
    @NonNull
    public Call executePost(@NonNull String body, String url, final HttpResponseCallback callback) {
        final RequestBody requestBody = RequestBody.create(JSON, body);
        final Call call = createCallPost(requestBody, url);
        call.enqueue(new CustomCallbackHandler(callback));
        return call;
    }

    /**
     * Выполнить POST запрос к серверу.
     *
     * @param body     тело запроса - json
     * @param params   - параметры сервера
     * @param callback - результат выполнения запроса
     */
    @NonNull
    public Call executePostRequest(@NonNull final String body, @NonNull final IHttpRequestParams params, final HttpResponseCallback callback) {
        final String url = generateServerUrl(params.getProtocol(), params.getHost(), params.getService());
        final RequestBody requestBody = RequestBody.create(JSON, body);
        final Call call = createCallPost(requestBody, url);
        call.enqueue(new CustomCallbackHandler(callback));
        return call;
    }

    /**
     * Выполнить POST запрос к серверу.
     *
     * @param body     тело запроса - json
     * @param service  - название сервиса
     * @param callback - результат выполнения запроса
     */
    @NonNull
    public Call executePostRequest(@NonNull final String body, final String service, final HttpResponseCallback callback) {
        return executePostRequest(body, new HttpRequestParams(getHost().getHostUrl(), service), callback);
    }

    /**
     * Выполнить POST запрос к серверу.
     *
     * @param body     тело запроса - json
     * @param callback - результат выполнения запроса
     */
    @NonNull
    public Call executePostRequest(@NonNull final String body, final HttpResponseCallback callback) {
        return executePostRequest(body, new HttpRequestParams(getHost().getHostUrl(), ""), callback);
    }

    @Nullable
    public Map<String, String> buildRequestCookieHeaders() {
        return buildCookieHeaders(HeadersContract.COOKIE.getOverrideName());
    }

    @Nullable
    public Map<String, String> buildCookieHeaders() {
        return buildCookieHeaders(HeadersContract.COOKIE.name());
    }

    /**
     * Выполнить GET запрос к серверу для скачивания файла.
     *
     * @param url               URL запроса
     * @param file              файл в который сохранить результат
     * @param fileDownloadEvent обработчик событий процесса загрузки
     * @return задача на загрузку файла
     */
    @NonNull
    public FileLoadTask downloadFile(String url, @NonNull File file, @NonNull FileDownloadEvent fileDownloadEvent) {
        Request.Builder builder = new Request.Builder().url(url);
        if (mCookieManager.getTokenId() != null) {
            builder.addHeader(HEADER_SBIS_ACCESS_TOKEN, mCookieManager.getTokenId());
        }
        Request request = builder.build();
        final Call call = httpFileLoaderClient.getValue().newCall(request);

        final FileLoadTask fileLoadTask = new FileLoadTask(call, file);

        OutputStream outputStream = null;
        InputStream inputStream = null;
        boolean saved = false;
        SbisError sbisError = null;
        try {
            final Response response = call.execute();
            if (response.isSuccessful()) {

                long contentSize = 0;

                final String contentRangeHeader = response.headers().get("Content-Range");
                final ResponseBody responseBody = response.body();

                if (contentRangeHeader == null) {
                    if (responseBody != null) contentSize = responseBody.contentLength();
                } else {
                    int rangeIx = contentRangeHeader.lastIndexOf('/');
                    if (rangeIx > 0) {
                        final String range = contentRangeHeader.substring(rangeIx + 1);
                        contentSize = Integer.parseInt(range);
                    }
                }

                if (responseBody != null) inputStream = responseBody.byteStream();
                outputStream = new FileOutputStream(file);

                if (inputStream != null) {
                    int read;
                    final byte[] bytes = new byte[4 * 1024]; // 4 kb buffer
                    long totalRead = 0L;

                    // пока не считали весь либо задачу не отменили
                    while ((read = inputStream.read(bytes)) != -1 && !fileLoadTask.isCanceled()) {
                        outputStream.write(bytes, 0, read);
                        if (contentSize > 0) {
                            totalRead += read;
                            int percent = (int) ((totalRead * 100L) / contentSize);
                            fileDownloadEvent.onProgress(percent);
                        }
                    }

                    outputStream.flush();
                    saved = true;
                }

                fileLoadTask.finish();
            } else {
                sbisError = new SbisError("Сервер вернул код ошибки", "Возникли проблемы с загрузкой файла " + file.getName(), response.code());
            }
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }

        if (saved) {
            fileDownloadEvent.onSuccess();
        } else {
            if (sbisError == null) {
                sbisError = new SbisError("Не удалось сохранить файл", "Возникли проблемы с сохранением файла " + file.getName(), -1);
            }
            fileDownloadEvent.onFail(sbisError);
        }

        return fileLoadTask;
    }

    @Nullable
    private Map<String, String> buildCookieHeaders(@NonNull String cookieKey) {
        final String formattedCookie = mCookieManager.getFormattedCookie();
        if (formattedCookie != null) {
            Map<String, String> cookieHeaders = new HashMap<>(1);
            cookieHeaders.put(cookieKey, formattedCookie);
            return cookieHeaders;
        }
        return null;
    }

    private Call createCallPost(RequestBody requestBody, String requestUrl) {
        return createCallPost(null, requestBody, requestUrl);
    }

    @SuppressWarnings("SameParameterValue")
    private Call createCallPost(@Nullable Headers headers, RequestBody requestBody, String requestUrl) {
        Request.Builder builder = new Request.Builder()
                .url(requestUrl)
                .post(requestBody);
        if (headers != null) {
            builder.headers(headers);
        }
        return httpClient.getValue().newCall(builder.build());
    }
}
