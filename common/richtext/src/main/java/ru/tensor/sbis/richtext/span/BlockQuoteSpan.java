package ru.tensor.sbis.richtext.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Span для установки стиля цитаты.
 *
 * @author am.boldinov
 */
public class BlockQuoteSpan implements LeadingMarginSpan, PrioritySpan {

    @NonNull
    private final Paint mPaint = new Paint();
    private final int mGapWidth;

    @Nullable
    private final BlockQuoteData mData;

    /**
     * @see #BlockQuoteSpan(int, float, int, BlockQuoteData)
     */
    public BlockQuoteSpan(int gapWidth, float lineWidth, @ColorInt int lineColor) {
        this(gapWidth, lineWidth, lineColor, null);
    }

    /**
     * Создает {@link BlockQuoteSpan} по параметрам.
     *
     * @param gapWidth размер отступа до основного текста
     * @param lineWidth ширина линии цитаты
     * @param lineColor цвет линии цитаты
     * @param data данные по цитате
     */
    public BlockQuoteSpan(int gapWidth, float lineWidth, @ColorInt int lineColor,
                          @Nullable BlockQuoteData data) {
        mGapWidth = gapWidth;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(lineColor);
        mData = data;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        x += SpannableUtil.LEADING_MARGIN_OFFSET_X;
        c.drawLine(x, top, x, bottom, mPaint);
    }

    @Override
    public int getPriority() {
        return LeadingMargin.BLOCK_QUOTE;
    }

    /**
     * Возвращает данные по цитате.
     */
    @Nullable
    public BlockQuoteData getData() {
        return mData;
    }
}
