package ru.tensor.sbis.richtext.converter.cfg;

import ru.tensor.sbis.richtext.R;

/**
 * Используемая по умолчанию конфигурация чисел для тегов перечисления {@link ru.tensor.sbis.richtext.span.NumberSpan}
 *
 * @author am.boldinov
 */
public class DefaultNumberSpanConfiguration implements NumberSpanConfiguration {

    @Override
    public int getTextSize() {
        return R.dimen.richtext_number_span_text_size;
    }
}
