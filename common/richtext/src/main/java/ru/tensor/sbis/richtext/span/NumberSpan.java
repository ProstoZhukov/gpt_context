package ru.tensor.sbis.richtext.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;

import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Span для установки цифры в начале абзаца
 *
 * @author am.boldinov
 */
public class NumberSpan implements LeadingMarginSpan, PrioritySpan {

    @NonNull
    private final String mNumber;
    private final int mGapWidth;
    @NonNull
    private final NumberSpanStyle mStyle;

    /**
     * Создает {@link NumberSpan} по параметрам
     *
     * @param gapWidth размер отступа до основного текста
     * @param number число
     * @param textSize размер текста числа
     */
    @SuppressWarnings("WeakerAccess")
    public NumberSpan(int gapWidth, int number, float textSize, @NonNull NumberSpanStyle style) {
        mNumber = Integer.toString(number).concat(".");
        final TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        mGapWidth = gapWidth == 0 ? 0 : gapWidth + (int) textPaint.measureText(mNumber);
        mStyle = style;
    }

    /**
     * Создает {@link NumberSpan} по параметрам
     *
     * @param context контекст приложения
     * @param gapWidth размер отступа до основного текста
     * @param number число
     */
    public NumberSpan(@NonNull Context context, int gapWidth,
                      int number, @NonNull NumberSpanStyle style) {
        this(context, gapWidth, number, R.dimen.richtext_number_span_text_size, style);
    }

    /**
     * Создает {@link NumberSpan} по параметрам
     *
     * @param context контекст приложения
     * @param gapWidth размер отступа до основного текста
     * @param number число
     * @param textSize размер текста числа
     */
    public NumberSpan(@NonNull Context context, int gapWidth, int number,
                      @DimenRes int textSize, @NonNull NumberSpanStyle style) {
        this(gapWidth, number, context.getResources().getDimensionPixelSize(textSize), style);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline,
                                  int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        if (mStyle != NumberSpanStyle.NONE) {
            if (((Spanned) text).getSpanStart(this) == start) {
                c.drawText(mNumber, x + SpannableUtil.LEADING_MARGIN_OFFSET_X, baseline, p);
            }
        }
    }


    @Override
    public int getPriority() {
        if (mStyle == NumberSpanStyle.NONE) {
            return LeadingMargin.ENUMERATION_NONE;
        }
        return LeadingMargin.ENUMERATION;
    }
}
