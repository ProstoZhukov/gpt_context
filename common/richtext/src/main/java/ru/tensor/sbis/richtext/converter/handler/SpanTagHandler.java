package ru.tensor.sbis.richtext.converter.handler;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.css.CssClassSpanConverter;
import ru.tensor.sbis.richtext.converter.css.LanguageTokenClassFactory;
import ru.tensor.sbis.richtext.converter.css.style.CssStyleSpanConverter;
import ru.tensor.sbis.richtext.converter.handler.base.SimpleCollectionMarkedTagHandler;

/**
 * Обработчик тега span
 *
 * @author am.boldinov
 */
public class SpanTagHandler extends SimpleCollectionMarkedTagHandler {

    @NonNull
    private final Context mContext;
    @NonNull
    private final CssStyleSpanConverter mStyleSpanConverter;
    @NonNull
    private final CssClassSpanConverter mClassSpanConverter;

    public SpanTagHandler(@NonNull Context context, @NonNull CssStyleSpanConverter styleSpanConverter,
                          @NonNull CssClassSpanConverter classSpanConverter) {
        mContext = context;
        mStyleSpanConverter = styleSpanConverter;
        mClassSpanConverter = classSpanConverter;
    }

    @NonNull
    @Override
    protected List<MarkSpan> createSpanCollection(@NonNull TagAttributes attributes) {
        if (attributes.isEmpty()) {
            return Collections.emptyList();
        }
        final List<MarkSpan> result = new ArrayList<>();
        final String styleAttr = attributes.getValue("style");
        if (styleAttr != null) {
            final List<MarkSpan> styles = mStyleSpanConverter.convert(styleAttr);
            if (styles != null) {
                result.addAll(styles);
            }
        }
        final String className = attributes.getValue("class");
        if (className != null) {
            // значения могут перечисляться через пробел
            final String[] values = className.split(StringUtils.SPACE);
            for (String value : values) {
                final List<MarkSpan> classes = mClassSpanConverter.convert(value);
                if (classes != null) {
                    result.addAll(classes);
                }
            }
            if (result.isEmpty()) { // токены идут одиночными классами
                final List<MarkSpan> tokens = LanguageTokenClassFactory.create(mContext, attributes, className);
                if (tokens != null) {
                    result.addAll(tokens);
                }
            }
        }
        return result;
    }
}
