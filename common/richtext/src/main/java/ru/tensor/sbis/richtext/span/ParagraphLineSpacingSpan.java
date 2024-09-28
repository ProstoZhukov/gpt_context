package ru.tensor.sbis.richtext.span;

import android.graphics.Paint;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LineHeightSpan;
import android.text.style.UpdateLayout;

import org.apache.commons.lang3.CharUtils;

import androidx.annotation.Px;
import ru.tensor.sbis.richtext.util.HtmlHelper;

/**
 * Span для изменения расстояния между абзацами
 *
 * @author am.boldinov
 */
public final class ParagraphLineSpacingSpan implements LineHeightSpan.WithDensity, UpdateLayout {

    @Px
    private final int mSize;

    /**
     * Создает {@link ParagraphLineSpacingSpan}
     *
     * @param size расстояние между абзацами в пикселях
     */
    public ParagraphLineSpacingSpan(@Px int size) {
        mSize = size;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm, TextPaint paint) {
        final Spanned spanned = (Spanned) text;
        if (end > 0 && end < text.length() && spanned.getSpanEnd(this) == end - 1 && text.charAt(end - 1) == CharUtils.LF
                && text.charAt(end) != CharUtils.LF && text.charAt(end) != HtmlHelper.VIEW_SYMBOL
                && (spanned.nextSpanTransition(end - 1, end + 1, LineHeightSpan.class) != end
                || spanned.nextSpanTransition(end - 1, end + 1, ParagraphLineSpacingSpan.class) == end)) {
            final int size = mSize - (fm.bottom - fm.descent);
            if (size > 0) {
                fm.descent += size;
                fm.bottom += size;
            }
        }
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {

    }
}
