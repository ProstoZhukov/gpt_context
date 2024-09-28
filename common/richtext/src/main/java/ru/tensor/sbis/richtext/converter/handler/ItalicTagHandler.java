package ru.tensor.sbis.richtext.converter.handler;

import android.content.Context;

import androidx.annotation.NonNull;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleMarkedTagHandler;

/**
 * Обработчик тегов наклонного текста: em
 *
 * @author am.boldinov
 */
public class ItalicTagHandler extends SimpleMarkedTagHandler {

    @NonNull
    private final Context mContext;

    public ItalicTagHandler(@NonNull Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    protected MarkSpan createSpan() {
        return new MarkSpan.Italic(mContext);
    }

    @NonNull
    @Override
    protected Class<? extends MarkSpan> getSpanClass() {
        return MarkSpan.Italic.class;
    }
}
