package ru.tensor.sbis.richtext.view.strategy.bounds;

import android.text.Editable;

import androidx.annotation.NonNull;

/**
 * Утилита для ручной трасформации границ спанов, которые являются EXCLUSIVE_EXCLUSIVE
 *
 * @author am.boldinov
 */
public interface SpanBoundsTransformer {

    /**
     * Необходимо вызывать перед добавлением текста
     *
     * @param text текущее состояние текста
     * @param position позиция добавления
     */
    void onBeforeTextInserted(@NonNull Editable text, int position);

    /**
     * Необходимо вызывать после добавления текста
     *
     * @param text обновленное состояние текста
     */
    void onAfterTextInserted(@NonNull Editable text);
}
