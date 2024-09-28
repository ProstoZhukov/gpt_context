package ru.tensor.sbis.richtext.converter.cfg;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.R;
import ru.tensor.sbis.richtext.converter.css.CssClassSpanConverter;
import ru.tensor.sbis.richtext.converter.css.DefaultCssClassSpanConverter;
import ru.tensor.sbis.richtext.converter.css.style.CssStyleSpanConverter;
import ru.tensor.sbis.richtext.converter.css.style.DefaultCssStyleSpanConverter;

/**
 * Используемая по умолчанию конфигурация css стилей
 *
 * @author am.boldinov
 */
public class DefaultCssConfiguration implements CssConfiguration {

    @ArrayRes
    @Override
    public int getTextColorPalette() {
        return R.array.richtext_css_class_text_color_palette_1;
    }

    @Override
    public int getBackgroundColorPalette() {
        return R.array.richtext_css_class_background_color_palette_1;
    }

    @NonNull
    @Override
    public CssStyleSpanConverter provideStyleConverter(@NonNull Context context) {
        return new DefaultCssStyleSpanConverter(context);
    }

    @NonNull
    @Override
    public CssClassSpanConverter provideClassConverter(@NonNull Context context) {
        return new DefaultCssClassSpanConverter(context, getTextColorPalette(), getBackgroundColorPalette());
    }
}
