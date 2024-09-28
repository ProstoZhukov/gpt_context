package ru.tensor.sbis.richtext.converter.handler.base;

import androidx.annotation.NonNull;

import android.text.Editable;

import ru.tensor.sbis.richtext.converter.TagAttributes;

/**
 * Потоковый обработчик тегов
 *
 * @author am.boldinov
 */
public interface TagHandler {

    /**
     * Событие об открытии тега
     *
     * @param stream     поток текста
     * @param attributes атрибуты тега
     */
    void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes);

    /**
     * Событие о закрытии тега
     *
     * @param stream поток текста
     */
    void onEndTag(@NonNull Editable stream);

    /**
     * Освобождает ресурсы обработчика для повторного переиспользования
     */
    void recycle();
}
