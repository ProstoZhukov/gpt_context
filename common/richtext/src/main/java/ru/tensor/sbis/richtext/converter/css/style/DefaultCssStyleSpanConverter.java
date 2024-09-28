package ru.tensor.sbis.richtext.converter.css.style;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.util.HtmlHelper;

/**
 * Стандартный конвертер css стиля в span
 *
 * @author am.boldinov
 */
public class DefaultCssStyleSpanConverter implements CssStyleSpanConverter {

    @NonNull
    private final Context mContext;

    public DefaultCssStyleSpanConverter(@NonNull Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public MarkSpan convert(@NonNull String attrKey, @NonNull String attrValue) {
        final CssStyle style = CssStyle.fromValue(attrKey, attrValue);
        if (style != null) {
            switch (style) {
                case UNDERLINE:
                    return new MarkSpan.Underline();
                case STRIKE:
                    return new MarkSpan.Strikethrough();
                case COLOR:
                    final Integer color = HtmlHelper.parseColor(attrValue);
                    if (color != null) {
                        return new MarkSpan.ForegroundColor(color);
                    } else {
                        return null;
                    }
                case FONT_SIZE:
                    final Integer fontSize = HtmlHelper.parseFontSize(mContext, attrValue);
                    if (fontSize != null) {
                        return new MarkSpan.FontSize(fontSize);
                    } else {
                        return null;
                    }
                case BOLD:
                    return new MarkSpan.Bold();
                default:
                    throw new UnsupportedOperationException("Unknown css style");
            }
        }
        return null;
    }

    @Nullable
    @Override
    public List<MarkSpan> convert(@NonNull String style) {
        final String[] globalAttrs = style.split(";");
        if (globalAttrs.length > 0) {
            final List<MarkSpan> spans = new ArrayList<>(globalAttrs.length);
            for (String globalAttr : globalAttrs) {
                final String[] attrs = globalAttr.split(":");
                if (attrs.length == 2) {
                    final String key = attrs[0].trim();
                    final String value = attrs[1].trim();
                    final MarkSpan span = convert(key, value);
                    if (span != null) {
                        spans.add(span);
                    }
                }
            }
            return spans;
        }
        return null;
    }
}
