package ru.tensor.sbis.richtext.converter.handler;

import android.text.Editable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.css.CssClassSpanConverter;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleCollectionMarkedTagHandler;
import ru.tensor.sbis.richtext.util.HtmlHelper;

/**
 * Обработчик тегов, которые представляют собой css класс
 *
 * @author am.boldinov
 */
abstract class CssStyleTagHandler extends SimpleCollectionMarkedTagHandler {

    @NonNull
    private final CssClassSpanConverter mConverter;

    CssStyleTagHandler(@NonNull CssClassSpanConverter converter) {
        mConverter = converter;
    }

    @NonNull
    @Override
    protected List<MarkSpan> createSpanCollection(@NonNull TagAttributes attributes) {
        final List<MarkSpan> result = mConverter.convert(getCssStyle());
        if (isParagraphClass()) {
            result.add(new MarkSpan.Paragraph());
        }
        return result;
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        super.onEndTag(stream);
        if (isParagraphClass()) {
            HtmlHelper.appendLineBreakIgnoreSpace(stream, 1);
        }
    }

    /**
     * Возвращает необходимость выделить содержимое тега в виде параграфа
     */
    protected boolean isParagraphClass() {
        return false;
    }

    @StyleRes
    protected abstract int getCssStyle();
}
