package ru.tensor.sbis.richtext.converter.cfg;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.converter.css.CssClassSpanConverter;
import ru.tensor.sbis.richtext.converter.css.style.CssStyleSpanConverter;

/**
 * Конфигурация css стилей для возможности предоставить кастомный стиль на прикладной уровне
 *
 * @author am.boldinov
 */
public interface CssConfiguration {

    /**
     * Возвращает палитру цветов текста в виде ресурса-массива, пример:
     * <array name="my_text_color_palette_1">
     * <item>@color/text_color_black_1</item>
     * <item>@color/text_color_accent_3</item>
     * <item>#9122BF</item>
     * <item>#808080</item>
     * </array>
     *
     * Позиция цвета должна совпадать с позицией на портале.
     * Существуют предустановленные палитры с префиксом richtext_css_class_text_color_palette.
     *
     * @return ссылка на ресурс массива с цветами текста R.array
     */
    @ArrayRes
    int getTextColorPalette();

    /**
     * Возвращает палитру с фоновыми цветами для текста в виде ресурса-массива, пример:
     * <array name="my_background_color_palette_1">
     * <item>#ebeced</item>
     * <item>#e9e5e3</item>
     * <item>#9122BF</item>
     * <item>#808080</item>
     * </array>
     *
     * Позиция цвета должна совпадать с позицией на портале.
     * Существуют предустановленные палитры с префиксом richtext_css_class_background_color_palette.
     *
     * @return ссылка на ресурс массива с цветами фона R.array
     */
    @ArrayRes
    int getBackgroundColorPalette();

    /**
     * @return конвертер для преобразования строкового представления css стиля в span
     */
    @NonNull
    CssStyleSpanConverter provideStyleConverter(@NonNull Context context);

    /**
     * @return конвертер для преобразования строкового представления css класса в span
     */
    @NonNull
    CssClassSpanConverter provideClassConverter(@NonNull Context context);
}
