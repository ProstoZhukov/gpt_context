package ru.tensor.sbis.design.text_span.util;

import android.content.Context;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;

import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan;

/**
 * Use {@link FontUtilsKt} instead.
 */
@Deprecated
public class TextFormatUtils {

    /**
     * Обернуть исходную строку в указанный шрифт.
     */
    @Deprecated
    public static SpannableString getCustomFontSpannableString(@Nullable String text, @FontRes int fontRes, @NonNull Context context) {
        if (text == null) {
            return null;
        }
        SpannableString s = new SpannableString(text);
        s.setSpan(new CustomTypefaceSpan(ResourcesCompat.getFont(context, fontRes)), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }
}
