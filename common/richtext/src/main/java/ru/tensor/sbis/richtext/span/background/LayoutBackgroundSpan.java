package ru.tensor.sbis.richtext.span.background;

import android.graphics.Canvas;
import android.text.Layout;
import android.text.style.UpdateAppearance;

import androidx.annotation.NonNull;

/**
 * Span для отрисовки фона текста.
 *
 * @author am.boldinov
 */
public interface LayoutBackgroundSpan extends UpdateAppearance {

    /**
     * Рисует фон. Вызывается перед отрисовкой текста.
     *
     * @param canvas канвас всего {@link android.widget.TextView}, важно учитывать позиции Span
     *               при отрисовке на конкретных строках текста
     * @param layout layout для получения координат отрисовки внутри текста
     * @param start позиция начала span в тексте
     * @param end позиция конца span в тексте
     */
    void draw(@NonNull Canvas canvas, @NonNull Layout layout, int start, int end);

}
