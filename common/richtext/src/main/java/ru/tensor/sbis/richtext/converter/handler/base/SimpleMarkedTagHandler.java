package ru.tensor.sbis.richtext.converter.handler.base;

import androidx.annotation.NonNull;
import android.text.Editable;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;

/**
 * Простая реализация маркируемого обработчика
 *
 * @author am.boldinov
 */
public abstract class SimpleMarkedTagHandler extends MarkedTagHandler {

    @Override
    public final void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        mark(stream, createSpan());
    }

    @Override
    public final void onEndTag(@NonNull Editable stream) {
        span(stream, getSpanClass());
    }

    @Override
    public final void recycle() {

    }

    @NonNull
    protected abstract MarkSpan createSpan();

    @NonNull
    protected abstract Class<? extends MarkSpan> getSpanClass();
}
