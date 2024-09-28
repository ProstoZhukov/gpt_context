package ru.tensor.sbis.richtext.converter.css;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.StyleRes;
import ru.tensor.sbis.richtext.converter.MarkSpan;

/**
 * Конвертер css класса в span
 *
 * @author am.boldinov
 */
public interface CssClassSpanConverter {

    /**
     * Конвертирует css класс в набор span'ов
     *
     * @param cssClass строковое представление css класса
     * @return набор маркируемых span
     */
    @Nullable
    List<MarkSpan> convert(@NonNull String cssClass);

    /**
     * Конвертирует представление css класса виде ссылки на ресурс стиля в набор span'ов
     *
     * @param style ссылка на ресурс css класса
     * @return набор маркируемых span
     */
    @NonNull
    List<MarkSpan> convert(@StyleRes int style);
}
