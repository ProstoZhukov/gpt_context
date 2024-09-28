package ru.tensor.sbis.richtext.converter.handler.postprocessor;

import androidx.annotation.NonNull;
import android.text.Editable;

/**
 * Постобработчик для установки дополнительных спанов или модификации текста
 * <p>
 * @author am.boldinov
 */
public interface SpanPostprocessor {

    /**
     * Вызывается конвертером после завершения потоковой обработки тегов
     *
     * @param text стилизованный текст
     */
    void process(@NonNull Editable text);

}
