package ru.tensor.sbis.richtext.converter;

import androidx.annotation.NonNull;
import android.text.Spannable;

/**
 * Потоковый обработчик тегов (SAX парсер)
 * <p>
 * @author am.boldinov
 */
public interface TagStreamProcessor {

    /**
     * Возвращает результат обработки тегов, изменяемый Spannable
     */
    @NonNull
    Spannable buildResult();

    /**
     * Событие о начале обработки документа
     */
    void onDocumentStart();

    /**
     * Событие о завершении обработки документа
     */
    void onDocumentEnd();
}
