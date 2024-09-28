package ru.tensor.sbis.richtext.converter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.text.Spannable;

import ru.tensor.sbis.richtext.converter.cfg.Configuration;
import ru.tensor.sbis.richtext.converter.handler.base.TagHandler;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor;

/**
 * Конвертер богатого текста
 * <p>
 * @author am.boldinov
 */
public interface RichTextConverter {

    /**
     * Конвертирует текст с набором тегов в spannable строку
     */
    @WorkerThread
    @NonNull
    Spannable convert(@NonNull String source);

    /**
     * Задает настройки конвертирования
     */
    void setConfiguration(@Nullable Configuration configuration);

    /**
     * Задает свой обработчик тега
     */
    void setCustomTagHandler(@NonNull String tag, @Nullable TagHandler tagHandler);

    /**
     * Задает свой обработчик тега для классов внутри тега
     *
     * @param className имя класа
     * @param tagHandler обработчик
     */
    void setCssClassTagHandler(@NonNull String tag, @NonNull String className, @Nullable TagHandler tagHandler);

    /**
     * Регистрирует постобработчик для установки дополнительных спанов или модификации текста
     * после завершения потоковой обработки тегов
     */
    void addPostprocessor(@NonNull SpanPostprocessor postprocessor);
}
