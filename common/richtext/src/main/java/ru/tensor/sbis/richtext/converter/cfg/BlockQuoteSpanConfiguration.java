package ru.tensor.sbis.richtext.converter.cfg;

import androidx.annotation.NonNull;
import ru.tensor.sbis.design.theme.res.SbisColor;
import ru.tensor.sbis.design.theme.res.SbisDimen;

/**
 * Конфигурация цитаты {@link ru.tensor.sbis.richtext.span.BlockQuoteSpan}
 *
 * @author am.boldinov
 */
public interface BlockQuoteSpanConfiguration extends BlockQuoteSenderConfiguration {

    /**
     * @return ширину линии цитаты
     */
    @NonNull
    SbisDimen getLineWidth();

    /**
     * @return цвет линии цитаты
     */
    @NonNull
    SbisColor getLineColor();

    /**
     * @return ссылка на ресурс с отступом для цитаты сверху и снизу
     */
    @NonNull
    SbisDimen getVerticalPadding();
}
