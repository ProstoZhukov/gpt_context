package ru.tensor.sbis.richtext.span.decoratedlink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import timber.log.Timber;

/**
 * Возможные типы ссылок
 * <p>
 * @author am.boldinov
 */
public enum UrlType {
    DEFAULT(0), // ссылка по умолчанию
    SEMANTIC_EXTERNAL(1), // есть семантические тэги и внешние ссылки
    NOT_SEMANTIC_EXTERNAL(2), // без семантических тэгов, для внешних ссылок
    DECORATED_INTERNAL(3), //  декорированная внутренняя ссылка
    HAS_DECORATED_DATA_INTERNAL(4), // есть данные для декорации, но ссылка не декорируется принудительно
    IMAGE(5); // ссылка на картинку

    private final int mValue;

    UrlType(int value) {
        mValue = value;
    }

    /**
     * Возвращает значение типа ссылки
     */
    public int getValue() {
        return mValue;
    }

    /**
     * Является ли тип ссылки внутренним (на внутренние ресурсы компании)
     */
    public boolean isInternal() {
        return getValue() == UrlType.DECORATED_INTERNAL.getValue()
                || getValue() == UrlType.HAS_DECORATED_DATA_INTERNAL.getValue();
    }

    /**
     * Формирует тип ссылки по значению
     */
    @NonNull
    public static UrlType fromValue(@Nullable String value) {
        if (value != null) {
            try {
                return fromValue(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                Timber.e(e, "Incorrect decorated url type");
            }
        }
        return DEFAULT;
    }

    /**
     * Формирует тип ссылки по значению
     */
    @NonNull
    public static UrlType fromValue(int value) {
        for (UrlType urlType : values()) {
            if (urlType.mValue == value) {
                return urlType;
            }
        }
        return DEFAULT;
    }
}
