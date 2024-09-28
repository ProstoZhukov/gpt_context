package ru.tensor.sbis.common.util;

import android.webkit.URLUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import ru.tensor.sbis.network_native.httpclient.Server;

/**
 * Утилита для формирования/работы с ссылками на превьювер.
 * Документация по сервису превьювера: https://online.sbis.ru/shared/disk/5af4697f-9f8d-4516-a5bd-5c2258566482
 * <p>
 * Примерный вид ссылки https://[online fix test pre-test dev].sbis.ru/previewer/[c/ r/ m/][d+/][d+/][%s]
 */
public class PreviewerUrlUtil {

    public static final String PREVIEWER_REGEX = "/previewer/";

    public static final String PREVIEWER_MODE_REGEX = ScaleMode.valuesAsRegex();

    public static final String PREVIEWER_SIZE_REGEX = "\\d+/";
    public static final String PREVIEWER_SIZES_REGEX = PREVIEWER_SIZE_REGEX + PREVIEWER_SIZE_REGEX;
    public static final String PREVIEWER_UNKNOWN_SIZES = "%d/%d";
    public static final String PREVIEWER_SIZES_UNIVERSAL_REGEX =
            "(" + PREVIEWER_SIZES_REGEX + "|" + PREVIEWER_UNKNOWN_SIZES + "/" + ")";

    public static final String PREVIEWER_FULL_REGEX = PREVIEWER_REGEX + PREVIEWER_MODE_REGEX + "?" + PREVIEWER_SIZES_UNIVERSAL_REGEX;

    /**
     * Способ масштабирования картинки
     */
    public enum ScaleMode {
        /**
         * Обрезка
         */
        CROP("c/"),
        /**
         * Масштабирование по большей стороне (изображение полностью впишется в область)
         */
        RESIZE("r/"),
        /**
         * Масштабирование по меньшей стороне (размеры изображения будут не меньше, чем заданная область)
         */
        SCALING_BY_MIN_SIDE("m/"),

        /**
         * Загрузка прогрессивного изображения
         */
        PROGRESSIVE("p/");

        private final String value;

        ScaleMode(String value) {
            this.value = value;
        }

        /**
         * Формирование префикса превьювера с типом масштабирования и размерами
         *
         * @param prefix  префикс, содержащий {@link #PREVIEWER_REGEX}
         * @param width   ширина
         * @param height  высота
         * @param postfix постфикс, отображаемый после размеров
         * @return отформатированная строка
         */
        private String concatWithPrefixPostfixAndSizes(@NonNull String prefix, int width, int height,
                                                       @NonNull String postfix) {
            return concatWithPrefix(prefix) + width + "/" + height + postfix;
        }

        /**
         * Формирование префикса превьювера с типом масштабирования
         *
         * @param prefix префикс, содержащий {@link #PREVIEWER_REGEX}
         * @return отформатированная строка
         */
        private String concatWithPrefix(@NonNull String prefix) {
            return prefix + value;
        }

        /**
         * Формирование значений в виде регулярного выражения вида (c/|r/|...p/)
         *
         * @return Регулярное выражение
         */
        public static String valuesAsRegex() {
            List<String> values = ArraysKt.map(values(), scaleMode -> scaleMode.value);
            return CollectionsKt.joinToString(values, "|", "(", ")", -1, "", null);
        }
    }

    /**
     * Формирование ссылки для изображения на previewer.
     * Если ссылка на внешний ресурс, возвращается без изменений.
     * Масштабирование по умолчанию {@link ScaleMode#SCALING_BY_MIN_SIDE}.
     *
     * @param url    ссылка для проверки и преобразования
     * @param width  ширина
     * @param height высота
     * @return ссылка на изображение указанных размеров, либо на внешний ресурс.
     */
    @Nullable
    public static String formatImageUrl(@Nullable String url, @Px int width, @Px int height) {
        return formatImageUrl(url, width, height, ScaleMode.SCALING_BY_MIN_SIDE);
    }

    /**
     * Формирование ссылки для изображения на previewer.
     * Если ссылка на внешний ресурс, возвращается без изменений.
     *
     * @param url       ссылка для проверки и преобразования
     * @param width     ширина
     * @param height    высота
     * @param scaleMode способ масштабирования картинки
     * @return ссылка на изображение указанных размеров, либо на внешний ресурс.
     */
    @Nullable
    public static String formatImageUrl(@Nullable String url, @Px int width, @Px int height,
                                        @NonNull ScaleMode scaleMode) {
        if (CommonUtils.isEmpty(url)) {
            return null;
        }
        if (URLUtil.isNetworkUrl(url)) { // ссылка сформирована полностью todo проверить
            String hostUrl = Server.getSbisHostUrl(url);
            if (hostUrl == null) { // ссылка на внешний ресурс
                return url;
            }
            if (!url.contains(PREVIEWER_REGEX)) { // если ссылка не содержит PREVIEWER_REGEX, добавляем
                return Pattern.compile(hostUrl).matcher(url)
                        .replaceFirst(scaleMode.concatWithPrefixPostfixAndSizes(hostUrl + PREVIEWER_REGEX, width, height, StringUtils.EMPTY));
            } else { // изменяем параметры
                return replacePreviewerUrlPart(url, width, height, scaleMode);
            }
        } else { // информативная часть ссылки
            url = url.startsWith("/") ? url : "/".concat(url);
            if (!url.contains(PREVIEWER_REGEX)) { // если ссылка не содержит PREVIEWER_REGEX, добавляем
                url = scaleMode.concatWithPrefixPostfixAndSizes(PREVIEWER_REGEX, width, height, url);
            } else {
                url = replacePreviewerUrlPart(url, width, height, scaleMode);
            }
            return UrlUtils.formatUrlWithHost(url);
        }
    }

    /**
     * Замена/установка размеров изображения в ссылке, содержащей {@link #PREVIEWER_REGEX}.
     * Предварительно проверяется содержание в ссылке подстроки {@link #PREVIEWER_REGEX}
     *
     * @param url       ссылка на изображение
     * @param width     ширина
     * @param height    высота
     * @param scaleMode параметр, отвечающий за способ масштабирования
     * @return ссылка на изображение указанных размеров
     */
    @NonNull
    public static String replacePreviewerUrlPartWithCheck(@NonNull String url,
                                                          @Px int width,
                                                          @Px int height,
                                                          @NonNull ScaleMode scaleMode) {
        if (!url.contains(PREVIEWER_REGEX)) {
            return url;
        } else {
            return replacePreviewerUrlPart(url, width, height, scaleMode);
        }
    }

    /**
     * Преобразование ссылки, содержащей {@link #PREVIEWER_REGEX}.
     * Добавление/замена параметра, отвечающего за масштабирование, установка/замена размеров.
     *
     * @param url       ссылка
     * @param width     ширина
     * @param height    высота
     * @param scaleMode способ масштабирования картинки
     * @return ссылка на изображение указанных размеров
     */
    private static String replacePreviewerUrlPart(@NonNull String url, @Px int width, @Px int height,
                                                  @NonNull ScaleMode scaleMode) {
        // замену crop игнорируем, т.к. замена на resize может сработать некорректно (если указано позиционирование)
        if (url.contains(ScaleMode.CROP.concatWithPrefix(PREVIEWER_REGEX))) {
            if (ScaleMode.CROP == scaleMode) {
                return resizeImagePreviewerUrl(url, width, height, ScaleMode.CROP.concatWithPrefix(PREVIEWER_REGEX), ScaleMode.CROP);
            } else {
                return url;
            }
        }

        if (url.contains(ScaleMode.RESIZE.concatWithPrefix(PREVIEWER_REGEX))) {
            return resizeImagePreviewerUrl(url, width, height, ScaleMode.RESIZE.concatWithPrefix(PREVIEWER_REGEX), scaleMode);
        }

        if (url.contains(ScaleMode.SCALING_BY_MIN_SIDE.concatWithPrefix(PREVIEWER_REGEX))) {
            return resizeImagePreviewerUrl(url, width, height, ScaleMode.SCALING_BY_MIN_SIDE.concatWithPrefix(PREVIEWER_REGEX), scaleMode);
        }

        if (url.contains(ScaleMode.PROGRESSIVE.concatWithPrefix(PREVIEWER_REGEX))) {
            return resizeImagePreviewerUrl(url, width, height, ScaleMode.PROGRESSIVE.concatWithPrefix(PREVIEWER_REGEX), scaleMode);
        }

        if (Pattern.compile(PREVIEWER_REGEX + PREVIEWER_SIZES_REGEX).matcher(url).find()) { // ссылка на previewer без опций с размерами
            return resizeImagePreviewerUrl(url, width, height, PREVIEWER_REGEX, scaleMode);
        }

        // ссылка на previewer без опций с заглушками вместо размеров
        if (Pattern.compile(PREVIEWER_REGEX + PREVIEWER_UNKNOWN_SIZES).matcher(url).find()) {
            return resizeImagePreviewerUrl(url, width, height, PREVIEWER_REGEX, scaleMode);
        }

        Matcher matcher = Pattern.compile(PREVIEWER_REGEX + PREVIEWER_SIZE_REGEX).matcher(url);
        if (matcher.find()) { // ссылка на previewer без опций с размером для квадратной области
            return matcher.replaceFirst(scaleMode.concatWithPrefixPostfixAndSizes(PREVIEWER_REGEX, width, height, "/"));
        }

        // ссылка на previewer без опций и размеров
        return Pattern.compile(PREVIEWER_REGEX).matcher(url)
                .replaceFirst(scaleMode.concatWithPrefixPostfixAndSizes(PREVIEWER_REGEX, width, height, "/"));
    }

    /**
     * Преобразование ссылки, содержащей {@link #PREVIEWER_REGEX} и параметр, отвечающий за масштабирование.
     * Установка размеров и изменение способа масштабирования на указанный.
     *
     * @param url           ссылка
     * @param width         ширина
     * @param height        высота
     * @param replacingPart заменяемая подстрока
     * @param scaleMode     способ масштабирования картинки
     * @return ссылка на изображение указанных размеров
     */
    private static String resizeImagePreviewerUrl(@NonNull String url, @Px int width, @Px int height,
                                                  @NonNull String replacingPart, @NonNull ScaleMode scaleMode) {
        String newPart = scaleMode.concatWithPrefix(PREVIEWER_REGEX);
        url = resetImageUrlSizes(url, replacingPart, newPart);
        url = insertSizeInImageUrl(url, width, height);
        return url;
    }

    /**
     * Сброс ранее установленных размеров изображения + замена параметров ссылки.
     *
     * @param imageUrl      ссылка, содержащая {@link #PREVIEWER_REGEX}
     * @param replacingPart заменяемая подстрока, содержащая параметры
     * @param newPart       подстрока, содержащая параметры, на которую заменяем
     * @return ссылка на изображение с пустыми размерами
     */
    @NonNull
    private static String resetImageUrlSizes(@NonNull String imageUrl, @NonNull String replacingPart,
                                             @NonNull String newPart) {
        Pattern pattern = Pattern.compile(replacingPart + PREVIEWER_SIZES_UNIVERSAL_REGEX);
        imageUrl = pattern.matcher(imageUrl).replaceFirst(newPart + PREVIEWER_UNKNOWN_SIZES + "/");
        return imageUrl;
    }

    /**
     * Установка новых размеров изображения.
     *
     * @param imageUrl ссылка, содержащая {@link #PREVIEWER_REGEX}
     * @param width    ширина
     * @param height   высота
     * @return ссылка на изображение с указанными размерами
     */
    private static String insertSizeInImageUrl(@NonNull String imageUrl, int width, int height) {
        return imageUrl.replaceFirst(PreviewerUrlUtil.PREVIEWER_UNKNOWN_SIZES, width + "/" + height);
    }

    /**
     * Сброс ранее установленных размеров изображения.
     *
     * @param imageUrl ссылка, содержащая {@link #PREVIEWER_REGEX} с масштабированием типа {@link ScaleMode#SCALING_BY_MIN_SIDE}
     * @return ссылка на изображение с пустыми размерами
     */
    @NonNull
    public static String resetImageUrlScalingByMinSideSizes(@NonNull String imageUrl) {
        return resetImageUrlSizes(imageUrl, ScaleMode.SCALING_BY_MIN_SIDE.concatWithPrefix(PREVIEWER_REGEX),
                ScaleMode.SCALING_BY_MIN_SIDE.concatWithPrefix(PREVIEWER_REGEX));
    }

    /**
     * Удаление предустановленных размеров для формирования ссылки на оригинальное изображение.
     *
     * @param url ссылка для обработки
     * @return оригинальная ссылка (на превьювер)
     */
    public static String convertToOriginalUrl(@NonNull String url) {
        Pattern pattern;
        if (url.contains(ScaleMode.RESIZE.concatWithPrefix(PREVIEWER_REGEX))) {
            pattern = Pattern.compile(ScaleMode.RESIZE.concatWithPrefix(PREVIEWER_REGEX) + PREVIEWER_SIZES_REGEX);
        } else if (url.contains(ScaleMode.SCALING_BY_MIN_SIDE.concatWithPrefix(PREVIEWER_REGEX))) {
            pattern = Pattern.compile(ScaleMode.SCALING_BY_MIN_SIDE.concatWithPrefix(PREVIEWER_REGEX) + PREVIEWER_SIZES_REGEX);
        } else if (Pattern.compile(PREVIEWER_REGEX + PREVIEWER_SIZES_REGEX).matcher(url).find()) {
            pattern = Pattern.compile(PREVIEWER_REGEX + PREVIEWER_SIZES_REGEX);
        } else {
            pattern = Pattern.compile(PREVIEWER_REGEX + PREVIEWER_SIZE_REGEX);
        }
        return pattern.matcher(url).replaceFirst(PREVIEWER_REGEX);
    }
}
