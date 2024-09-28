package ru.tensor.sbis.richtext.span;

import android.graphics.Paint;
import androidx.annotation.Px;
import android.text.Spanned;
import android.text.style.LineHeightSpan;
import android.text.style.UpdateLayout;

import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Span изменения высоты строки.
 * Является заменой LineHeightSpan.Standard по причине отсутствия ниже API 29.
 *
 * @author am.boldinov
 */
public final class StandartLineHeightSpan implements LineHeightSpan, UpdateLayout {

    @Px
    private final int mSize;

    public StandartLineHeightSpan(@Px int size) {
        mSize = size;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        if (fm != null && ((Spanned) text).getSpanEnd(this) <= end) {
            SpannableUtil.setSpanHeight(mSize, 0, fm);
        }
    }
}
