package ru.tensor.sbis.richtext.converter.handler;

import androidx.annotation.NonNull;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleMarkedTagHandler;

/**
 * Обработчик тегов жирного текста: strong, b
 *
 * @author am.boldinov
 */
public class BoldTagHandler extends SimpleMarkedTagHandler {

    @NonNull
    @Override
    protected MarkSpan createSpan() {
        return new MarkSpan.Bold();
    }

    @NonNull
    @Override
    protected Class<? extends MarkSpan> getSpanClass() {
        return MarkSpan.Bold.class;
    }
}
