package ru.tensor.sbis.richtext.converter.cfg;

import androidx.annotation.NonNull;
import ru.tensor.sbis.design.theme.res.SbisColor;
import ru.tensor.sbis.design.theme.res.SbisDimen;
import ru.tensor.sbis.richtext.R;

/**
 * Используемая по умолчанию конфигурация отображения цитат
 *
 * @author am.boldinov
 */
public class DefaultBlockQuoteSpanConfiguration implements BlockQuoteSpanConfiguration {

    @NonNull
    @Override
    public SbisDimen getSenderTextSize() {
        return new SbisDimen.Attr(ru.tensor.sbis.design.R.attr.fontSize_m_scaleOff);
    }

    @NonNull
    @Override
    public SbisColor getSenderTextColor() {
        return new SbisColor.Attr(ru.tensor.sbis.design.R.attr.secondaryTextColor);
    }

    @NonNull
    @Override
    public SbisDimen getLineWidth() {
        return new SbisDimen.Res(R.dimen.richtext_block_quote_line_width);
    }

    @NonNull
    @Override
    public SbisColor getLineColor() {
        return new SbisColor.Attr(ru.tensor.sbis.design.R.attr.paletteColor12_3);
    }

    @NonNull
    @Override
    public SbisDimen getVerticalPadding() {
        return new SbisDimen.Res(R.dimen.richtext_block_quote_span_vertical_padding);
    }
}
