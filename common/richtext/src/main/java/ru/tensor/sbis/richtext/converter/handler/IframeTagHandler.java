package ru.tensor.sbis.richtext.converter.handler;

import androidx.annotation.NonNull;

import android.text.Editable;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.attributes.ValueTagAttributes;
import ru.tensor.sbis.richtext.converter.handler.base.TagHandler;

/**
 * Обработчик тегов iframe
 *
 * @author am.boldinov
 */
public class IframeTagHandler implements TagHandler {

    @Nullable
    private TagHandler mUrlTagHandler;

    public IframeTagHandler() {

    }

    public IframeTagHandler(@Nullable TagHandler urlTagHandler) {
        mUrlTagHandler = urlTagHandler;
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        final String url = StringUtils.trimToEmpty(attributes.getValue("src"))
                .replaceFirst("^//", StringUtils.EMPTY)
                .replaceFirst("^www.", "https://");
        if (mUrlTagHandler != null) {
            mUrlTagHandler.onStartTag(stream, new ValueTagAttributes(url, attributes.getTag(), attributes.getParent()));
        }
        if (!url.isEmpty()) {
            stream.append(url);
        }
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        if (mUrlTagHandler != null) {
            mUrlTagHandler.onEndTag(stream);
        }
    }

    @Override
    public void recycle() {
        if (mUrlTagHandler != null) {
            mUrlTagHandler.recycle();
        }
    }
}
