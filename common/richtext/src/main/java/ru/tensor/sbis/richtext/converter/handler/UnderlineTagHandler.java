package ru.tensor.sbis.richtext.converter.handler;

import androidx.annotation.NonNull;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleMarkedTagHandler;

/**
 * Обработчик тегов подчеркивания: u
 *
 * @author am.boldinov
 */
public class UnderlineTagHandler extends SimpleMarkedTagHandler {

    @NonNull
    @Override
    protected MarkSpan createSpan() {
        return new MarkSpan.Underline();
    }

    @NonNull
    @Override
    protected Class<? extends MarkSpan> getSpanClass() {
        return MarkSpan.Underline.class;
    }
}
