package ru.tensor.sbis.richtext.converter.handler;

import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.css.CssClassSpanConverter;

/**
 * Обработчик тегов h1, h2, h3...
 *
 * @author am.boldinov
 */
public final class HTagHandler extends CssStyleTagHandler {

    @StyleRes
    private final int mStyle;

    public HTagHandler(@StyleRes int style, @NonNull CssClassSpanConverter converter) {
        super(converter);
        mStyle = style;
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        mark(stream, new MarkSpan.Header(attributes.getValue("id")));
        super.onStartTag(stream, attributes);
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        super.onEndTag(stream);
        span(stream, MarkSpan.Header.class);
    }

    @Override
    protected boolean isParagraphClass() {
        return true;
    }

    @Override
    protected int getCssStyle() {
        return mStyle;
    }
}
