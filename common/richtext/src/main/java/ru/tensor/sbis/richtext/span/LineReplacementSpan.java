package ru.tensor.sbis.richtext.span;

import android.text.style.ReplacementSpan;

import androidx.annotation.CallSuper;

/**
 * Реализация {@link ReplacementSpan} для ограничения размеров по ширине строки.
 *
 * @author am.boldinov
 */
public abstract class LineReplacementSpan extends ReplacementSpan {

    private int mMaxWidth = Integer.MAX_VALUE;

    /**
     * Устанавливает максимальную ширину для отрисовки
     */
    @CallSuper
    public void setMaxLineWidth(int maxSize) {
        mMaxWidth = maxSize;
    }

    protected final int getMaxLineWidth() {
        return mMaxWidth;
    }
}
