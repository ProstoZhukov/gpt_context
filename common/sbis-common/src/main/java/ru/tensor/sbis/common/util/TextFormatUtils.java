package ru.tensor.sbis.common.util;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import java.util.Map;

import kotlin.jvm.functions.Function1;
import ru.tensor.sbis.common.util.urldetector.Url;
import ru.tensor.sbis.common.util.urldetector.UrlPosition;
import ru.tensor.sbis.common.util.urldetector.detection.UrlDetector;
import ru.tensor.sbis.common.util.urldetector.detection.UrlDetectorOptions;
import ru.tensor.sbis.design.text_span.span.EllipsizeLineSpan;

public class TextFormatUtils {

    private static final String ELLIPSIZE_STRING = "\u2026";

    private static class URLSpanNoUnderline extends URLSpan {
        @ColorInt
        private final int mLinkColor;
        private final Function1<String, Boolean> clickHandler;

        URLSpanNoUnderline(String url, @ColorInt int linkColor) {
            this(url, linkColor, null);
        }

        URLSpanNoUnderline(String url, @ColorInt int linkColor, Function1<String, Boolean> handler) {
            super(url);
            mLinkColor = linkColor;
            clickHandler = handler;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mLinkColor);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            if (clickHandler == null || !clickHandler.invoke(getURL()))
                super.onClick(widget);
        }
    }

    @Nullable
    public static Spannable highlightLinks(@Nullable CharSequence text, @ColorInt int linkColor) {
        return highlightLinks(text, linkColor, null);
    }

    @Nullable
    public static Spannable highlightLinks(@Nullable CharSequence text, @ColorInt int linkColor, Function1<String, Boolean> urlClickHandler) {
        if (text == null) {
            return null;
        }

        SpannableString spannableText = SpannableString.valueOf(text);

        UrlDetector detector = new UrlDetector(text.toString(), UrlDetectorOptions.BRACKET_AND_QUOTE_MATCH);
        Map<UrlPosition, Url> detectedUrls = detector.detect();

        for (Map.Entry<UrlPosition, Url> entry : detectedUrls.entrySet()) {
            Url url = entry.getValue();
            int start = entry.getKey().getStart();
            int end = entry.getKey().getEnd();

            URLSpanNoUnderline span = new URLSpanNoUnderline(url.normalize().getFullUrl(), linkColor, urlClickHandler);
            spannableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableText;
    }

    @Nullable
    public static Spannable highlightCollapsedLinks(@Nullable CharSequence text, @Nullable CharSequence collapsedText, @ColorInt int linkColor) {
        if (text == null || collapsedText == null) {
            return null;
        }

        SpannableString spannableText = SpannableString.valueOf(collapsedText);

        UrlDetector detector = new UrlDetector(text.toString(), UrlDetectorOptions.BRACKET_AND_QUOTE_MATCH);
        Map<UrlPosition, Url> detectedUrls = detector.detect();

        for (Map.Entry<UrlPosition, Url> entry : detectedUrls.entrySet()) {
            Url url = entry.getValue();
            URLSpanNoUnderline span = new URLSpanNoUnderline(url.normalize().getFullUrl(), linkColor);
            int start = entry.getKey().getStart();
            int end = entry.getKey().getEnd();

            if (collapsedText.length() > end) {
                spannableText.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (collapsedText.length() > start && collapsedText.length() < end) {
                spannableText.setSpan(span, start, collapsedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableText;
    }

    /**
     * Усекает длину контента до одной строки и оборачивает его
     * в {@link EllipsizeLineSpan}. В случае, если текст
     * не поместится в контейнер для отрисовки, в конце
     * строки будет добавлен символ ellipsize.
     * @param text - текст для усечения
     */
    public static Spannable ellipsize(@NonNull String text) {
        return ellipsize(text, true);
    }

    /**
     * Усекает длину контента до одной строки и оборачивает его
     * в {@link EllipsizeLineSpan}. В случае, если текст
     * не поместится в контейнер для отрисовки, в конце
     * строки будет добавлен символ ellipsize.
     * @param text          - текст для усечения
     * @param enableCache   - нужно ли кешировать работу {@link EllipsizeLineSpan}.
     */
    public static Spannable ellipsize(@NonNull String text, boolean enableCache) {
        text = prepareEllipsize(text);
        Spannable ellipsized = new SpannableString(text);
        ellipsized.setSpan(new EllipsizeLineSpan(enableCache), 0, ellipsized.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ellipsized;
    }

    @NonNull
    public static String prepareEllipsize(@NonNull String text) {
        final int end = text.indexOf('\n');
        return end != -1 ? text.substring(0, end).concat(ELLIPSIZE_STRING) : text;
    }

    private TextFormatUtils() {

    }
}
