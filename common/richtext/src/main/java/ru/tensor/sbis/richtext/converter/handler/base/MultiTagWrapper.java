package ru.tensor.sbis.richtext.converter.handler.base;

import androidx.annotation.NonNull;

/**
 * Интерфейс для обработки нескольких тегов внутри одного обработчика
 *
 * @author am.boldinov
 */
public interface MultiTagWrapper {

    /**
     * Устанавливает текущей тег, который должен начать обрабатываться
     */
    void setCurrentTag(@NonNull String tag);
}
