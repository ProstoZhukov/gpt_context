package ru.tensor.sbis.webviewer;

import static ru.tensor.sbis.webviewer.utils.WebChromeClientUtilsKt.createCustomWebChromeClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.tensor.sbis.common.util.AppConfig;
import ru.tensor.sbis.common.util.DeviceUtils;
import ru.tensor.sbis.common.util.FileUtil;
import ru.tensor.sbis.network_native.apiservice.api.certificate.CertificateSafeWebClient;
import ru.tensor.sbis.network_native.apiservice.contract.ApiService;
import ru.tensor.sbis.network_native.httpclient.Server;
import ru.tensor.sbis.webviewer.utils.TitleLoadedListener;
import ru.tensor.sbis.webviewer.utils.WebViewRenderProcessGoneHandler;
import timber.log.Timber;

/**
 * Кастомная WebView для открытия документов
 *
 * @author ma.kolpakov
 */
public class DocumentWebView extends AdvancedWebView {

    private static final String IS_WEB_VIEW_COOKIE = "isWebView=true";
    private static final String IS_MULTITOUCH_COOKIE = "is_multitouch=true";
    // Указаны реальные размеры экрана, а не размер окна браузера
    private static final String S3DS_COOKIE = "s3ds=width|height|width|height|width|height";
    private static final String TZ_COOKIE = "tz=0";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String HEADER_SBIS_ACCESS_TOKEN = "X-SBISAccessToken";
    private static final String HEADER_SET_COOKIE = "Set-Cookie";
    private static final String SID_COOKIE_PREFIX = "sid=";

    private static final ApiService apiService = WebViewerPlugin.INSTANCE
            .getWebViewerComponent()
            .getDependency()
            .apiService();

    private String mDocumentUrl;
    @Nullable
    private String mDocumentId;
    private String mToken;
    @Nullable
    private LoadFileByLinkInterface mFileLoadingListener;
    @Nullable
    private TitleLoadedListener mTitleLoadedListener;
    private Disposable mDisposable;
    @Nullable
    private DocumentWebViewClient webViewClient;
    private boolean allowReplaceHttpToHttps = true;

    public DocumentWebView(Context context) {
        super(context);
    }

    public DocumentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DocumentWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @CallSuper
    @Override
    protected void init(Context context) {
        super.init(context);
        setWebViewClient(createWebViewClient());
        setWebChromeClient(createCustomWebChromeClient(this::onTitleReceived));
        WebSettings settings = getSettings();
        settings.setUserAgentString(null);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        setWebContentsDebuggingEnabled(AppConfig.isDebug());
        setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            if (mFileLoadingListener == null || mFileLoadingListener.hasActiveAction()) {
                return;
            }
            if (isBlobUrl(url)) {
                mFileLoadingListener.onUnableToLoadFile();
                return;
            }
            String fileName = FileUtil.parseFileName(url, contentDisposition, mimetype);
            String folderPath = getFileLoadingFolderPath();
            if (new File(FileUtil.getFilePath(folderPath, fileName)).exists()) {
                mFileLoadingListener.onFileAlreadyExists(fileName, folderPath, url);
            } else {
                mFileLoadingListener.onFileLoading(fileName, folderPath, url);
            }
        });
    }

    private String formS3dsCookie(Context context) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float factor = (float) (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        int width = (int) (DeviceUtils.getScreenWidthInPx(context) / factor);
        int height = (int) (DeviceUtils.getScreenHeightInPx(context) / factor);
        return S3DS_COOKIE
                .replaceAll("width", String.valueOf(width))
                .replaceAll("height", String.valueOf(height));
    }

    /**
     * Возвращает кастомный WebViewClient. При переопределении, также необходимо переопределить
     * {@link #setRendererDeathListener}
     */
    @NonNull
    protected WebViewClient createWebViewClient() {
        webViewClient = new DocumentWebViewClient(getContext());
        return webViewClient;
    }

    /**
     * Устанавливает возможность загрузки каждой новой страницы в отдельном окне.
     * Следует вызывать сразу после инициализации WebView.
     *
     * @param support если передано true - новая страница будет загружаться в отдельном окне,
     *                если передано false - новая страница будет загружаться в текущем окне, при возврате
     *                на предыдущую страницу произойдет ее перезагрузка.
     */
    public void setSupportMultipleWindows(boolean support) {
        if (support) {
            createCustomWebChromeClient(this::onTitleReceived, message -> {
                final DocumentWebView newPage = addPage();
                WebViewTransport transport = (WebViewTransport) message.obj;
                transport.setWebView(newPage);
                message.sendToTarget();
                return true;
            });
        } else {
            createCustomWebChromeClient(this::onTitleReceived);
        }
        getSettings().setSupportMultipleWindows(support);
    }

    /**
     * Возвращает возможность поддержки нескольких окон при загрузке страниц
     */
    public boolean isSupportMultipleWindows() {
        return getSettings().supportMultipleWindows();
    }

    /**
     * URL документа, которую загружаем.
     */
    @Nullable
    public String getDocumentUrl() {
        return mDocumentUrl;
    }

    /**
     * Устанавливаем и загружаем документ по URL
     */
    public void setDocumentUrl(@NonNull String documentUrl) {
        mDocumentUrl = documentUrl;
        if (isCookiePrefetchNeeded(documentUrl)) {
            fetchRequiredCookiesAndThenLoadPage(documentUrl);
        } else {
            loadDocument(documentUrl);
        }
    }

    /**
     * Идентификатор сессии пользователя. Нужен для авторизации.
     */
    public void setToken(@NonNull String token) {
        mToken = token;
        setAuthorizationHeader();
    }

    /**
     * Установить cookies для корректного отображения документа
     */
    public void setUiCookies(Context context) {
        setCookie(IS_WEB_VIEW_COOKIE);
        setCookie(IS_MULTITOUCH_COOKIE);
        setCookie(formS3dsCookie(context));
        setCookie(TZ_COOKIE);
    }

    /**
     * Идентификатор устройства
     */
    @SuppressWarnings("unused")
    public void setDeviceId(@NonNull String deviceId) {
        removeHttpHeader("X-SBISDEVICEID");
        addHttpHeader("X-SBISDEVICEID", deviceId);
    }

    public void setDocumentId(@Nullable String documentId) {
        mDocumentId = documentId;
    }

    /**
     * Выставляет http-заголовок с токеном авторизации.
     */
    private void setAuthorizationHeader() {
        removeHttpHeader(HEADER_SBIS_ACCESS_TOKEN);
        if (mToken != null) addHttpHeader(HEADER_SBIS_ACCESS_TOKEN, mToken);
    }

    /**
     * Выставляет cookie для браузера.
     */
    private void setCookie(@NonNull String cookie) {
        CookieManager cookieManager = CookieManager.getInstance();
        final String[] urls = Server.getInstance().getHost().getSupportedSbisUrls();
        for (String url : urls) {
            if (containsCookie(cookieManager, url, cookie)) {
                break;
            }
            cookieManager.setCookie(HTTP + url, cookie);
            cookieManager.setCookie(HTTPS + url, cookie);
            cookieManager.setCookie(url, cookie);
        }
        cookieManager.flush();
    }

    private boolean containsCookie(CookieManager cookieManager, String url, String cookie) {
        String urlCookie = cookieManager.getCookie(url);
        if (urlCookie == null) {
            return false;
        }
        String cookieName = cookie.substring(0, cookie.indexOf("="));
        return urlCookie.contains(cookieName);
    }

    private void setCookies(@NonNull String url, @NonNull List<String> cookies) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, IS_WEB_VIEW_COOKIE);
        for (String cookie : cookies) {
            cookieManager.setCookie(url, cookie);
        }
        cookieManager.flush();
    }

    /**
     * Установка слушателя загрузки файла
     *
     * @param fileLoadingListener слушатель загрузки файлов по ссылке
     */
    public void setFileLoadingListener(@Nullable LoadFileByLinkInterface fileLoadingListener) {
        mFileLoadingListener = fileLoadingListener;
        final DocumentWebView nextPage = getNextPage();
        if (nextPage != null) {
            nextPage.setFileLoadingListener(fileLoadingListener);
        }
    }

    /**
     * Задаёт путь папки для скачивания файла.
     */
    protected String getFileLoadingFolderPath() {
        return FileUtil.getCachedFolderPath(getContext(), String.valueOf(mDocumentId));
    }

    /**
     * Задаёт обработчик события загрузки заголовка
     */
    public void setTitleLoadedListener(@Nullable TitleLoadedListener titleLoadedListener) {
        mTitleLoadedListener = titleLoadedListener;
        final DocumentWebView nextPage = getNextPage();
        if (nextPage != null) {
            nextPage.setTitleLoadedListener(titleLoadedListener);
        }
    }

    /**
     * Включить/отключить замену HTTP на HTTPS для ссылок, начиная с Android 9.0.
     * По умолчанию включено.
     *
     * @see #loadUrl(String)
     */
    public void setShouldAllowReplaceHttpToHttps(boolean isAllowed) {
        allowReplaceHttpToHttps = isAllowed;
    }

    /**
     * SelfDocumented
     */
    protected void setRendererDeathListener(@Nullable WebViewRendererDeathListener rendererDeathListener) {
        if (webViewClient != null) {
            webViewClient.setRendererDeathListener(rendererDeathListener);
        }
    }

    @Override
    public void loadUrl(@Nullable String url) {
        if (url != null && allowReplaceHttpToHttps
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && url.startsWith(HTTP)) {
            /*
            Начиная с Android 9 (API 28), при загрузке http страницы возникает ошибка
            NET::ERR_CLEARTEXT_NOT_PERMITTED, страницы не грузятся в целях безопасности.
            Для обхода заменяем http на https. Если https для сайта не поддерживается, должен
            сработать автоматический редирект в браузер.
            */
            super.loadUrl(url.replace(HTTP, HTTPS));
        } else {
            super.loadUrl(url);
        }
    }

    @Override
    public void reload() {
        final DocumentWebView nextPage = getNextPage();
        if (nextPage != null) {
            nextPage.reload();
        } else {
            super.reload();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final DocumentWebView nextPage = getNextPage();
        if (nextPage != null) {
            nextPage.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final DocumentWebView nextPage = getNextPage();
        if (nextPage != null) {
            nextPage.onPause();
        }
    }

    @Override
    public boolean canGoBack() {
        return getNextPage() != null || super.canGoBack();
    }

    @Override
    public void goBack() {
        final DocumentWebView nextPage = getNextPage();
        if (nextPage != null) {
            nextPage.goBack();
        } else {
            if (canGoBack()) {
                super.goBack();
            } else if (getParent() instanceof DocumentWebView) {
                removePage();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    @NonNull
    protected DocumentWebView createNewPage() {
        return new DocumentWebView(getContext());
    }

    @NonNull
    private DocumentWebView addPage() {
        DocumentWebView webView = createNewPage();
        webView.setLayoutParams(getLayoutParams());
        webView.setSupportMultipleWindows(true);
        webView.mDocumentId = mDocumentId;
        webView.mDocumentUrl = mDocumentUrl;
        webView.mFileLoadingListener = mFileLoadingListener;
        webView.mListener = mListener;
        addView(webView);
        final Listener listener = getListener();
        if (listener != null) {
            listener.onPageAdded();
        }
        return webView;
    }

    private void removePage() {
        final Listener listener = getListener();
        if (listener != null) {
            listener.onPageRemoved();
        }
        mFileLoadingListener = null;
        mListener = null;
        onDestroy();
    }

    private void removeAllPages() {
        if (getChildCount() > 0 && isSupportMultipleWindows()) {
            removeAllViews();
        }
    }

    @Nullable
    private DocumentWebView getNextPage() {
        if (getChildCount() == 1 && isSupportMultipleWindows()) {
            final View child = getChildAt(0);
            if (child instanceof DocumentWebView) {
                return (DocumentWebView) child;
            }
        }
        return null;
    }

    @Nullable
    private Listener getListener() {
        if (mListener instanceof Listener) {
            return (Listener) mListener;
        }
        return null;
    }

    private boolean isBlobUrl(@NonNull String url) {
        return url.startsWith("blob");
    }

    private void loadDocument(@NonNull String documentUrl) {
        if (isHostOfUrl(documentUrl)) {
            setAuthorizationHeader();
        } else {
            removeHttpHeader(HEADER_SBIS_ACCESS_TOKEN);
        }
        loadUrl(documentUrl);
        removeAllPages();
    }

    /**
     * Принадлежит ли url, а соответственно и токен авторизации текущему хосту.
     */
    private boolean isHostOfUrl(@NonNull String url) {
        String hostUrl = Server.getInstance().getHost().getHostUrl();
        int dashIndex = hostUrl.indexOf("-");
        if (dashIndex < 0) {
            return true;
        }
        String hostPrefix = hostUrl.substring(0, dashIndex);
        return Uri.parse(url).getHost().startsWith(hostPrefix);
    }

    private boolean isCookiePrefetchNeeded(@NonNull String documentUrl) {
        if (mToken == null || !isHostOfUrl(documentUrl)) {
            return false;
        }

        String cookie = CookieManager.getInstance().getCookie(documentUrl);
        return cookie == null || !cookie.contains(SID_COOKIE_PREFIX);
    }

    private void fetchRequiredCookiesAndThenLoadPage(@NonNull String documentUrl) {
        OkHttpClient client = getCookiesInterceptHttpClient(documentUrl);
        Request request = createCookiesFetchRequest();
        mDisposable = Completable.fromCallable(() -> client.newCall(request).execute())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> loadDocument(documentUrl),
                        e -> {
                            if (webViewClient != null) {
                                webViewClient.onReceivedError(this, WebViewClient.ERROR_CONNECT, "", documentUrl);
                            }
                        }
                );
    }

    @NonNull
    private OkHttpClient getCookiesInterceptHttpClient(@NonNull String documentUrl) {
        return apiService.getOkHttpClientBuilder()
                .addInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    List<String> headers = originalResponse.headers(HEADER_SET_COOKIE);
                    if (!headers.isEmpty()) {
                        setCookies(documentUrl, headers);
                    }
                    return originalResponse;
                })
                .build();
    }

    @NonNull
    private Request createCookiesFetchRequest() {
        return new Request.Builder()
                .url(Server.getInstance().getHost().getFullHostUrl())
                .addHeader(HEADER_SBIS_ACCESS_TOKEN, mToken)
                .build();
    }

    private Unit onTitleReceived() {
        if (mTitleLoadedListener != null) {
            mTitleLoadedListener.onTitleLoaded();
        }
        return Unit.INSTANCE;
    }

    protected static class DocumentWebViewClient extends CertificateSafeWebClient {

        /**
         * Список url, которые выстреливают в случае кривой сессии.
         * Мы будем их перехватывать и пытаться повторить запрос.
         */
        private static final String[] REDIRECT_URL = new String[]{
                String.format("https://%s/auth/index.html", Server.getInstance().getHost().getHostUrl()),
                "https://cdn.sbis.ru/detect/"
        };

        private static final String URL_404 = Server.getInstance().getHost().getFullHostUrl().concat("/false");
        private static final String DEFAULT_URL = Server.getInstance().getHost().getFullHostUrl().concat("/opendoc.html");

        @NonNull
        private final LocalUrlOpener mLocalUrlOpener;

        @NonNull
        private final WebViewRenderProcessGoneHandler renderProcessGoneHandler =
                new WebViewRenderProcessGoneHandler();

        protected DocumentWebViewClient(@NonNull Context context) {
            super(apiService.getCertificateResolver());
            mLocalUrlOpener = new LocalUrlOpener(context);
        }

        /**
         * SelfDocumented
         */
        public void setRendererDeathListener(@Nullable WebViewRendererDeathListener rendererDeathListener) {
            renderProcessGoneHandler.setRendererDeathListener(rendererDeathListener);
        }

        @Override
        public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull String url) {
            DocumentWebView webView = (DocumentWebView) view;
            if (isRedirectUrl(url)) {
                view.clearHistory();
                webView.setAuthorizationHeader();
                view.loadUrl(webView.mDocumentUrl);
            } else if (url.equals(URL_404) && webView.mDocumentId != null) {
                view.loadUrl(DEFAULT_URL);
            } else if (areUrlEqual(view.getUrl(), url)) {
                return false;
            } else if (!openLocalUrl(url)) {
                view.loadUrl(url);
            }
            return true;
        }

        private boolean areUrlEqual(String currentUrl, String nextUrl) {
            try {
                String decodedUrl = URLDecoder.decode(nextUrl, "UTF-8");
                return decodedUrl.equals(currentUrl);
            } catch (Exception e) {
                Timber.e(e);
                return false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Timber.e(DocumentWebView.class.getName(), "onReceivedError " + errorCode + ", " + description + ", " + failingUrl);
        }

        @Override
        public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
            return renderProcessGoneHandler.onWebViewRenderProcessGone(detail);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        protected boolean openLocalUrl(@NonNull String url) {
            return mLocalUrlOpener.open(url);
        }

        protected boolean isRedirectUrl(@NonNull String url) {
            for (String redirectUrl : REDIRECT_URL) {
                if (url.contains(redirectUrl)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Слушатель событий {@link DocumentWebView}
     */
    interface Listener extends AdvancedWebView.Listener {

        /**
         * Событие о добавлении новой страницы в стек
         *
         * @see #setSupportMultipleWindows(boolean)
         */
        void onPageAdded();

        /**
         * Событие об удалении текущей страницы из стека
         *
         * @see #setSupportMultipleWindows(boolean)
         */
        void onPageRemoved();
    }

    /**
     * Слушатель загрузки файлов по ссылке
     */
    interface LoadFileByLinkInterface {
        /**
         * Обработка ситуации, когда файл по указанному пути уже существует
         *
         * @param fileName   имя файла
         * @param folderPath путь до папки, в которой лежит файл
         * @param url        ссылка для скачивания файла
         */
        void onFileAlreadyExists(@NonNull String fileName, @NonNull String folderPath, @NonNull String url);

        /**
         * Обработка ситуации, когда необходимо скачать файл
         *
         * @param fileName   имя файла
         * @param folderPath путь до папки, в которой лежит файл
         * @param url        ссылка для скачивания файла
         */
        void onFileLoading(@NonNull String fileName, @NonNull String folderPath, @NonNull String url);

        /**
         * Обработка ситуации, когда загрузка файла невозможна
         */
        void onUnableToLoadFile();

        /**
         * @return true если в данный момент слушатель уже обрабатывает какое-либо действие
         */
        boolean hasActiveAction();
    }
}