package ru.tensor.sbis.richtext.converter.css;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;

/**
 * Фабрика по созданию span'ов на основе поддерживаемых css классов стилизации блока с кодом.
 *
 * @author am.boldinov
 */
public class LanguageTokenClassFactory {

    /**
     * Создает набор span'ов для стилизации участка текста с кодом.
     *
     * @param context контекст приложения
     * @param attributes набор атрибутов, привязанных к текущему тегу
     * @param className название css класса
     * @return набор маркируемых span
     */
    @Nullable
    public static List<MarkSpan> create(@NonNull Context context, @NonNull TagAttributes attributes,
                                        @NonNull String className) {
        if (!className.startsWith("token")) {
            return null;
        }
        switch (className) {
            case "token comment":
            case "token prolog":
            case "token doctype":
            case "token cdata":
                return singleColorSpan(context, R.color.richtext_language_token_color_1);
            case "token punctuation":
                return singleColorSpan(context, R.color.richtext_language_token_color_2);
            case "token property":
            case "token tag":
            case "token boolean":
            case "token number":
            case "token constant":
            case "token symbol":
            case "token deleted":
                return singleColorSpan(context, R.color.richtext_language_token_color_3);
            case "token string":
                if (isParentClass(attributes, "language-css")) {
                    final List<MarkSpan> spans = new ArrayList<>(2);
                    spans.add(new MarkSpan.ForegroundColor(ContextCompat.getColor(context, R.color.richtext_language_token_color_5)));
                    spans.add(new MarkSpan.BackgroundColor(context, ContextCompat.getColor(context, R.color.richtext_language_token_operator_background_color)));
                    return spans;
                }
            case "token selector":
            case "token attr-name":
            case "token char":
            case "token builtin":
            case "token inserted":
                return singleColorSpan(context, R.color.richtext_language_token_color_4);
            case "token operator":
            case "token entity":
            case "token url":
                return singleColorSpan(context, R.color.richtext_language_token_color_5);
            case "token atrule":
            case "token attr-value":
            case "token keyword":
                return singleColorSpan(context, R.color.richtext_language_token_color_6);
            case "token function":
            case "token class-name":
                return singleColorSpan(context, R.color.richtext_language_token_color_7);
            case "token regex":
            case "token variable":
                return singleColorSpan(context, R.color.richtext_language_token_color_8);
            case "token important":
                final List<MarkSpan> spans = new ArrayList<>(2);
                spans.add(new MarkSpan.ForegroundColor(ContextCompat.getColor(context, R.color.richtext_language_token_color_8)));
                spans.add(new MarkSpan.Bold());
                return spans;
            case "token bold":
                return Collections.singletonList(new MarkSpan.Bold());
            case "token italic":
                return Collections.singletonList(new MarkSpan.Italic(context));
            default:
                return null;
        }
    }

    private static boolean isParentClass(@NonNull TagAttributes attributes, @NonNull String className) {
        final TagAttributes parent = attributes.getParent();
        if (parent != null) {
            if (Objects.equals(parent.getValue("class"), className)) {
                return true;
            } else {
                return isParentClass(parent, className);
            }
        }
        return false;
    }

    @NonNull
    private static List<MarkSpan> singleColorSpan(@NonNull Context context, @ColorRes int color) {
        return Collections.singletonList(new MarkSpan.ForegroundColor(
                ContextCompat.getColor(context, color)
        ));
    }
}
