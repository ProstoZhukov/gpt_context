package ru.tensor.sbis.richtext.converter.handler;

import androidx.annotation.NonNull;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleMarkedTagHandler;

/**
 * Обработчик тегов зачеркивания: strike
 *
 * @author am.boldinov
 */
public class StrikeTagHandler extends SimpleMarkedTagHandler {

    @NonNull
    @Override
    protected MarkSpan createSpan() {
        return new MarkSpan.Strikethrough();
    }

    @NonNull
    @Override
    protected Class<? extends MarkSpan> getSpanClass() {
        return MarkSpan.Strikethrough.class;
    }
}
