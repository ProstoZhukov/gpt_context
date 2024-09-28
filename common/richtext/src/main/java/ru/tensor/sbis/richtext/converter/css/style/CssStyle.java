package ru.tensor.sbis.richtext.converter.css.style;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Поддерживаемые css стили
 *
 * @author am.boldinov
 */
enum CssStyle {

    UNDERLINE("text-decoration", "underline"),
    STRIKE("text-decoration", "line-through"),
    COLOR("color", null),
    FONT_SIZE("font-size", null),
    BOLD("font-weight", "bold");

    @NonNull
    private final String mAttr;
    @Nullable
    private final String mValue;

    CssStyle(@NonNull String attr, @Nullable String value) {
        mAttr = attr;
        mValue = value;
    }

    /**
     * Получить стиль по параметрам
     * @param key название стиля
     * @param value атрибут
     */
    @Nullable
    public static CssStyle fromValue(@NonNull String key, @Nullable String value) {
        for (CssStyle cssStyle : values()) {
            if (cssStyle.mAttr.equalsIgnoreCase(key) && (cssStyle.mValue == null || cssStyle.mValue.equalsIgnoreCase(value))) {
                return cssStyle;
            }
        }
        return null;
    }
}
