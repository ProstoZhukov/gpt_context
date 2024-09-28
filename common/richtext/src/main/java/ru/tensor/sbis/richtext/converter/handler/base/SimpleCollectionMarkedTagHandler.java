package ru.tensor.sbis.richtext.converter.handler.base;

import android.text.Editable;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Простая реализация маркируемого обработчика для установки нескольких спанов одновременно на один тег
 *
 * @author am.boldinov
 */
public abstract class SimpleCollectionMarkedTagHandler extends MarkedTagHandler {

    private int mStartDomPosition;

    @CallSuper
    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        mStartDomPosition++;
        final List<MarkSpan> collection = createSpanCollection(attributes);
        for (MarkSpan span : collection) {
            span.setDomPosition(this, mStartDomPosition);
            mark(stream, span);
        }
    }

    @CallSuper
    @Override
    public void onEndTag(@NonNull Editable stream) {
        final MarkSpan[] spans = stream.getSpans(0, stream.length(), MarkSpan.class);
        for (MarkSpan span : spans) {
            if (span.containsDomPosition(this, mStartDomPosition)) {
                SpannableUtil.setSpanFromMark(stream, span, span.getRealSpan());
            }
        }
        mStartDomPosition--;
    }

    @CallSuper
    @Override
    public void recycle() {
        mStartDomPosition = 0;
    }

    @NonNull
    protected abstract List<MarkSpan> createSpanCollection(@NonNull TagAttributes attributes);
}
