package ru.tensor.sbis.richtext.converter.handler;

import android.content.Context;
import android.text.Editable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.cfg.style.InlineCodeStyle;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleCollectionMarkedTagHandler;
import ru.tensor.sbis.richtext.util.HtmlHelper;

/**
 * Обработчик тегов инлайн-выделения "Код".
 *
 * @author am.boldinov
 */
public class InlineCodeTagHandler extends SimpleCollectionMarkedTagHandler {

    @NonNull
    private final Context mContext;
    @NonNull
    private final InlineCodeStyle mStyle;

    public InlineCodeTagHandler(@NonNull SbisThemedContext context) {
        mContext = context;
        mStyle = new InlineCodeStyle(context);
    }

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        super.onStartTag(stream, attributes);
        stream.append(HtmlHelper.NBSP); // вместо ReplacementSpan отступа для увеличения производительности
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        stream.append(HtmlHelper.NBSP);
        super.onEndTag(stream);
    }

    @NonNull
    @Override
    protected List<MarkSpan> createSpanCollection(@NonNull TagAttributes attributes) {
        final List<MarkSpan> list = new ArrayList<>(2);
        list.add(new MarkSpan.BackgroundColor(mContext, mStyle.getBackgroundColor()));
        list.add(new MarkSpan.ForegroundColor(mStyle.getTextColor()));
        return list;
    }
}
