package ru.tensor.sbis.design.text_span.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.style.ReplacementSpan;

/**
 * Спан для усечения длины текста шириной канваса для отрисовки.
 * Поддерживает оптимизацию посредством кеширования в случае, если спан
 * наложен на часть текста, не содержащую переноса строки. Для включения
 * необходимо передать флаг {@link #mCacheEnabled} = true.
 * Так же для корректной работы кеширования необходимо, чтобы данный
 * спан единовременно отображался только в одной вью.
 *
 * @author am.boldinov
 */
public class EllipsizeLineSpan extends ReplacementSpan {
    private final static String CHAR_ELLIPSIS = "\u2026";
    private final Rect mBounds = new Rect();
    private final boolean mCacheEnabled;

    // Кешированные параметры для ellipsize
    private CharSequence mText;
    private float mWidth = -1;
    private int mStart = -1;
    private int mEnd = -1;
    private int mEllipsisEnd = -1;
    private float mTextWidth = -1;

    public EllipsizeLineSpan(boolean enableCache) {
        mCacheEnabled = enableCache;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        canvas.getClipBounds(mBounds);
        final float width = mBounds.right - x;
        float textWidth = paint.measureText(text, start, end);
        if (textWidth <= width) {
            canvas.drawText(text, start, end, x, y, paint);
        } else {
            if (!mCacheEnabled || mText != text || width != mWidth || start != mStart || end != mEnd) {
                // Вычисляем заново ellipsis
                mText = text;
                mWidth = width;
                mStart = start;
                mEnd = end;
                float ellipsisWidth = paint.measureText(CHAR_ELLIPSIS);
                end = start + paint.breakText(text, start, end, true, width - ellipsisWidth, null);
                while (end > start && text.charAt(end - 1) == ' ') { // Пропускаем пробелы в конце строки
                    --end;
                }
                mEllipsisEnd = end;
                mTextWidth = paint.measureText(text, mStart, mEllipsisEnd);
            }
            canvas.drawText(text, mStart, mEllipsisEnd, x, y, paint);
            canvas.drawText(CHAR_ELLIPSIS, x + mTextWidth, y, paint);
        }
    }

}
