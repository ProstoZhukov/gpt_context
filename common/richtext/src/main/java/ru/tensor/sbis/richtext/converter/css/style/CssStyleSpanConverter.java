package ru.tensor.sbis.richtext.converter.css.style;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.richtext.converter.MarkSpan;

/**
 * Конвертер css стиля в span
 *
 * @author am.boldinov
 */
public interface CssStyleSpanConverter {

    /**
     * Конвертирует атрибуты одного css стиля в span
     *
     * @param attrKey   атрибут стиля
     * @param attrValue значение атрибута стиля
     * @return span
     */
    @Nullable
    MarkSpan convert(@NonNull String attrKey, @NonNull String attrValue);

    /**
     * Конвертирует css стиль в набор span'ов
     *
     * @param style html строковое представление набора стилей, например
     *              color: #000000;font-size: 12px;
     * @return набор маркируемых span
     */
    @Nullable
    List<MarkSpan> convert(@NonNull String style);
}
