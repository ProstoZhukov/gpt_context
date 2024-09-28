package ru.tensor.sbis.richtext.span.view.youtube;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;

/**
 * Утилита для работы с ютуб видео
 * <p>
 * Created by am.boldinov on 09.03.17.
 */
public class YouTubeUtil {

    /**
     * Открывает плеер для проигрывания ютуб видео.
     */
    public static void playYouTubeVideo(@NonNull Context context, String videoId) {
        openAsUrl(context, videoId);
    }

    /**
     * Открывает ютуб видео во внешних приложениях.
     */
    public static void openAsUrl(@NonNull Context context, String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        final String url = getVideoUrl(videoId);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * Является ли переданная ссылка ютуб видео.
     */
    public static boolean isYouTubeVideo(@Nullable String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        final String host = Uri.parse(url).getHost();
        return host != null && host.contains("youtube.com");
    }

    /**
     * Типы превью ютуб видео
     */
    public enum PreviewType {
        NORMAL_QUALITY("default", 120, 90),
        MEDIUM_QUALITY("mqdefault", 320, 180),
        HIGH_QUALITY("hqdefault", 480, 360),
        START_FRAME("1", 120, 90),
        MIDDLE_FRAME("2", 120, 90),
        END_FRAME("3", 120, 90),
        STANDARD_PREVIEW("sddefault", 640, 480),
        MAX_DEF_PREVIEW("maxresdefault", 1920, 1080);

        private final String route;
        private final int width;
        private final int height;

        PreviewType(String route, int width, int height) {
            this.route = route;
            this.width = width;
            this.height = height;
        }

        String getRoute() {
            return route;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

    /**
     * Формирует ссылку на видео
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public static String getVideoUrl(@NonNull String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return null;
        }
        return String.format("https://www.youtube.com/watch?v=%s", videoId);
    }

    /**
     * Формирует ссылку на превью изображение с качеством по умолчанию
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String getDefaultPreviewUrl(@NonNull String videoId) {
        return getPreviewUrl(videoId, PreviewType.HIGH_QUALITY);
    }

    /**
     * Формирует ссылку на превью изображение
     */
    @Nullable
    public static String getPreviewUrl(@NonNull String videoId, @NonNull PreviewType type) {
        if (TextUtils.isEmpty(videoId)) {
            return null;
        }
        return String.format("https://i1.ytimg.com/vi/%s/%s.jpg", videoId, type.getRoute());
    }

    /**
     * Достает из ссылки на ютуб видео идентификатор этого видео
     */
    @Nullable
    public static String pickVideoId(@Nullable String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int slash = url.lastIndexOf("/");
        return slash > -1 ? url.substring(slash + 1) : null;
    }
}
