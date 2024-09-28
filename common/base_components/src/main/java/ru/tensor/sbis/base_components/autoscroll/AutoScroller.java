package ru.tensor.sbis.base_components.autoscroll;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Интерфейс авто-скроллера для списка.
 *
 * @author am.boldinov
 */
@SuppressWarnings("rawtypes")
public interface AutoScroller {

    /**
     * Подготовиться к изменению контента.
     * @param beforeContent - контент до изменения
     */
    void onBeforeContentChanged(@Nullable List beforeContent);

    /**
     * Обработать изменение контента.
     * @param afterContent - контент после изменения
     */
    void onAfterContentChanged(@Nullable List afterContent);

    /**
     * Обработать добавление элементов в контент
     * @param position - позиция добавления
     * @param count - количество новых элементов
     */
    @SuppressWarnings("unused")
    void onContentRangeInserted(int position, int count);

}
