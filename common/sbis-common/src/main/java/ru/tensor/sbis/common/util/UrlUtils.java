package ru.tensor.sbis.common.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.tensor.sbis.network_native.httpclient.Server;
import ru.tensor.sbis.network_native.httpclient.Server.Host;
import ru.tensor.sbis.verification_decl.account.UserAccount;

public class UrlUtils {

    @NonNull
    public static final String FILE_SD_OBJECT = "FileSD";
    public static final String EXTERNAL_DOCUMENT_OBJECT = "ВнешнийДокумент";
    public static final String PROJECT_OBJECT = "Проект";
    public static final String PHASE_OBJECT = "Этап";
    @Px
    private static final int ATTACHMENT_MAX_SIDE_SIZE = 2048;

    public static final String DISK_API_V1_SERVICE_POSTFIX = "/disk/api/v1/";
    private static final String DISK_SERVICE_POSTFIX = "/disk/";
    public static final String URL_PARAMETER_UUID = "guid";

    public static final String MAIL_URL_PREFIX = "mailto:";
    public static final String TEL_URL_PREFIX = "tel:";

    public static boolean isMailUrl(@Nullable String url) {
        return url != null && url.startsWith(MAIL_URL_PREFIX);
    }

    public static boolean isTelUrl(@Nullable String url) {
        return url != null && url.startsWith(TEL_URL_PREFIX);
    }

    @NonNull
    public static String getMainServiceUrl() {
        return Server.getInstance().getHost().getFullHostUrl();
    }

    @NonNull
    public static String getDiskServiceUrl() {
        return Server.getInstance().getHost().getFullHostUrl() + DISK_SERVICE_POSTFIX;
    }

    @Nullable
    public static String getMyProfilePhotoUrl() {
        JsonObject json = new JsonObject();
        json.addProperty("kind", "default");
        return buildUrlByMethod("PProfileServicePerson.GetPhoto", json, 0L);
    }

    // Нужно получать url фото через этот метод
    @Nullable
    public static String getPhotoUrlById(@NonNull String photoId, @Px int size) {
        UrlWithSize photoFromCache = PhotoUrlByIdCache.get(photoId);
        if (photoFromCache != null && photoFromCache.getSize() > size) {
            return photoFromCache.getUrl();
        }
        final JsonObject json = new JsonObject();
        json.addProperty("Ид", photoId);
        String result = buildPreviewUrlByMethod("СервисПрофилей.ФотоПоИд", json, 0, size);
        PhotoUrlByIdCache.put(photoId, result, size);
        return result;
    }

    @Nullable
    public static String getPersonPhotoUrlByPhotoId(@NonNull String id, int minSide) {
        if (id.isEmpty()) {
            ThrowableExtKt.safeThrow(new IllegalStateException("Empty photo id"));
        }
        JsonObject json = new JsonObject();
        json.addProperty("Id", id);
        return PreviewerUrlUtil.formatImageUrl(
                buildUrlByMethod("ProfileServiceMobile.PhotoById", json, 0L),
                minSide,
                minSide,
                PreviewerUrlUtil.ScaleMode.CROP
        );
    }

    @Nullable
    public static String getImageUrl(@NonNull UserAccount account) {
        if (account.getUuid() != null) {
            return getImageUrl(UUIDUtils.toString(account.getUuid()));
        }
        return null;
    }

    public enum ImageSize {
        MINI("mini"),
        DEFAULT("default"),
        ORIGINAL("original");

        private final String paramValue;

        ImageSize(@NonNull String paramValue) {
            this.paramValue = paramValue;
        }

        @NonNull
        private String getParam() {
            return paramValue;
        }
    }

    @Nullable
    public static String getImageUrl(@Nullable String uuid, @NonNull ImageSize imageSize) {
        final JsonObject json = new JsonObject();
        json.addProperty("Персона", uuid);
        json.addProperty("Тип", imageSize.getParam()); // возвращает 200x200, если передать origin - вернет оригинальное
        return buildUrlByMethod("СервисПрофилей.Фото", json, 0);
    }

    @Nullable
    public static String getImageUrl(@Nullable String uuid) {
        return getImageUrl(uuid, ImageSize.DEFAULT);
    }

    @Nullable
    public static String getOriginalImageUrl(@Nullable String uuid) {
        return getImageUrl(uuid, ImageSize.ORIGINAL);
    }

    @Nullable
    public static String buildUrlByMethod(@NonNull String method, @NonNull JsonObject json, long id) {
        return buildUrlByMethodWithHost("service", method, json, id);
    }

    /**
     * Метод формирования ссылки на превью вложения с размерами по умолчанию {@link UrlUtils#ATTACHMENT_MAX_SIDE_SIZE}
     * без оберзки полей справа и снизу
     * См. также  {@link UrlUtils#getAttachmentPreviewUrl(String, String, String, String, String, int, int)}
     */
    @NonNull
    public static String getAttachmentPreviewUrl(@NonNull String service,
                                                 @NonNull String object,
                                                 @NonNull String attachmentId,
                                                 @Nullable String redactionId,
                                                 @Nullable String version) {
        return getAttachmentPreviewUrl(service, object, attachmentId, redactionId, version, ATTACHMENT_MAX_SIDE_SIZE, ATTACHMENT_MAX_SIDE_SIZE);
    }

    /**
     * Метод формирования ссылки на превью вложения без оберзки полей справа и снизу
     * См. также  {@link UrlUtils#getAttachmentPreviewUrl(String, String, String, String, String, int, int, boolean, ru.tensor.sbis.common.util.PreviewerUrlUtil.ScaleMode)}
     */
    @NonNull
    public static String getAttachmentPreviewUrl(@NonNull String service,
                                                 @NonNull String object,
                                                 @NonNull String attachmentId,
                                                 @Nullable String redactionId,
                                                 @Nullable String version,
                                                 @Px int width,
                                                 @Px int height) {
        return getAttachmentPreviewUrl(service, object, attachmentId, redactionId, version, width, height, false, PreviewerUrlUtil.ScaleMode.SCALING_BY_MIN_SIDE);
    }

    /**
     * Метод формирования ссылки на превью вложения.
     * Загружать картинку по сформированной ссылке следует ТОЛЬКО С АВТОРИЗАЦИЕЙ!
     *
     * @param service               Адрес сервиса, на котором находится бизнес-логика, обрабатывающая вложение
     * @param object                Название объекта бизнес-логики, обрабатывающей вложение
     * @param attachmentId          Идентификатор вложения
     * @param redactionId           Идентификатор редакции вложения
     * @param version               Версия (или хэш) вложения
     * @param width                 Ширина запрашиваемой превью
     * @param height                Высота запрашиваемой превью
     * @param cropRightBottomFields Обезать поля справа и снизу (передача высоты как 0 на БЛ)
     * @return Ссылка на вложение, сформированная на основании входные параметров и обёрнутая в previewer
     */
    @NonNull
    public static String getAttachmentPreviewUrl(@NonNull String service,
                                                 @NonNull String object,
                                                 @NonNull String attachmentId,
                                                 @Nullable String redactionId,
                                                 @Nullable String version,
                                                 @Px int width,
                                                 @Px int height,
                                                 boolean cropRightBottomFields,
                                                 @NonNull PreviewerUrlUtil.ScaleMode scaleMode) {
        JsonObject params = prepareParamsForAttachmentUrl(service, object, attachmentId, redactionId, version, width, cropRightBottomFields ? 0 : height);
        String url = buildPreviewUrlByMethod("docview_auth/service", "DocView.ReadAttachmentAsPreviewSync", params, 0, width, height, scaleMode);
        return url == null ? "" : url;
    }

    /**
     * Метод формирования ссылки на превью вложения без previewer-а
     * Загружать картинку по сформированной ссылке следует ТОЛЬКО С АВТОРИЗАЦИЕЙ!
     *
     * @param service               Адрес сервиса, на котором находится бизнес-логика, обрабатывающая вложение
     * @param object                Название объекта бизнес-логики, обрабатывающей вложение
     * @param attachmentId          Идентификатор вложения
     * @param redactionId           Идентификатор редакции вложения
     * @param version               Версия (или хэш) вложения
     * @param width                 Ширина запрашиваемой превью
     * @param height                Высота запрашиваемой превью
     * @param cropRightBottomFields Обезать поля справа и снизу (передача высоты как 0 на БЛ)
     * @return Ссылка на вложение, сформированная на основании входных параметров
     */
    @NonNull
    public static String getSimpleAttachmentPreviewUrl(@NonNull String service,
                                                       @NonNull String object,
                                                       @NonNull String attachmentId,
                                                       @Nullable String redactionId,
                                                       @Nullable String version,
                                                       @Px int width,
                                                       @Px int height,
                                                       boolean cropRightBottomFields) {
        JsonObject params = prepareParamsForAttachmentUrl(service, object, attachmentId, redactionId, version, width, cropRightBottomFields ? 0 : height);
        String url = buildUrlByMethod("docview_auth/service", "DocView.ReadAttachmentAsPreviewSync", params, 0);
        return url == null ? "" : UrlUtils.formatUrlWithHost(url);
    }

    @NonNull
    private static JsonObject prepareParamsForAttachmentUrl(@NonNull String service,
                                                            @NonNull String object,
                                                            @NonNull String attachmentId,
                                                            @Nullable String redactionId,
                                                            @Nullable String version,
                                                            @Px int width,
                                                            @Px int height) {
        JsonObject json = new JsonObject();
        json.addProperty("Service", service);
        json.addProperty("Object", validateBusinessLogicObject(object));
        json.addProperty("ID", attachmentId);
        json.addProperty("IDRedaction", redactionId == null || redactionId.equals("null") ? StringUtils.EMPTY : redactionId);
        json.addProperty("Version", version == null || version.equals("null") ? StringUtils.EMPTY : version);
        json.addProperty("Width", String.valueOf(width));
        json.addProperty("Height", String.valueOf(height));
        json.addProperty("Icon", String.valueOf(false));
        json.addProperty("Transp", String.valueOf(true));
        return json;
    }

    /**
     * Метод проверяет объект бизнес-логики на валидность для запроса.
     * Все объекты бизнес-логики, кроме {@link UrlUtils#FILE_SD_OBJECT}, {@link UrlUtils#PROJECT_OBJECT} и {@link UrlUtils#PHASE_OBJECT},
     * должны быть заменены на {@link UrlUtils#EXTERNAL_DOCUMENT_OBJECT}, того требует серверная сторона.
     *
     * @param sourceObject Исходный объект бизнес-логики
     * @return Проверенный объект бизнес-логики
     */
    @NonNull
    public static String validateBusinessLogicObject(@NonNull String sourceObject) {
        switch (sourceObject) {
            case UrlUtils.FILE_SD_OBJECT:
            case UrlUtils.PROJECT_OBJECT:
            case UrlUtils.PHASE_OBJECT:
                return sourceObject;
            default:
                return UrlUtils.EXTERNAL_DOCUMENT_OBJECT;

        }
    }

    @NonNull
    public static String getServiceUrlByBusinessLogicObject(@NonNull String businessLogicObject) {
        if (UrlUtils.FILE_SD_OBJECT.equals(validateBusinessLogicObject(businessLogicObject))) {
            return getDiskServiceUrl();
        }
        return getMainServiceUrl();
    }

    @Nullable
    public static String buildPreviewUrlByMethod(@NonNull String method, @NonNull JsonObject params, long id, @Px int size) {
        return buildPreviewUrlByMethod("service", method, params, id, size, size);
    }

    @NonNull
    private static String encodeParams(@NonNull String json) throws UnsupportedEncodingException {
        byte[] plainTextBytes = json.getBytes("UTF-8");
        String base64String = Base64.encodeToString(plainTextBytes, 0);
        return URLEncoder.encode(base64String, "utf-8");
    }

    public static String formatUrl(@NonNull String url) {
        if (!TextUtils.isEmpty(url)) {
            if (URLUtil.isNetworkUrl(url)) {
                return url;
            } else {
                return formatUrlWithHost(url);
            }
        }
        return null;
    }

    /**
     * Форматирует ссылки на внутренние ресурсы, которые приходят без хоста
     */
    @Nullable
    public static String checkAndFormatUrlWithHost(@Nullable String url) {
        if (url != null && url.startsWith("/")) {
            return formatUrlWithHost(url);
        }
        return url;
    }

    @NonNull
    static String formatUrlWithHost(@NonNull String url) {
        return Server.getInstance().getHost().getFullHostUrl().concat(url.startsWith("/") ? url : "/".concat(url));
    }

    /**
     * Подстановка размеров изображения в ссылку на изображение.
     * todo Использовать такой метод извне не безопасно, т.к. можно сломать ссылку
     * todo лучше перейти на {@link PreviewerUrlUtil#replacePreviewerUrlPartWithCheck(String, int, int, PreviewerUrlUtil.ScaleMode)}
     *
     * @param urlWithPlaceholders ссылка, содержащая /previewer/ - важно
     * @param width               ширина
     * @param height              высота
     * @return ссылка на изображение с заданными размерами
     */
    @Deprecated
    public static String insertSizeInImageUrlPlaceholders(@NonNull String urlWithPlaceholders, @Px int width, @Px int height) {
        return urlWithPlaceholders.replace("%d/%d", width + "/" + height);
    }

    /**
     * todo Заменить на прямой вызов {@link PreviewerUrlUtil#resetImageUrlScalingByMinSideSizes(String)}
     */
    @NonNull
    @Deprecated
    public static String resetImageUrlPlaceholderSizes(@NonNull String imageUrl) {
        return PreviewerUrlUtil.resetImageUrlScalingByMinSideSizes(imageUrl);
    }

    public static boolean isSBISUrl(@NonNull String url) {
        return Server.getSbisHostUrl(url) != null;
    }

    @Nullable
    public static String getOpendocUuid(@NonNull String url) {
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter(URL_PARAMETER_UUID);
    }

    /**
     * Генерация ссылки содержащей название метода и аргументы без указания хоста.
     *
     * @param service сервис, с которого зовется метод
     * @param method  название метода
     * @param params  параметры
     * @param id      идентификатор
     * @return ссылка с вызовом метода
     */
    @Nullable
    private static String buildUrlByMethod(@NonNull String service,
                                           @NonNull String method,
                                           @NonNull JsonObject params,
                                           long id) {
        String methodEncoded, paramsEncoded;
        try {
            methodEncoded = URLEncoder.encode(method, "utf-8");
            paramsEncoded = UrlUtils.encodeParams(params.toString());
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        return String.format(Locale.getDefault(),
                "/%s/?id=%d&method=%s&protocol=3&params=%s",
                service,
                id,
                methodEncoded,
                paramsEncoded);
    }

    @Nullable
    private static String buildUrlByMethodWithHost(@NonNull String service,
                                                   @NonNull String method,
                                                   @NonNull JsonObject params,
                                                   long id) {
        String urlByMethod = buildUrlByMethod(service, method, params, id);
        return urlByMethod != null ? formatUrlWithHost(urlByMethod) : null;
    }

    @Nullable
    public static String buildPreviewUrlByMethod(@NonNull String service,
                                                 @NonNull String method,
                                                 @NonNull JsonObject params,
                                                 long id,
                                                 @Px int width,
                                                 @Px int height) {
        return buildPreviewUrlByMethod(service, method, params, id, width, height, PreviewerUrlUtil.ScaleMode.SCALING_BY_MIN_SIDE);
    }

    @Nullable
    public static String buildPreviewUrlByMethod(@NonNull String service,
                                                 @NonNull String method,
                                                 @NonNull JsonObject params,
                                                 long id,
                                                 @Px int width,
                                                 @Px int height,
                                                 @NonNull PreviewerUrlUtil.ScaleMode scaleMode) {
        return PreviewerUrlUtil.formatImageUrl(buildUrlByMethod(service, method, params, id), width, height, scaleMode);
    }

    /**
     * Получить список ссылок из строки
     *
     * @param value строка в которой ведётся поиск
     * @return {@link List<String>} строковый список из ссылок содержащихся в строке
     */
    public static List<String> getLinksFromString(String value) {
        // RegEx паттерн, для получения ссылок из строки
        //noinspection Annotator
        final Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
        );
        final Matcher matcher = urlPattern.matcher(value);
        final ArrayList<String> urlList = new ArrayList<>();
        // Пока Matcher что-то находит - извлекаем подстроку по индексам
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            urlList.add(value.substring(matchStart, matchEnd));
        }
        return urlList;
    }
}
