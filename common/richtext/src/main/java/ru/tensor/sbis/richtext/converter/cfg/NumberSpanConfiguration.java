package ru.tensor.sbis.richtext.converter.cfg;

import androidx.annotation.DimenRes;

/**
 * Конфигурация чисел для тегов перечисления {@link ru.tensor.sbis.richtext.span.NumberSpan}
 *
 * @author am.boldinov
 */
public interface NumberSpanConfiguration {

    /**
     * @return размер текста числа
     */
    @DimenRes
    int getTextSize();
}
