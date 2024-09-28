package ru.tensor.sbis.richtext.converter.handler.base;

import android.text.Editable;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor;

/**
 * Обработчик тега, который является оберткой над несколькими обработчиками.
 * Используется в случае, если на какой-либо тег необходимо навесить несколько независимых обработчиков.
 *
 * @author am.boldinov
 */
public final class ComposeTagHandler implements TagHandler, MultiTagWrapper, SpanPostprocessor {

    @NonNull
    private final TagHandler[] mHandlers;

    public ComposeTagHandler(@NonNull TagHandler... handlers) {
        mHandlers = handlers;
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        for (TagHandler handler : mHandlers) {
            handler.onStartTag(stream, attributes);
        }
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        for (TagHandler handler : mHandlers) {
            handler.onEndTag(stream);
        }
    }

    @Override
    public void recycle() {
        for (TagHandler handler : mHandlers) {
            handler.recycle();
        }
    }

    @Override
    public void setCurrentTag(@NonNull String tag) {
        for (TagHandler handler : mHandlers) {
            if (handler instanceof MultiTagWrapper) {
                ((MultiTagWrapper) handler).setCurrentTag(tag);
            }
        }
    }

    @Override
    public void process(@NonNull Editable text) {
        for (TagHandler handler : mHandlers) {
            if (handler instanceof SpanPostprocessor) {
                ((SpanPostprocessor) handler).process(text);
            }
        }
    }
}
