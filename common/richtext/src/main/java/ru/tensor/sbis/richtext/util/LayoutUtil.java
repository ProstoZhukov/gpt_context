package ru.tensor.sbis.richtext.util;

import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.UiThread;

/**
 * Утилита для расширения возможностей {@link Layout}.
 *
 * @author am.boldinov
 */
@UiThread
public class LayoutUtil {

    private static final class Work {

        @NonNull
        private static final TextPaint paint = new TextPaint();
        @NonNull
        private static final Paint.FontMetricsInt metrics = new Paint.FontMetricsInt();
        @NonNull
        private static final Paint.FontMetricsInt calcMetrics = new Paint.FontMetricsInt();
    }

    /**
     * Возвращает относительное значение {@link android.graphics.Paint.FontMetricsInt#ascent} внутри
     * layout для конкретной строки, по аналогии с {@link Layout#getLineTop(int)}.
     * По умолчанию считает значение для всей строки, в случае необходимости получения результата
     * для конкретных символов/подстроки необходимо использовать {@link #getLineAscentRelative(Layout, int, int, int)}.
     *
     * @param layout layout с текстом
     * @param line номер строки
     */
    public static int getLineAscentRelative(@NonNull Layout layout, int line) {
        return getLineAscentRelative(layout, line, layout.getLineStart(line), layout.getLineEnd(line));
    }

    /**
     * @see #getLineAscentRelative(Layout, int)
     */
    public static int getLineAscentRelative(@NonNull Layout layout, int line, int start, int end) {
        updateMetrics(layout, line, start, end);
        return layout.getLineTop(line) + Math.abs(Work.metrics.top - Work.metrics.ascent);
    }

    /**
     * Возвращает относительное значение {@link android.graphics.Paint.FontMetricsInt#descent} внутри
     * layout для конкретной строки, по аналогии с {@link Layout#getLineBottom(int)}.
     * По умолчанию считает значение для всей строки, в случае необходимости получения результата
     * для конкретных символов/подстроки необходимо использовать {@link #getLineDescentRelative(Layout, int, int, int)}.
     *
     * @param layout layout с текстом
     * @param line номер строки
     */
    public static int getLineDescentRelative(@NonNull Layout layout, int line) {
        return getLineDescentRelative(layout, line, layout.getLineStart(line), layout.getLineEnd(line));
    }

    /**
     * @see #getLineDescentRelative(Layout, int)
     */
    public static int getLineDescentRelative(@NonNull Layout layout, int line, int start, int end) {
        updateMetrics(layout, line, start, end);
        return layout.getLineBottom(line) - Math.abs(Work.metrics.bottom - Work.metrics.descent);
    }

    /**
     * Возвращает реальную ширину текста в {@link StaticLayout}
     *
     * @param layout layout с текстом
     */
    public static float getTextWidth(@NonNull StaticLayout layout) {
        float max = 0;
        for (int i = 0; i < layout.getLineCount(); i++) {
            max = Math.max(layout.getLineWidth(i), max);
        }
        return max;
    }

    /**
     * @return paint для единоразовой работы внутри Span.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @NonNull
    public static TextPaint getWorkPaint() {
        return Work.paint;
    }

    /**
     * @return fontMetrics для единоразовой работы при извлечении метрик из {@link Paint}.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static Paint.FontMetricsInt getWorkMetrics() {
        return Work.metrics;
    }

    private static void updateMetrics(@NonNull Layout layout, int line, int start, int end) {
        if (layout.getText() instanceof Spanned) {
            SpannableUtil.invalidateSpanHeight(Work.metrics);
            final Spanned text = (Spanned) layout.getText();
            if (SpannableUtil.hasNextSpanTransition(text, start - 1, end + 1, MetricAffectingSpan.class)) {
                final MetricAffectingSpan[] spans = text.getSpans(start, end, MetricAffectingSpan.class);
                for (MetricAffectingSpan span : spans) {
                    Work.paint.set(layout.getPaint());
                    span.updateMeasureState(Work.paint);
                    Work.paint.getFontMetricsInt(Work.calcMetrics);
                    if (getTextHeight(Work.calcMetrics) > getTextHeight(Work.metrics)) {
                        SpannableUtil.copyMetrics(Work.calcMetrics, Work.metrics);
                    }
                }
            }
            if (getTextHeight(Work.metrics) == 0) {
                layout.getPaint().getFontMetricsInt(Work.metrics);
            }
            Work.metrics.bottom = Math.max(Work.metrics.bottom, layout.getLineDescent(line));
            Work.metrics.top = Math.min(Work.metrics.top, layout.getLineAscent(line));
        } else {
            layout.getPaint().getFontMetricsInt(Work.metrics);
        }
    }

    private static int getTextHeight(@NonNull Paint.FontMetricsInt fm) {
        return fm.descent - fm.ascent;
    }
}
